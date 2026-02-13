FROM gcr.io/distroless/java17
ENV TZ="Europe/Oslo"
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
