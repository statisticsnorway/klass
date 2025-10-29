FROM eclipse-temurin:17-jre
ENV TZ="Europe/Oslo"
WORKDIR /app
COPY target/*.war app.war
ENTRYPOINT ["java", "-jar", "app.war"]
