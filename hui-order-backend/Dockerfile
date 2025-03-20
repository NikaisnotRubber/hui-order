# 使用包含Maven和JDK 21的Docker镜像进行构建
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build

COPY . /app

WORKDIR /app

RUN mvn clean install -Dmaven.test.skip=true

FROM openjdk:21-jdk

COPY --from=build /app/target/hui-order-backend-0.0.1-SNAPSHOT.jar /hui-order-backend-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/hui-order-backend-0.0.1-SNAPSHOT.jar"]


