FROM gcr.io/distroless/java17
ENV TZ="Europe/Oslo"
WORKDIR /app
COPY target/*.war app.war
ENTRYPOINT ["java", "-jar", "app.war"]
