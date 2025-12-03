# 1단계: 빌드 스테이지
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Gradle Wrapper 파일과 의존성 관련 파일 먼저 복사
COPY gradlew .
COPY gradle ./gradle/
COPY build.gradle settings.gradle ./

# 의존성 캐시 레이어 생성
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

# 전체 소스 복사
COPY . .

# 최종 JAR 빌드
RUN ./gradlew build --no-daemon -x test


# 2단계: 런타임 스테이지
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# JAR만 복사
COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar

EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
