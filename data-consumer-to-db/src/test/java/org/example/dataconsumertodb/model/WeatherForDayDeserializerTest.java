package org.example.dataconsumertodb.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WeatherForDayDeserializerTest {

    private WeatherForDayDeserializer weatherForDayDeserializer;
    private List<WeatherForDay> weatherEntities;

    @BeforeEach
    void setUp() {
        weatherForDayDeserializer = new WeatherForDayDeserializer();
        weatherEntities = List.of(
                new WeatherForDay(null, LocalDate.MIN, 4.1, new Location(null, "name 1 test", "region 1 test", "country 1 test")),
                new WeatherForDay(null, LocalDate.now(), -2.3, new Location(null, "name 2 test", "region 2 test", "country 2 test")),
                new WeatherForDay(null, LocalDate.MAX, 19.0, new Location(null, "name 3 test", "region 3 test", "country 3 test"))
        );
    }

    @Test
    void testDeserialize() {

        for (var weather : weatherEntities) {

            var data = convertToByteBuffer(weather);
            assert(data != null);

            var result = weatherForDayDeserializer.deserialize("topic", null, data);
            assertNotNull(result);
            assertEquals(weather, result);
        }
    }

    private ByteBuffer convertToByteBuffer(Object obj) {

        var objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        try {
            byte[] byteArray = objectMapper.writeValueAsBytes(obj);
            return ByteBuffer.wrap(byteArray);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}