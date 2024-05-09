FROM openjdk

WORKDIR /app

COPY target/config-server-0.0.1-SNAPSHOT.jar ./app.jar
COPY config-repo ./config-repo

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]

# Run:
#   'docker build -t config-server-image .'
#   or 'docker build -t ivangorbunovv/spring-cloud-weather-forecasting:config-server .'