package org.example.dataconsumertodb.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Log4j2
public class WeatherForDayDeserializer extends JsonDeserializer<WeatherForDay> {

    public WeatherForDayDeserializer() {
        super();
        objectMapper.findAndRegisterModules();
    }

    @Override
    public WeatherForDay deserialize(String topic, Headers headers, ByteBuffer data) {

        var byteArray = new byte[data.remaining()];
        data.get(byteArray);

        if (byteArray.length == 0) {
            log.warn("Empty data came from topic '{}'", topic);
            return null;
        }

        var json = new String(byteArray, StandardCharsets.UTF_8);

        try {
            return objectMapper.readValue(json, WeatherForDay.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
