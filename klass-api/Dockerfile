FROM gcr.io/distroless/java21-debian13@sha256:a2aeb5a7a714cfe928b31fdf67b2d23f503ea6b9612705af062de22e0c50ba77
ENV TZ="Europe/Oslo"
WORKDIR /app
COPY target/*.war app.war
USER nonroot
ENTRYPOINT ["java", "-jar", "app.war"]
