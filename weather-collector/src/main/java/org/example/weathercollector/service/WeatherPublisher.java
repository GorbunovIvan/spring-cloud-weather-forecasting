package org.example.weathercollector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.weathercollector.model.WeatherForDay;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class WeatherPublisher {

    private final KafkaTemplate<String, WeatherForDay> kafkaProducer;

    @Value("${spring.kafka.weather-topic}")
    private String topicName;

    protected void publish(List<WeatherForDay> weatherEntities) {
        for (var weather : weatherEntities) {
            try {
                kafkaProducer.send(topicName, "key", weather);
                log.info("Successful publishing. Weather for location '" + weather.getLocation().getName() + "' was published");
            } catch (Exception e) {
                log.error("Error during publishing. Weather for location '" + weather.getLocation().getName() + "' was not published.\n" + e.getCause());
            }
        }
    }
}
