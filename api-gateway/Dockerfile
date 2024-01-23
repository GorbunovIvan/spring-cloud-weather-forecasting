FROM openjdk

WORKDIR /app

COPY target/api-gateway-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8765

ENTRYPOINT ["java", "-jar", "app.jar"]

# Run:
#   'docker build -t api-gateway-image .'