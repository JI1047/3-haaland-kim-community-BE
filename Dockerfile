# JDK가 포함된 빌드용 이미지를 사용
FROM eclipse-temurin:21-jdk-alpine AS builder

# 컨테이너 내부 작업 디렉토리를 /app으로 설정
WORKDIR /app

# gradle를 컨테이너에 복사
# wrapper로 빌드 가능하게 한다.
COPY gradlew .
# Gradle Wrapper가 사용하는 설정과 캐시 파일 디렉토리 복사
#이걸 먼저 COPY하는 이유 = Docker layer caching 극대화
COPY gradle ./gradle/
#빌드 스크립트만 먼저 복사
#소스코드가 바뀌어도 의존성 캐시는 유지
COPY build.gradle settings.gradle ./

# gradlew 파일 실행 권한 부여
RUN chmod +x gradlew
#gradle 의존성만 미리 다운로드
#Docker 캐시층(layer)을 저장해두고 다음 빌드에서 재사용 가능
RUN ./gradlew dependencies --no-daemon >/dev/null 2>&1 || true

# 마지막에 전체 소스코드 복사
#이렇게 해야 코드를 수정해도 의존성 캐시가 깨지지 않음
#변경된 부분만 새로 빌드하게 됨
COPY src ./src

#Spring Boot 실행용 JAR을 빌드
#bootJar → 실제 실행 가능한 JAR 생성
#-x test → 테스트는 실행하지 않음
#테스트는 CI에서 이미 실행했으므로 docker build 단계에서 시간을 절약
RUN ./gradlew bootJar --no-daemon -x test

# ---- 2) Runtime Stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
