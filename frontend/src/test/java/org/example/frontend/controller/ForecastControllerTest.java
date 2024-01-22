package org.example.frontend.controller;

import org.example.frontend.model.Forecast;
import org.example.frontend.service.ForecastService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ForecastControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ForecastService forecastService;

    private List<Forecast> forecasts;

    private final String baseURI = "/weather";

    @BeforeEach
    void setUp() {

        forecasts = List.of(
                new Forecast(LocalDate.now().minusDays(2), "test Location 1"),
                new Forecast(LocalDate.now().minusDays(1), "test Location 2"),
                new Forecast(LocalDate.now().minusDays(0), "test Location 3"),
                new Forecast(LocalDate.now().plusDays(0), "test Location 2"),
                new Forecast(LocalDate.now().plusDays(1), "test Location 1"),
                new Forecast(LocalDate.now().plusDays(2), "test Location 2")
        );

        for (var forecast : forecasts) {
            when(forecastService.forecastByLocationAndDay(forecast.getLocation().getName(), forecast.getDay())).thenReturn(forecast);
        }
    }

    @Test
    void testForecastByLocationAndDay() throws Exception {

        for (var forecast : forecasts) {

            mvc.perform(MockMvcRequestBuilders.get(baseURI + "/forecast")
                        .param("location", forecast.getLocation().getName())
                        .param("day", forecast.getDay().toString()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("forecasts/forecast"))
                    .andExpect(model().attribute("forecast", forecast));

            verify(forecastService, times(1)).forecastByLocationAndDay(forecast.getLocation().getName(), forecast.getDay());
        }
    }

    @Test
    void testForecastByLocationAndDay_Empty() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get(baseURI + "/forecast"))
                .andExpect(status().isOk())
                .andExpect(view().name("forecasts/forecast"))
                .andExpect(model().attribute("forecast", new Forecast(LocalDate.now(), "")));

        verify(forecastService, never()).forecastByLocationAndDay(anyString(), any(LocalDate.class));
    }
}