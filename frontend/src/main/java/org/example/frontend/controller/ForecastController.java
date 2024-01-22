package org.example.frontend.controller;

import lombok.RequiredArgsConstructor;
import org.example.frontend.model.Forecast;
import org.example.frontend.service.ForecastService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/weather")
@RequiredArgsConstructor
public class ForecastController {

    private final ForecastService forecastService;

    @GetMapping("/forecast")
    public String forecastByLocationAndDay(Model model,
                                @RequestParam(value = "location", required = false) String location,
                                @RequestParam(value = "day", required = false) LocalDate day) {

        if (day == null) {
            day = LocalDate.now();
        }

        Forecast forecast;
        String error = null;

        if (location == null) {
            forecast = new Forecast(day, "");
        } else {
            forecast = forecastService.forecastByLocationAndDay(location, day);
            if (forecast.getTemperatureCelsius() == null) {
                error = "Error while receiving forecast";
            }
        }

        model.addAttribute("forecast", forecast);
        model.addAttribute("error", error);

        return "forecasts/forecast";
    }
}
