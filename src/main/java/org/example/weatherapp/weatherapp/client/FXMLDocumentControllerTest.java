package org.example.weatherapp.client;

import org.example.weatherapp.shared.WeatherService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.rmi.RemoteException;

@ExtendWith(ApplicationExtension.class)
class FXMLDocumentControllerTest {

    private FXMLDocumentController controller;
    private static MockWeatherService mockWeatherService;

    // A mock implementation of the WeatherService for testing purposes
    static class MockWeatherService implements WeatherService {
        @Override
        public String getWeather(String city) throws RemoteException {
            return "Mock weather for " + city;
        }

        @Override
        public String getWeatherJson(String city) throws RemoteException {
            if ("error".equalsIgnoreCase(city)) {
                return "{\"cod\":\"404\",\"message\":\"city not found\"}";
            }
            // A simplified, valid JSON response for testing
            return "{\"coord\":{\"lon\":-0.1257,\"lat\":51.5085},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"main\":{\"temp\":15.0,\"feels_like\":14.0,\"temp_min\":13.0,\"temp_max\":17.0,\"pressure\":1012,\"humidity\":60},\"wind\":{\"speed\":1.5,\"deg\":80},\"name\":\"London\",\"sys\":{\"country\":\"GB\"}}";
        }

        @Override
        public String getForecastJson(String city) throws RemoteException {
            return "{}"; // Return empty for now, can be expanded
        }

        @Override
        public String getCityDescription(String city) throws RemoteException {
            return "Mock description";
        }

        @Override
        public String getCityFacts(String city) throws RemoteException {
            return "Mock fact";
        }
    }

    @Start
    private void start(Stage stage) throws Exception {
        mockWeatherService = new MockWeatherService();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/weatherapp/OpenWeatherApp.fxml"));
        stage.setScene(new Scene(loader.load()));
        
        controller = loader.getController();
        controller.setWeatherService(mockWeatherService);
        
        stage.show();
    }

    @Test
    void initialize_withService_shouldLoadDefaultWeather() {
        // After startup, the controller should have loaded weather for "Dilla" (the default)
        // We need to wait for the UI thread to update.
        Platform.runLater(() -> {
            Label cityLabel = (Label) controller.cityField.getScene().lookup("#cityLabel");
            assertNotNull(cityLabel, "City label should be present.");
            // The mock service returns "London" for any valid city, including the default "Dilla"
            assertEquals("London, GB", cityLabel.getText(), "Default weather for 'Dilla' should be loaded on start.");
        });
    }

    @Test
    void searchWeather_withValidCity_shouldUpdateUI() {
        TextField cityField = (TextField) controller.cityField.getScene().lookup("#cityField");
        Label cityLabel = (Label) controller.cityField.getScene().lookup("#cityLabel");
        Label tempLabel = (Label) controller.cityField.getScene().lookup("#temperatureLabel");

        assertNotNull(cityField);
        assertNotNull(cityLabel);
        assertNotNull(tempLabel);

        // Simulate user typing "TestCity" and pressing Enter
        Platform.runLater(() -> {
            cityField.setText("TestCity");
            controller.searchWeather();
            
            // The mock service will return the "London" data for "TestCity"
            assertEquals("London, GB", cityLabel.getText());
            assertEquals("15.0°C", tempLabel.getText());
        });
    }

    @Test
    void searchWeather_withInvalidCity_shouldShowError() {
        TextField cityField = (TextField) controller.cityField.getScene().lookup("#cityField");
        Label cityLabel = (Label) controller.cityField.getScene().lookup("#cityLabel");

        assertNotNull(cityField);
        assertNotNull(cityLabel);

        // Simulate user typing "error" (which our mock service handles as a 404)
        Platform.runLater(() -> {
            cityField.setText("error");
            controller.searchWeather();
            
            assertEquals("City not found!", cityLabel.getText(), "An invalid city should display a 'City not found!' message.");
        });
    }
}
