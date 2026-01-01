package org.example.weatherapp.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.rmi.RemoteException;

class WeatherServiceImplTest {

    private WeatherServiceImpl weatherService;

    @BeforeEach
    void setUp() {
        try {
            weatherService = new WeatherServiceImpl();
        } catch (RemoteException e) {
            fail("Failed to instantiate WeatherServiceImpl: " + e.getMessage());
        }
    }

    @Test
    void getWeatherJson_shouldReturnNonEmptyJsonForValidCity() {
        try {
            // This is more of an integration test as it calls the real API
            String json = weatherService.getWeatherJson("London");
            assertNotNull(json, "JSON string should not be null.");
            assertNotEquals("{}", json, "JSON string should not be an empty object.");
            assertTrue(json.contains("\"name\":\"London\""), "JSON should contain the city name 'London'.");
        } catch (RemoteException e) {
            fail("RemoteException was thrown: " + e.getMessage());
        }
    }

    @Test
    void getForecastJson_shouldReturnNonEmptyJsonForValidCity() {
        try {
            // This is also an integration test
            String json = weatherService.getForecastJson("Paris");
            assertNotNull(json, "Forecast JSON should not be null.");
            assertNotEquals("{}", json, "Forecast JSON should not be an empty object.");
            assertTrue(json.contains("\"city\":{\"name\":\"Paris\"}"), "Forecast JSON should contain the city name 'Paris'.");
        } catch (RemoteException e) {
            fail("RemoteException was thrown: " + e.getMessage());
        }
    }

    @Test
    void getWeatherJson_shouldReturnEmptyJsonForInvalidCity() {
        try {
            String json = weatherService.getWeatherJson("InvalidCityName12345");
            assertNotNull(json);
            // The API returns a specific error object for "not found", which is not an empty object.
            // A better test would be to check for the "404" code inside the JSON.
            assertTrue(json.contains("\"cod\":\"404\""), "JSON for an invalid city should contain a '404' error code.");
        } catch (RemoteException e) {
            fail("RemoteException was thrown: " + e.getMessage());
        }
    }
}
