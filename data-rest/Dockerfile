FROM openjdk

WORKDIR /app

COPY target/data-rest-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]

# Run:
#   'docker build -t data-rest-image .'