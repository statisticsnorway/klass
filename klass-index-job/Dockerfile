FROM gcr.io/distroless/java21-debian13:nonroot
ENV TZ="Europe/Oslo"
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
