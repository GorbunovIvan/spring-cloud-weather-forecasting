FROM openjdk

WORKDIR /app

COPY target/weather-collector-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]

# Run:
#   'docker build -t weather-collector-image .'