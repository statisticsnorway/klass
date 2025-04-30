FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/*.war app.war
ENTRYPOINT ["java", "-jar", "app.war"]