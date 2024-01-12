package org.example.dataconsumertodb.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.dataconsumertodb.model.WeatherForDayDeserializer;
import org.example.dataconsumertodb.model.WeatherForDay;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public Map<String, Object> consumerConfig() {

        var properties = new HashMap<String, Object>();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, weatherForDayJsonDeserializer());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        return properties;
    }

    @Bean
    public JsonDeserializer<WeatherForDay> weatherForDayJsonDeserializer() {
        JsonDeserializer<WeatherForDay> deserializer = new WeatherForDayDeserializer();
        deserializer.addTrustedPackages("*");
        return deserializer;
    }

    @Bean
    public ConsumerFactory<String, WeatherForDay> consumerFactory(JsonDeserializer<WeatherForDay> weatherForDayJsonDeserializer) {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(), new StringDeserializer(), weatherForDayJsonDeserializer);
    }

    @Bean
    public KafkaListenerContainerFactory<?> kafkaListenerContainerFactory(ConsumerFactory<String, WeatherForDay> consumerFactor) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, WeatherForDay>();
        factory.setConsumerFactory(consumerFactor);
        factory.setBatchListener(true); // To receive many messages at once (by 100 per round by default)
        return factory;
    }
}
