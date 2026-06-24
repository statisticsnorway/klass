FROM eclipse-temurin:25-jre
WORKDIR /app
COPY target/*.war app.war
ENTRYPOINT ["java", "-jar", "app.war"]
