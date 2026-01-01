package org.example.weatherapp.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.weatherapp.shared.WeatherService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        WeatherService weatherService = null;
        try {
            // Try to connect to the server.
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            weatherService = (WeatherService) registry.lookup("WeatherService");
            System.out.println("Successfully connected to the RMI server.");

            // --- Connection Successful: Load Main App ---
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/org/example/weatherapp/OpenWeatherApp.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            scene.getStylesheets().add(getClass().getResource("/org/example/weatherapp/styles.css").toExternalForm());

            FXMLDocumentController controller = fxmlLoader.getController();
            controller.setWeatherService(weatherService);

            stage.setTitle("Weather App");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            // --- MODIFIED: Print the full stack trace for debugging ---
            System.err.println("An error occurred while loading the application UI.");
            e.printStackTrace(); // This will give us the real error.

            // --- Connection Failed: Load Error Screen ---
            VBox errorRoot = new VBox(20);
            errorRoot.setAlignment(Pos.CENTER);
            errorRoot.setStyle("-fx-background-color: #f2f2f2;");
            
            Label errorTitle = new Label("Application Error");
            errorTitle.setStyle("-fx-font-size: 24px; -fx-text-fill: #d32f2f;");

            Label errorMessage = new Label("Could not load the main application UI.\nPlease check the console for errors and restart.");
            errorMessage.setStyle("-fx-font-size: 14px;");

            errorRoot.getChildren().addAll(errorTitle, errorMessage);
            
            Scene errorScene = new Scene(errorRoot, 500, 300);
            stage.setTitle("Error");
            stage.setScene(errorScene);
            stage.show();
        }
    }
}
