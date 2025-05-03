FROM eclipse-temurin:21-jre

RUN useradd -r -u 1001 appuser

WORKDIR /app

COPY bootstrap/build/libs/*.jar app.jar

RUN chown -R appuser:appuser /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
