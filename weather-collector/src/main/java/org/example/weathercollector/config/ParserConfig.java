package org.example.weathercollector.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ConditionalOnProperty(name = "parser.scheduled.enabled", havingValue = "true")
@EnableScheduling
public class ParserConfig {
}
