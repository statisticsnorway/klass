FROM gcr.io/distroless/java21-debian13@sha256:058980963076363047293bffad7aab3c5af8162d1957c42851b3205bd9bae34e
ENV TZ="Europe/Oslo"
WORKDIR /app
COPY target/*.war app.war
USER nonroot
ENTRYPOINT ["java", "-jar", "app.war"]
