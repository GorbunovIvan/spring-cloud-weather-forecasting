This is a microservice application, built as a multi-module Maven project, which consists of:
1) [config-server](config-server) - manages the storage of microservice properties. It contains its own folder [config-repo](config-server%2Fconfig-repo), which is used as a git repository for storing properties to be accessible when is run in a container.
2) [eureka-server](eureka-server) - (also "eserver") used for service registration and discovery within a microservices architecture, handles service instances that come and go dynamically.
3) [api-gateway](api-gateway) - acts as a single entry point for clients to access all other services (except config-server), encapsulating the complexities of the underlying microservices architecture from clients.
4) [weather-collector](weather-collector) - microservice that analyzes the weather for today on a weather site for all US capitals and puts all the weather into the Kafka message broker as a producer.
5) [data-consumer-to-db](data-consumer-to-db) - microservice that takes the weather from the Kafka message broker, acting as a consumer, and saves it into a Postgres database.
6) [data-rest](data-rest) - microservice that reads data from the database and provides it as a rest service a service that can predict the weather for a specific date and location based on the statistics it has in the database. Secured by a token (JWT). Has Swagger UI documentation.
7) [frontend](frontend) - microservice that takes data from a [data-rest](data-rest) using a rest template (including going through JWT authorization) and has a web interface on thymeleaf to interact with user, giving him the ability to enter a date, city and get a weather forecast.

Also, the project contains [config-repo](config-repo) folder that is used as a git-repository to store properties 
when app is run on local machine

Every microservice has a Dockerfile.
The application has docker-compose.yml in the root directory.