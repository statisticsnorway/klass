# eclipse-temurin:8-jre
FROM eclipse-temurin@sha256:1fc1cdba9c1e68173bc553b95438206b0b995f9f763cfc77feb45b27f5774d9b
WORKDIR /app
COPY target/*.war app.war
RUN groupadd --system --gid 65532 nonroot \
    && useradd --system --uid 65532 --gid 65532 --create-home --home-dir /home/nonroot nonroot \
    && chown -R 65532:65532 /app
USER nonroot
ENTRYPOINT ["java", "-jar", "app.war"]
