FROM eclipse-temurin:24-jre
WORKDIR /app
COPY target/*.war app.war
ENTRYPOINT ["java", "-jar", "app.war"]
