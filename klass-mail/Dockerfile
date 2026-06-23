FROM gcr.io/distroless/java21-debian13:nonroot
WORKDIR /app
COPY target/*.war app.war
ENTRYPOINT ["java", "-jar", "app.war"]
