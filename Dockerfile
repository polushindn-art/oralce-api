FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache tzdata musl-locales musl-locales-lang libstdc++ && \
    rm -rf /var/cache/apk/*

ENV LANG=ru_RU.UTF-8
ENV LC_ALL=ru_RU.UTF-8
ENV TZ=Asia/Barnaul

COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]