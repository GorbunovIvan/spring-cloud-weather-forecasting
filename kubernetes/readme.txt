### Preparing DOCKER images ###

cd config-server
docker build -t ivangorbunovv/spring-cloud-weather-forecasting:config-server .
docker push ivangorbunovv/spring-cloud-weather-forecasting:config-server
cd ..

cd eureka-server
docker build -t ivangorbunovv/spring-cloud-weather-forecasting:eureka-server .
docker push ivangorbunovv/spring-cloud-weather-forecasting:eureka-server
cd ..

cd api-gateway
docker build -t ivangorbunovv/spring-cloud-weather-forecasting:api-gateway .
docker push ivangorbunovv/spring-cloud-weather-forecasting:api-gateway
cd ..

cd weather-collector
docker build -t ivangorbunovv/spring-cloud-weather-forecasting:weather-collector .
docker push ivangorbunovv/spring-cloud-weather-forecasting:weather-collector
cd ..

cd data-consumer-to-db
docker build -t ivangorbunovv/spring-cloud-weather-forecasting:data-consumer-to-db .
docker push ivangorbunovv/spring-cloud-weather-forecasting:data-consumer-to-db
cd ..

cd data-rest
docker build -t ivangorbunovv/spring-cloud-weather-forecasting:data-rest .
docker push ivangorbunovv/spring-cloud-weather-forecasting:data-rest
cd ..

cd frontend
docker build -t ivangorbunovv/spring-cloud-weather-forecasting:frontend .
docker push ivangorbunovv/spring-cloud-weather-forecasting:frontend
cd ..


### Kubernetes operations ###

kubectl apply -f kubernetes\deploy-and-service-for-config-server.yaml
kubectl apply -f kubernetes\deploy-and-service-for-eureka-server.yaml
kubectl apply -f kubernetes\deploy-and-service-for-api-gateway.yaml
kubectl apply -f kubernetes\deploy-and-service-for-kafka.yaml
kubectl apply -f kubernetes\deploy-and-service-for-postgres.yaml
kubectl apply -f kubernetes\deploy-and-service-for-weather-collector.yaml
kubectl apply -f kubernetes\deploy-and-service-for-data-consumer-to-db.yaml
kubectl apply -f kubernetes\deploy-and-service-for-data-rest.yaml
kubectl apply -f kubernetes\deploy-and-service-for-frontend.yaml
