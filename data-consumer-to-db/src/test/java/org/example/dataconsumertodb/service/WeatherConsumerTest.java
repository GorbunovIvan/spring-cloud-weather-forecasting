package org.example.dataconsumertodb.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.dataconsumertodb.model.Location;
import org.example.dataconsumertodb.model.WeatherForDay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WeatherConsumerTest {

    private WeatherConsumer weatherConsumer;

    @Mock
    private WeatherService weatherService;

    private List<WeatherForDay> weatherEntities;

    @BeforeEach
    void setUp() {

        Mockito.reset(weatherService);

        weatherConsumer = new WeatherConsumer(weatherService);

        weatherEntities = List.of(
                new WeatherForDay(null, LocalDate.MIN, 4.1, new Location(null, "name 1 test", "region 1 test", "country 1 test")),
                new WeatherForDay(null, LocalDate.now(), -2.3, new Location(null, "name 2 test", "region 2 test", "country 2 test")),
                new WeatherForDay(null, LocalDate.MAX, 19.0, new Location(null, "name 3 test", "region 3 test", "country 3 test"))
        );
    }

    @Test
    void testMessageListener() {

        var records = weatherEntities.stream()
                .map(w -> new ConsumerRecord<>("topic", 1, 1L, "key", w))
                .toList();

        weatherConsumer.messageListener(records);
        verify(weatherService, times(1)).createIfNotExisting(weatherEntities);
    }
}