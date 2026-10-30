FROM gcr.io/distroless/java21-debian13@sha256:ba6c75fda24af8a9aee3ec7633bd7369ee390c054d4e4292d52f17014cc9a11a
ENV TZ="Europe/Oslo"
WORKDIR /app
COPY target/*.jar app.jar
USER nonroot
ENTRYPOINT ["java", "-jar", "app.jar"]
