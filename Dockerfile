# 1단계: Gradle로 빌드하는 빌드 이미지
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Gradle wrapper + 설정 파일 + 소스 복사
COPY . .

# JAR 빌드
RUN ./gradlew clean build -x test

# 2단계: 실행용 이미지
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# 빌드 단계에서 생성된 JAR만 가져옴
COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "/app.jar"]
