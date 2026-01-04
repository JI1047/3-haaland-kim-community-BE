# 1단계: 의존성 분석 (deps)
FROM eclipse-temurin:21-jdk-alpine AS deps

WORKDIR /app
# 빌드된 jar 파일을 복사
COPY build/libs/*.jar app.jar

# jdeps를 사용하기 위해 jar 파일 압축 해제
RUN mkdir /app/unpacked && \
    unzip app.jar -d /app/unpacked && \
    # 분석 시작: 필요한 모듈 리스트를 /deps.info에 저장
    $JAVA_HOME/bin/jdeps \
    --ignore-missing-deps \
    --print-module-deps \
    -q \
    --recursive \
    --multi-release 21 \
    --class-path "/app/unpacked/BOOT-INF/lib/*" \
    --module-path "/app/unpacked/BOOT-INF/lib/*" \
    app.jar > /deps.info

# 2단계: 사용자 정의 JRE 생성 (builder)
FROM eclipse-temurin:21-jdk-alpine AS builder

# jlink 실행에 필요한 binutils 설치
RUN apk add --no-cache binutils

# 1단계에서 분석한 모듈 리스트 가져오기
COPY --from=deps /deps.info /deps.info

# 최소한의 모듈만 포함된 custom JRE 생성
RUN $JAVA_HOME/bin/jlink \
    --verbose \
    --add-modules $(cat /deps.info) \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /customjre

# 3단계: 최종 실행 이미지 (runtime)
FROM alpine:3.19

# 환경 변수 설정
ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"
ENV SPRING_PROFILES_ACTIVE=docker

# 2단계에서 만든 가벼운 JRE만 복사 (이것이 핵심!)
COPY --from=builder /customjre $JAVA_HOME

# 보안을 위한 비관리자 유저 생성
RUN adduser -u 1000 -D appuser
USER 1000

WORKDIR /app
# 실행할 앱 복사
COPY --chown=1000:1000 build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]