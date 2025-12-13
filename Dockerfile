FROM gradle:9.2.1-jdk25 AS build

WORKDIR /build

COPY . .
RUN gradle :bootstrap:bootJar --no-daemon

FROM eclipse-temurin:25-jre

RUN useradd -r -u 1001 appuser

WORKDIR /app

COPY --from=build /build/bootstrap/build/libs/*.jar app.jar

COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

RUN chown -R appuser:appuser /app /entrypoint.sh

USER appuser

EXPOSE 8080

ENTRYPOINT ["/entrypoint.sh"]
CMD ["java", "-jar", "app.jar"]
