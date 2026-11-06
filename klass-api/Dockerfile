FROM gcr.io/distroless/java21-debian13@sha256:1bc4eca3c3b351743be673d7109951b509d6f47a57648e5e2efe784a1b051c4a
ENV TZ="Europe/Oslo"
WORKDIR /app
COPY target/*.war app.war
USER nonroot
ENTRYPOINT ["java", "-jar", "app.war"]
