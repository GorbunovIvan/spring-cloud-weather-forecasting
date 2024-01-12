package org.example.dataconsumertodb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.dataconsumertodb.model.WeatherForDay;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@ConditionalOnProperty(name = "consumer.enabled", havingValue = "true")
public class WeatherConsumer {

    private final WeatherService weatherService;

    @KafkaListener(topics = "${spring.kafka.weather-topic}")
    public void messageListener(List<ConsumerRecord<String, WeatherForDay>> records) {
        try {
            var weatherEntities = records.stream().map(ConsumerRecord::value).toList();
            weatherService.createIfNotExisting(weatherEntities);
            log.info("Consumed: {}", records);
        } catch (Exception e) {
            log.error("Consuming failed: {}", records);
            log.error(e.getMessage());
        }
    }
}
