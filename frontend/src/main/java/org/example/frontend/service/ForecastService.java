package org.example.frontend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.frontend.model.Forecast;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class ForecastService {

    private final RestTemplate restTemplate;

    @Value("${forecast.remote-service.forecast-url}")
    private String remoteForecastServiceURL;

    @Value("${forecast.remote-service.auth.url}")
    private String remoteForecastServiceAuthURL;

    @Value("${forecast.remote-service.auth.username}")
    private String remoteForecastServiceAuthUsername;

    @Value("${forecast.remote-service.auth.password}")
    private String remoteForecastServiceAuthPassword;

    private String jwToken = "";

    protected void setJwToken(String jwToken) {
        this.jwToken = jwToken;
    }

    public Forecast forecastByLocationAndDay(String location, LocalDate day) {
        return forecastByLocationAndDay(location, day, true);
    }

    protected Forecast forecastByLocationAndDay(String location, LocalDate day, boolean tryToReauthorizeOnError) {

        var forecastToReturnForErrors = new Forecast(day, location);

        if (jwToken == null || jwToken.isEmpty()) {
            var token = getAuthTokenFromRemoteService();
            if (token == null || token.isEmpty()) {
                tryToReauthorizeOnError = false;
                log.warn("jwToken is empty");
            } else {
                setJwToken(token);
            }
        }

        var URL = String.format("%s?location=%s&day=%s", remoteForecastServiceURL, location, day);
        var headers = new HttpHeaders();
        headers.set("Authorization", jwToken);

        var forecastResponse = getFromRemoteService(URL, headers, Forecast.class);

        // OK - 200
        if (forecastResponse.getStatusCode().is2xxSuccessful()) {
            if (forecastResponse.getBody() instanceof Forecast forecast) {
                return forecast;
            }
            log.error("Something when wrong by addressing to URL: '" + URL + "'.");
            return forecastToReturnForErrors;
        }

        // UNAUTHORIZED or FORBIDDEN
        if (tryToReauthorizeOnError) {
            if (forecastResponse.getStatusCode().value() == 401
                || forecastResponse.getStatusCode().value() == 403) {

                var token = getAuthTokenFromRemoteService();
                if (token == null) {
                    return forecastToReturnForErrors;
                } else {
                    setJwToken(token);
                }

                return forecastByLocationAndDay(location, day, false);
            }
        }

        log.warn("Status code: '" + forecastResponse.getStatusCode() + "' from addressing to URL: '" + URL + "'");
        return forecastToReturnForErrors;
    }

    protected String getAuthTokenFromRemoteService() {

        var request = new HashMap<String, String>();
        request.put("username", remoteForecastServiceAuthUsername);
        request.put("password", remoteForecastServiceAuthPassword);
        var authResponse = postToRemoteService(remoteForecastServiceAuthURL, request, Map.class);

        if (authResponse.getStatusCode().is2xxSuccessful()) {
            if (authResponse.getBody() instanceof Map<?, ?> mapResponse) {
                return (String) mapResponse.get("token");
            }
        }

        log.error("Authorization failed");
        return null;
    }

    protected ResponseEntity<?> getFromRemoteService(String URL, HttpHeaders headers, Class<?> responseType) {

        ResponseEntity<?> result;

        try {
            if (headers != null && !headers.isEmpty()) {
                result = restTemplate.exchange(URL, HttpMethod.GET, new HttpEntity<>(headers), responseType);
            } else {
                result = restTemplate.getForEntity(URL, responseType);
            }
        } catch (RestClientResponseException e) {
            result = new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        } catch (RestClientException e) {
            result = new ResponseEntity<>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            result = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (!result.getStatusCode().is2xxSuccessful()) {
            log.error("Attempt to get a response from a URL: '" + URL + "' failed - an error is received: " + result.getBody());
        }

        return result;
    }

    protected ResponseEntity<?> postToRemoteService(String URL, Map<String, String> request, Class<?> responseType) {

        ResponseEntity<?> result;

        try {
            result = restTemplate.postForEntity(URL, request, responseType);
        } catch (HttpClientErrorException e) {
            result = new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        } catch (ResourceAccessException e) {
            result = new ResponseEntity<>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            result = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (!result.getStatusCode().is2xxSuccessful()) {
            log.error("Attempt to post a request to a URL: '" + URL + "' failed - an error is received: " + result.getBody());
        }

        return result;
    }
}
