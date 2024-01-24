FROM openjdk

WORKDIR /app

COPY target/data-consumer-to-db-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]

# Run:
#   'docker build -t data-consumer-to-db-image .'