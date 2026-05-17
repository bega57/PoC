FROM gradle:8.10-jdk17 AS build

WORKDIR /app

COPY . .

RUN gradle build -x test

FROM eclipse-temurin:17

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]