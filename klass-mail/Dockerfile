FROM gcr.io/distroless/java21-debian13@sha256:1de29c4b0865b76fb878ba0a7b5a1d80db189db99cf46a5d88ec9c253b5cea9b
WORKDIR /app
COPY target/*.war app.war
USER nonroot
ENTRYPOINT ["java", "-jar", "app.war"]
