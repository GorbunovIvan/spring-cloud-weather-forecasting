package org.example.weathercollector.service;

import org.example.weathercollector.model.Location;
import org.example.weathercollector.model.WeatherForDay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class WeatherPublisherTest {

    @Autowired
    private WeatherPublisher weatherPublisher;

    @MockBean
    private KafkaTemplate<String, WeatherForDay> kafkaProducer;

    @Value("${spring.kafka.weather-topic}")
    private String topicName;

    private List<WeatherForDay> weatherEntities;

    @BeforeEach
    void setUp() {

        weatherEntities = List.of(
                new WeatherForDay(LocalDate.MIN, new Location("test-1"), 0.4),
                new WeatherForDay(LocalDate.now(), new Location("test-2"), -1.8),
                new WeatherForDay(LocalDate.MAX, new Location("test-3"), 23.0)
        );

        for (var weather : weatherEntities) {
            when(kafkaProducer.send(topicName, "key", weather)).thenReturn(null);
        }
    }

    @Test
    void testPublish() {
        weatherPublisher.publish(weatherEntities);
        for (var weather : weatherEntities) {
            verify(kafkaProducer, times(1)).send(topicName, "key", weather);
        }
        verify(kafkaProducer, times(weatherEntities.size())).send(anyString(), anyString(), any(WeatherForDay.class));
    }
}