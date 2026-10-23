# eclipse-temurin:8-jre
FROM eclipse-temurin@sha256:1fc1cdba9c1e68173bc553b95438206b0b995f9f763cfc77feb45b27f5774d9b
WORKDIR /app
COPY target/*.war app.war
USER nonroot
ENTRYPOINT ["java", "-jar", "app.war"]
