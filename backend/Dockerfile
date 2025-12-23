# مرحله 1: Build
FROM eclipse-temurin:17-jdk AS build
LABEL maintainer="navid.mahjoub@yahoo.com"
WORKDIR /app

# نصب Maven
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# کپی فایل‌های پروژه
COPY pom.xml .
COPY src ./src

# ساخت فایل JAR از سورس کد
RUN mvn clean package -DskipTests

# مرحله 2: Runtime
FROM eclipse-temurin:17-jdk
LABEL maintainer="navid.mahjoub@yahoo.com"
WORKDIR /app

RUN apt-get update && \
    apt-get install -y --no-install-recommends ffmpeg netcat-openbsd && \
    rm -rf /var/lib/apt/lists/*

# کپی فایل JAR از مرحله build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh","-c","until nc -z seddit_db 3306; do echo 'waiting for mysql...'; sleep 1; done; java -jar /app.jar"]
