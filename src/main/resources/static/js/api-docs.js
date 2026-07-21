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
        const response = await fetch('/api/markdown/files-tree', {
            headers: {
                'Accept': 'application/json; charset=utf-8'
            }
        });
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

        // Загружаем start_page.md, если он есть, иначе первый файл
        const startPageLink = Array.from(document.querySelectorAll('.file-list a')).find(
            link => link.dataset.file === 'start_page.md'
        );

        if (startPageLink) {
            loadFile('start_page.md');
            startPageLink.classList.add('active');
        } else {
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
        }

    } catch (error) {
        console.error('Ошибка загрузки списка файлов:', error);
        document.getElementById('file-list').innerHTML = '<p style="color: red;">❌ Ошибка загрузки списка файлов</p>';
    }
}

function loadFile(filename) {
    currentFile = filename;
    document.getElementById('md-content').innerHTML = '<div class="loading">Загрузка...</div>';

    fetch(`/markdown/${filename}`, {
        headers: {
            'Accept': 'text/markdown; charset=utf-8'
        }
    })
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

// Конвертация Mermaid диаграмм в текстовое описание для Word
function convertMermaidForWord(html) {
    return html.replace(
        /<div class="mermaid">([\s\S]*?)<\/div>/g,
        (match, diagramCode) => {
            // Определяем тип диаграммы
            let diagramType = 'Диаграмма';
            if (diagramCode.includes('sequenceDiagram')) {
                diagramType = 'Диаграмма последовательности';
            } else if (diagramCode.includes('flowchart') || diagramCode.includes('graph')) {
                diagramType = 'Блок-схема';
            } else if (diagramCode.includes('stateDiagram')) {
                diagramType = 'Диаграмма состояний';
            } else if (diagramCode.includes('erDiagram')) {
                diagramType = 'ER-диаграмма';
            }

            return `
                <div style="background: #e8f5e9; border: 1px solid #4CAF50; padding: 12px; margin: 15px 0; border-radius: 5px;">
                    <p style="font-weight: bold; margin: 0 0 8px 0; color: #2e7d32;">📊 ${diagramType}</p>
                    <p style="margin: 0 0 8px 0; color: #555;">Диаграмма не отображается в Word версии документа.</p>
                    <p style="margin: 0;">
                        🔗 <a href="${window.location.href}" style="color: #2e7d32;">Открыть в браузере для просмотра диаграммы</a>
                    </p>
                </div>
            `;
        }
    );
}

// Скачивание текущего MD файла в Word
function downloadAsWord() {
    if (!currentFile) {
        alert('Нет открытого документа');
        return;
    }

    // Предупреждение перед скачиванием
    if (!confirm('⚠️ ВНИМАНИЕ!\n\nДиаграммы НЕ будут сохранены в Word.\n\nРекомендуем использовать "PDF / Печать" (Ctrl+P) — диаграммы сохранятся.\n\nПродолжить скачивание в Word?')) {
        return;
    }

    let content = document.getElementById('md-content').innerHTML;
    const title = currentFile.replace('.md', '');

    content = convertMermaidForWord(content);
    content = content.replace(/<script[\s\S]*?<\/script>/gi, '');

    const wordHtml = `<!DOCTYPE html>
    <html>
    <head>
        <meta charset="UTF-8">
        <title>${title}</title>
        <style>
            body { font-family: 'Times New Roman', Times, serif; margin: 2cm; line-height: 1.5; }
            h1, h2, h3, h4 { color: #2c3e50; margin-top: 20px; }
            h1 { font-size: 24pt; }
            h2 { font-size: 18pt; }
            h3 { font-size: 16pt; }
            table { border-collapse: collapse; width: 100%; margin: 16px 0; }
            th, td { border: 1px solid #ddd; padding: 8px; text-align: left; vertical-align: top; }
            th { background-color: #f2f2f2; font-weight: bold; }
            code { background-color: #f4f4f4; padding: 2px 5px; border-radius: 4px; font-family: 'Courier New', monospace; font-size: 10pt; }
            pre { background-color: #f4f4f4; padding: 10px; border-radius: 5px; overflow-x: auto; font-family: 'Courier New', monospace; font-size: 10pt; white-space: pre-wrap; }
            blockquote { border-left: 3px solid #4CAF50; margin: 10px 0; padding-left: 15px; color: #666; }
            hr { border: none; border-top: 1px solid #ddd; margin: 20px 0; }
            .mermaid { display: none; }
        </style>
    </head>
    <body>
        <h1>${title}</h1>
        <hr>
        ${content}
        <hr>
        <p style="color: #999; font-size: 10pt;">
            📄 Скачано из ORACLE REST API Documentation<br>
            📅 Дата: ${new Date().toLocaleDateString('ru-RU')}<br>
            🔗 Оригинал: ${window.location.href}
        </p>
    </body>
    </html>`;

    const blob = new Blob([wordHtml], { type: 'application/msword' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${title.replace(/[\\/:*?"<>|]/g, '_')}.doc`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
}

// Печать в PDF (сохраняет диаграммы)
function printToPdf() {
    // Временно скрываем всё лишнее (на случай, если CSS не сработает)
    const sidebar = document.querySelector('.sidebar');
    const buttons = document.querySelectorAll('.action-btn, .download-btn, .env-switch, .env-info, .links, .mobile-toggle');

    const originalDisplay = [];

    // Сохраняем оригинальные стили и скрываем
    buttons.forEach((el, index) => {
        if (el) {
            originalDisplay[index] = el.style.display;
            el.style.display = 'none';
        }
    });

    if (sidebar) {
        originalDisplay['sidebar'] = sidebar.style.display;
        sidebar.style.display = 'none';
    }

    // Вызываем печать
    window.print();

    // Восстанавливаем всё обратно
    setTimeout(() => {
        buttons.forEach((el, index) => {
            if (el) {
                el.style.display = originalDisplay[index] || '';
            }
        });
        if (sidebar) {
            sidebar.style.display = originalDisplay['sidebar'] || '';
        }
    }, 100);
}
// Показываем кнопку при загрузке файла (обёртка)
const originalLoadFile = loadFile;
window.loadFile = function(filename) {
    originalLoadFile(filename);
    const downloadBtn = document.getElementById('downloadWordBtn');
    if (downloadBtn) {
        downloadBtn.style.display = 'flex';
    }
};

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

// Инициализация кнопки скачивания Word
function initDownloadButton() {
    const downloadBtn = document.getElementById('downloadWordBtn');
    if (downloadBtn) {
        downloadBtn.addEventListener('click', downloadAsWord);
    }
}

// Инициализация кнопки печати PDF
function initPrintButton() {
    const printBtn = document.getElementById('printPdfBtn');
    if (printBtn) {
        printBtn.addEventListener('click', printToPdf);
    }
}

// Мобильное меню и инициализация кнопок
document.addEventListener('DOMContentLoaded', () => {
    const mobileToggle = document.getElementById('mobileToggle');
    const sidebar = document.getElementById('sidebar');
    if (mobileToggle && sidebar) {
        mobileToggle.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
        });
    }

    initEnvButtons();
    initDownloadButton();
    initPrintButton();
});

// Экспортируем функции
window.init = init;
window.loadFile = loadFile;
window.switchEnvironment = switchEnvironment;
window.downloadAsWord = downloadAsWord;
window.printToPdf = printToPdf;