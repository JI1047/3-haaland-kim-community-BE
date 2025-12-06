FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY build/libs/*.jar app.jar

# Docker 실행 시 항상 docker 프로필 사용
ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
