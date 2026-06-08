mermaid.initialize({ startOnLoad: false, theme: 'default', securityLevel: 'loose' });

let currentFile = null;

// Текущий хост из Thymeleaf (сервер)
let currentHost = {
    host: '',
    port: '',
    sid: ''
};

// Получаем данные из window.currentHost (переданного из Thymeleaf)
if (window.currentHost) {
    currentHost = window.currentHost;
    console.log('Current host loaded:', currentHost);
}

// Обновление отображения хоста
function updateHostDisplay() {
    const hostEl = document.getElementById('hostValue');
    const portEl = document.getElementById('portValue');
    const sidEl = document.getElementById('sidValue');

    if (hostEl) hostEl.textContent = currentHost.host || '-';
    if (portEl) portEl.textContent = currentHost.port || '-';
    if (sidEl) sidEl.textContent = currentHost.sid || '-';
}

// Переключение окружения (перезагрузка страницы)
function switchEnvironment(host, port) {
    const currentUrl = new URL(window.location.href);
    currentUrl.host = `${host}:${port}`;
    window.location.href = currentUrl.toString();
}

// Загрузка списка файлов
async function loadFileList() {
    try {
        const response = await fetch('/api/markdown/files-tree');
        const tree = await response.json();

        let html = '';

        if (tree.root && tree.root.length) {
            tree.root.forEach(file => {
                html += `
                    <li>
                        <a href="#" data-file="${file.path}">
                            📄 ${file.name}
                        </a>
                    </li>
                `;
            });
            delete tree.root;
        }

        for (const [folder, files] of Object.entries(tree)) {
            html += `
                <div class="folder" style="margin-top: 10px;">
                    <div class="folder-title" style="cursor: pointer; padding: 5px 0; font-weight: bold; color: #666;">
                        📁 ${folder} <span style="font-size: 11px;">▶</span>
                    </div>
                    <ul class="folder-content" style="list-style: none; padding-left: 20px; margin: 5px 0; display: none;">
                        ${files.map(file => `
                            <li>
                                <a href="#" data-file="${file.path}">
                                    📄 ${file.name}
                                </a>
                            </li>
                        `).join('')}
                    </ul>
                </div>
            `;
        }

        document.getElementById('file-list').innerHTML = `<ul style="list-style: none; padding: 0;">${html}</ul>`;

        // Обработчики для папок
        document.querySelectorAll('.folder-title').forEach(title => {
            title.addEventListener('click', (e) => {
                e.stopPropagation();
                const content = title.nextElementSibling;
                const isCollapsed = content.style.display === 'none';
                content.style.display = isCollapsed ? 'block' : 'none';
                title.querySelector('span').textContent = isCollapsed ? '▼' : '▶';
            });
        });

        // Обработчики для файлов
        document.querySelectorAll('.file-list a').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const file = link.dataset.file;
                if (file) {
                    loadFile(file);
                    document.querySelectorAll('.file-list a').forEach(a => a.classList.remove('active'));
                    link.classList.add('active');

                    if (window.innerWidth <= 768) {
                        document.getElementById('sidebar')?.classList.add('collapsed');
                    }
                }
            });
        });

        // Загружаем первый файл
        let firstFile = null;
        if (tree.root && tree.root.length) {
            firstFile = tree.root[0].path;
        } else {
            const firstFolder = Object.values(tree)[0];
            if (firstFolder && firstFolder.length) {
                firstFile = firstFolder[0].path;
            }
        }
        if (firstFile) {
            loadFile(firstFile);
            const firstLink = document.querySelector(`.file-list a[data-file="${firstFile}"]`);
            if (firstLink) firstLink.classList.add('active');
        }

    } catch (error) {
        console.error('Ошибка загрузки списка файлов:', error);
        document.getElementById('file-list').innerHTML = '<p style="color: red;">❌ Ошибка загрузки списка файлов</p>';
    }
}

function loadFile(filename) {
    currentFile = filename;
    document.getElementById('md-content').innerHTML = '<div class="loading">Загрузка...</div>';

    fetch(`/markdown/${filename}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Файл ${filename} не найден`);
            }
            return response.text();
        })
        .then(md => {
            let html = marked.parse(md);
            html = html.replace(/<pre><code class="language-mermaid">([\s\S]*?)<\/code><\/pre>/g, '<div class="mermaid">$1</div>');
            document.getElementById('md-content').innerHTML = html;

            setTimeout(() => {
                mermaid.run({ querySelector: '.mermaid' }).catch(err => console.warn('Mermaid error:', err));
            }, 50);
        })
        .catch(error => {
            document.getElementById('md-content').innerHTML = `
                <div style="color: red; padding: 20px; text-align: center;">
                    <p>❌ ${error.message}</p>
                </div>
            `;
        });
}

function init() {
    updateHostDisplay();
    loadFileList();
}

// Инициализация кнопок переключения окружений
function initEnvButtons() {
    const envButtons = document.querySelectorAll('.env-link');
    envButtons.forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const host = btn.dataset.host;
            const port = btn.dataset.port;
            if (host && port) {
                switchEnvironment(host, port);
            }
        });
    });
}

// Мобильное меню
document.addEventListener('DOMContentLoaded', () => {
    const mobileToggle = document.getElementById('mobileToggle');
    const sidebar = document.getElementById('sidebar');
    if (mobileToggle && sidebar) {
        mobileToggle.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
        });
    }

    // Инициализация кнопок окружений после загрузки DOM
    initEnvButtons();
});

// Экспортируем функции для использования в HTML
window.init = init;
window.loadFile = loadFile;
window.switchEnvironment = switchEnvironment;