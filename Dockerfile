# 1단계: 빌드용 (조립 전문가)
# 한 번 빌드하면 이 단계는 캐싱되어 다시 실행되지 않아야 함
FROM eclipse-temurin:21-jdk-alpine AS builder

RUN apk add --no-cache binutils

# [개선] jdeps 분석 대신, 표준적인 모듈 리스트를 직접 할당
# 프로젝트에서 특별한 모듈을 추가하지 않는 한 이 리스트로 충분합니다
RUN $JAVA_HOME/bin/jlink \
    --verbose \
    --add-modules java.base,java.sql,java.naming,java.desktop,java.management,java.instrument,java.scripting,java.security.jgss,java.security.sasl,jdk.httpserver,jdk.jfr,jdk.unsupported \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /customjre

# 2단계: 실행 전용 (배달 전문가)
FROM alpine:3.19

ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"
ENV SPRING_PROFILES_ACTIVE=docker

# 위에서 만든 커스텀 JRE 복사 (이 레이어가 캐싱의 핵심)
COPY --from=builder /customjre $JAVA_HOME

# 유저 생성 등 공통 설정
RUN adduser -u 1000 -D appuser
WORKDIR /app

# [중요] 가장 자주 변하는 JAR 복사는 맨 마지막에!
# 그래야 위쪽의 무거운 jlink 단계가 깨지지 않고 재사용됩니다.
COPY --chown=1000:1000 build/libs/*.jar app.jar

USER 1000
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]