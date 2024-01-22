package org.example.frontend.service;

import org.example.frontend.model.Forecast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.Mockito.*;

@SpringBootTest
class ForecastServiceTest {

    @Autowired
    private ForecastService forecastService;

    @MockBean
    private RestTemplate restTemplate;

    @Value("${forecast.remote-service.forecast-url}")
    private String remoteForecastServiceURL;

    @Value("${forecast.remote-service.auth.url}")
    private String remoteForecastServiceAuthURL;

    @Value("${forecast.remote-service.auth.username}")
    private String remoteForecastServiceAuthUsername;

    @Value("${forecast.remote-service.auth.password}")
    private String remoteForecastServiceAuthPassword;

    private List<Forecast> forecasts;
    private Map<String, Forecast> requestResponseMap;

    private final String token = "test-token";

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

        requestResponseMap = new HashMap<>();

        for (var forecast : forecasts) {
            var URL = String.format("%s?location=%s&day=%s", remoteForecastServiceURL, forecast.getLocation(), forecast.getDay());
            requestResponseMap.put(URL, forecast);
        }

        var headers = new HttpHeaders();
        headers.set("Authorization", token);
        var httpEntity = new HttpEntity<>(headers);

        when(restTemplate.getForEntity("/get-401", Forecast.class)).thenReturn(ResponseEntity.status(401).build());

        for (var entry : requestResponseMap.entrySet()) {
            when(restTemplate.getForEntity(entry.getKey(), Forecast.class)).thenReturn(ResponseEntity.ok(entry.getValue()));
            when(restTemplate.exchange(entry.getKey(), HttpMethod.GET, httpEntity, Forecast.class)).thenReturn(ResponseEntity.ok(entry.getValue()));
        }

        when(restTemplate.postForEntity(endsWith("/weather/auth/login"), any(Map.class), any())).thenReturn(ResponseEntity.ok(Map.of("token", token)));
    }

    @Test
    void testForecastByLocationAndDay() {
        for (var forecast : forecasts) {
            var result = forecastService.forecastByLocationAndDay(forecast.getLocation().getName(), forecast.getDay());
            assertEquals(forecast, result);
        }
    }

    @Test
    void testForecastByLocationAndDay_AlwaysUnauthorized() {

        // Mocking to return an error: "unauthorized"
        when(restTemplate.getForEntity(anyString(), any())).thenReturn(ResponseEntity.status(403).build());
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class))).thenReturn(ResponseEntity.status(403).build());

        // Setting token
        forecastService.setJwToken("");

        // Verifying
        forecastService.forecastByLocationAndDay("test-location", LocalDate.now(), false);
        verify(restTemplate, times(1)).postForEntity(anyString(), any(Map.class), any());

        forecastService.forecastByLocationAndDay("test-location", LocalDate.now());
        verify(restTemplate, times(2)).postForEntity(anyString(), any(Map.class), any());

        forecastService.forecastByLocationAndDay("test-location", LocalDate.now(), true);
        verify(restTemplate, times(3)).postForEntity(anyString(), any(Map.class), any());
    }

    @Test
    void testForecastByLocationAndDay_AuthorizedButWrongToken() {



        // Mocking to return an error: "unauthorized"
        when(restTemplate.getForEntity(anyString(), any())).thenReturn(ResponseEntity.status(401).build());
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class))).thenReturn(ResponseEntity.status(401).build());

        // Setting token
        forecastService.setJwToken(token);

        // Verifying
        forecastService.forecastByLocationAndDay("test-location", LocalDate.now(), false);
        verify(restTemplate, never()).postForEntity(anyString(), any(Map.class), any());

        forecastService.forecastByLocationAndDay("test-location", LocalDate.now());
        verify(restTemplate, times(1)).postForEntity(anyString(), any(Map.class), any());

        forecastService.forecastByLocationAndDay("test-location", LocalDate.now(), true);
        verify(restTemplate, times(2)).postForEntity(anyString(), any(Map.class), any());
    }

    @Test
    void testGetAuthTokenFromRemoteService() {
        var result = forecastService.getAuthTokenFromRemoteService();
        assertEquals(token, result);
        verify(restTemplate, times(1)).postForEntity(anyString(), any(), any());
    }

    @Test
    void testGetFromRemoteService() {

        var headers = new HttpHeaders();
        headers.set("Authorization", token);

        for (var entry : requestResponseMap.entrySet()) {
            var URL = entry.getKey();
            var result = forecastService.getFromRemoteService(URL, headers, Forecast.class);
            assertEquals(ResponseEntity.ok(entry.getValue()), result);
            verify(restTemplate, times(1)).exchange(URL, HttpMethod.GET, new HttpEntity<>(headers), Forecast.class);
        }
        verify(restTemplate, times(requestResponseMap.size())).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }

    @Test
    void testGetFromRemoteService_EmptyHeaders() {
        for (var entry : requestResponseMap.entrySet()) {
            var URL = entry.getKey();
            var result = forecastService.getFromRemoteService(URL, null, Forecast.class);
            assertEquals(ResponseEntity.ok(entry.getValue()), result);
            verify(restTemplate, times(1)).getForEntity(URL, Forecast.class);
        }
        verify(restTemplate, times(requestResponseMap.size())).getForEntity(anyString(), any(Class.class));
    }

    @Test
    void testPostToRemoteService() {

        var request = new HashMap<String, String>();
        request.put("username", remoteForecastServiceAuthUsername);
        request.put("password", remoteForecastServiceAuthPassword);

        var responseExpected = ResponseEntity.ok(Map.of("token", token));

        var result = forecastService.postToRemoteService(remoteForecastServiceAuthURL, request, Map.class);
        assertEquals(responseExpected, result);
        verify(restTemplate, times(1)).postForEntity(anyString(), any(Map.class), any());
    }
}