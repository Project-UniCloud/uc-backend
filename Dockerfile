FROM gradle:8.5-jdk21 AS build

WORKDIR /build

COPY . .

RUN gradle :bootstrap:bootJar

FROM eclipse-temurin:21-jre

RUN useradd -r -u 1001 appuser

WORKDIR /app

COPY --from=build /build/bootstrap/build/libs/*.jar app.jar

RUN chown -R appuser:appuser /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
