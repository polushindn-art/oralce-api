FROM eclipse-temurin:21-jre-alpine

RUN apt-get update && apt-get install -y \
    tzdata \
    libstdc++6 \
    && rm -rf /var/lib/apt/lists/*

ENV LANG=ru_RU.UTF-8
ENV LC_ALL=ru_RU.UTF-8
ENV TZ=Asia/Barnaul

COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]