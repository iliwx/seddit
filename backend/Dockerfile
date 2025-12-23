FROM eclipse-temurin:17-jdk
LABEL maintainer="navid.mahjoub@yahoo.com"

RUN apt-get update \
 && apt-get install -y --no-install-recommends ffmpeg netcat-openbsd \
 && rm -rf /var/lib/apt/lists/*

COPY *.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh","-c","until nc -z seddit_db 3306; do echo 'waiting for mysql...'; sleep 1; done; java -jar /app.jar"]