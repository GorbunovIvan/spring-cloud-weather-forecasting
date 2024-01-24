FROM openjdk

WORKDIR /app

COPY target/frontend-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]

# Run:
#   'docker build -t frontend-image .'