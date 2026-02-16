FROM docker.io/library/eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /src/eshop
COPY . .

RUN chmod +x ./gradlew \
    && ./gradlew --no-daemon bootJar \
    && cp "$(ls build/libs/*.jar | grep -Ev 'plain\\.jar$' | head -n 1)" /src/eshop/app.jar

FROM docker.io/library/eclipse-temurin:21-jre-alpine AS runner

ARG USER_NAME=eshop
ARG USER_UID=1000
ARG USER_GID=${USER_UID}

RUN addgroup -g "${USER_GID}" "${USER_NAME}" \
    && adduser -h /opt/eshop -D -u "${USER_UID}" -G "${USER_NAME}" "${USER_NAME}"

USER ${USER_NAME}
WORKDIR /opt/eshop

COPY --from=builder --chown=${USER_UID}:${USER_GID} /src/eshop/app.jar app.jar

EXPOSE 8080

ENV PORT=8080
ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar --server.port=${PORT}"]
