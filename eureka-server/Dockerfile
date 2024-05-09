FROM openjdk

WORKDIR /app

COPY target/eureka-server-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]

# Run:
#   'docker build -t eureka-server-image .'
#   or 'docker build -t ivangorbunovv/spring-cloud-weather-forecasting:eureka-server .'