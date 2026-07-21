FROM eclipse-temurin:21-jre-alpine

# Установка русской локали
RUN apk add --no-cache tzdata && \
    apk add --no-cache musl-locales musl-locales-lang && \
    rm -rf /var/cache/apk/*

# Настройка локали
ENV LANG=ru_RU.UTF-8
ENV LANGUAGE=ru_RU:ru
ENV LC_ALL=ru_RU.UTF-8
ENV TZ=Europe/Moscow

COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-Dconsole.encoding=UTF-8", "-jar", "app.jar"]