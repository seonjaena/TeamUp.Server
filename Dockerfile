FROM gradle:8.6.0-jdk21-jammy as Builder

WORKDIR /usr/local/TeamUp.Server

COPY *.gradle gradle.* gradlew ./
COPY gradle ./gradle
COPY src/main ./src/main

RUN ./gradlew clean build -x test && \
    mv build/libs/teamup-*.jar teamup.jar


FROM eclipse-temurin:21.0.2_13-jre-alpine as Production

ENV TZ=Asia/Seoul

WORKDIR /usr/local/TeamUp.Server

COPY --from=Builder /usr/local/TeamUp.Server/teamup.jar ./teamup.jar

RUN apk --no-cache add tzdata && \
    apk --no-cache add curl && \
    echo $TZ > /etc/timezone && \
    apk del tzdata

ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "teamup.jar"]
