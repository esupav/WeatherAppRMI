package org.example.weatherapp.client;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.effect.DropShadow;
import javafx.util.Duration;
import org.example.weatherapp.shared.WeatherService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class FXMLDocumentController {

    // Main Card UI
    @FXML private TextField cityField;
    @FXML private Label cityLabel;
    @FXML private Label temperatureLabel;
    @FXML private Label descriptionLabel;
    @FXML private ImageView weatherIcon;
    @FXML private StackPane animationPane;
    @FXML private StackPane backgroundAnimationPane;
    @FXML private HBox forecastContainer;
    @FXML private Label humidityLabel;
    @FXML private Label windLabel;
    @FXML private Label precipLabel;
    
    // Detailed Forecast Pane UI
    @FXML private VBox forecastDetailPane;
    @FXML private Label detailDayLabel;
    @FXML private Label detailTempMinMaxLabel;
    @FXML private Label detailFeelsLikeLabel;
    @FXML private Label detailPressureLabel;
    @FXML private Label detailCloudinessLabel;
    @FXML private Label detailWindDirectionLabel;
    @FXML private Label detailPopLabel;

    private WeatherService weatherService;
    private final JSONParser parser = new JSONParser();

    public void setWeatherService(WeatherService weatherService) {
        this.weatherService = weatherService;
        initialize();
    }

    private void initialize() {
        cityField.setOnAction(event -> searchWeather());

        if (weatherService == null) {
            cityLabel.setText("Server Not Connected");
            cityField.setDisable(true);
            cityField.setPromptText("Connection failed");
            temperatureLabel.setText("");
            descriptionLabel.setText("Please start the server and restart the client.");
        } else {
            loadDefaultWeather();
        }
    }

    private void loadDefaultWeather() {
        new Thread(() -> fetchWeatherAndForecast("Dilla")).start();
    }

    @FXML
    private void searchWeather() {
        String city = cityField.getText().trim();
        if (city.isEmpty()) {
            cityLabel.setText("Enter a city name!");
            return;
        }
        new Thread(() -> fetchWeatherAndForecast(city)).start();
    }

    private void fetchWeatherAndForecast(String city) {
        if (weatherService == null) {
            Platform.runLater(() -> cityLabel.setText("Server not connected."));
            return;
        }

        try {
            String weatherJsonString = weatherService.getWeatherJson(city);
            JSONObject weatherData = (JSONObject) parser.parse(weatherJsonString);

            if (weatherData.isEmpty() || (weatherData.containsKey("cod") && !String.valueOf(weatherData.get("cod")).equals("200"))) {
                Platform.runLater(() -> {
                    clearUI();
                    cityLabel.setText("City not found!");
                });
                return;
            }

            String forecastJsonString = weatherService.getForecastJson(city);

            Platform.runLater(() -> {
                try {
                    updateWeatherUI(weatherData);
                    updateForecastUI(forecastJsonString);
                } catch (ParseException e) {
                    e.printStackTrace();
                    clearUI();
                    cityLabel.setText("Error parsing server data.");
                }
            });

        } catch (RemoteException | ParseException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                clearUI();
                cityLabel.setText("Error fetching data from server.");
            });
        }
    }

    private void clearUI() {
        temperatureLabel.setText("");
        descriptionLabel.setText("");
        humidityLabel.setText("");
        windLabel.setText("");
        precipLabel.setText("");
        weatherIcon.setImage(null);
        animationPane.getChildren().clear();
        backgroundAnimationPane.getChildren().clear();
        forecastContainer.getChildren().clear();
        if (forecastDetailPane != null) {
            forecastDetailPane.setVisible(false);
            forecastDetailPane.setManaged(false);
        }
    }

    private void updateWeatherUI(JSONObject data) {
        String name = (String) data.get("name");
        JSONObject sys = (JSONObject) data.get("sys");
        String country = (String) sys.get("country");
        JSONObject main = (JSONObject) data.get("main");
        double temp = getDouble(main.get("temp"));
        JSONArray weatherArray = (JSONArray) data.get("weather");
        JSONObject weatherObj = (JSONObject) weatherArray.get(0);
        String desc = (String) weatherObj.get("description");
        String icon = (String) weatherObj.get("icon");
        double humidity = getDouble(main.get("humidity"));
        JSONObject windObj = (JSONObject) data.get("wind");
        double wind = getDouble(windObj.get("speed"));

        double precip = 0.0;
        if (data.containsKey("rain")) {
            JSONObject rainObj = (JSONObject) data.get("rain");
            if (rainObj.containsKey("1h")) precip = getDouble(rainObj.get("1h"));
        } else if (data.containsKey("snow")) {
            JSONObject snowObj = (JSONObject) data.get("snow");
            if (snowObj.containsKey("1h")) precip = getDouble(snowObj.get("1h"));
        }

        cityLabel.setText(name + ", " + country);
        temperatureLabel.setText(String.format("%.1f°C", temp));
        descriptionLabel.setText(capitalize(desc));
        weatherIcon.setImage(new Image("https://openweathermap.org/img/wn/" + icon + "@2x.png"));
        humidityLabel.setText("Humidity: " + humidity + "%");
        windLabel.setText("Wind: " + wind + " m/s");
        
        updatePrecipLabel(precipLabel, precip, desc);

        showWeatherAnimation(desc);
    }

    private void updateForecastUI(String forecastJsonString) throws ParseException {
        forecastContainer.getChildren().clear();
        JSONObject json = (JSONObject) parser.parse(forecastJsonString);

        if (json.isEmpty() || !String.valueOf(json.get("cod")).equals("200")) return;

        JSONArray list = (JSONArray) json.get("list");
        
        String previousDate = "";
        int daysCount = 0;
        LocalDate today = LocalDate.now();

        for (int i = 0; i < list.size(); i++) {
            JSONObject obj = (JSONObject) list.get(i);
            String dtTxt = (String) obj.get("dt_txt");
            String currentDate = dtTxt.split(" ")[0];

            if (!currentDate.equals(previousDate)) {
                previousDate = currentDate;
                
                LocalDate date = LocalDate.parse(currentDate);
                
                if (date.equals(today)) {
                    continue;
                }

                JSONObject main = (JSONObject) obj.get("main");
                double temp = getDouble(main.get("temp"));
                double tempMin = getDouble(main.get("temp_min"));
                double tempMax = getDouble(main.get("temp_max"));
                double feelsLike = getDouble(main.get("feels_like"));
                double pressure = getDouble(main.get("pressure"));
                
                JSONObject clouds = (JSONObject) obj.get("clouds");
                double cloudiness = getDouble(clouds.get("all"));
                
                JSONObject windObj = (JSONObject) obj.get("wind");
                double wind = getDouble(windObj.get("speed"));
                double windDeg = getDouble(windObj.get("deg"));
                
                double pop = getDouble(obj.get("pop")) * 100;
                
                double precip = 0.0; 
                if (obj.containsKey("rain")) {
                     JSONObject rain = (JSONObject) obj.get("rain");
                     if (rain.containsKey("3h")) precip = getDouble(rain.get("3h"));
                }

                JSONArray weatherArray = (JSONArray) obj.get("weather");
                JSONObject weatherObj = (JSONObject) weatherArray.get(0);
                String desc = (String) weatherObj.get("description");
                String icon = (String) weatherObj.get("icon");

                String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

                VBox dayBox = createForecastCard(dayName, icon, temp, desc, tempMin, tempMax, feelsLike, pressure, cloudiness, windDeg, pop, precip);
                forecastContainer.getChildren().add(dayBox);

                daysCount++;
                if (daysCount >= 5) break;
            }
        }
    }

    private VBox createForecastCard(String dayName, String icon, double temp, String desc, 
                                    double min, double max, double feelsLike, double pressure, 
                                    double clouds, double windDeg, double pop, double precip) {
        VBox container = new VBox(5);
        container.setAlignment(Pos.BOTTOM_CENTER); 
        container.setPrefHeight(140); 
        container.setMinHeight(140);
        container.setMaxHeight(140);
        container.setPrefWidth(120); 

        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("day");
        card.setPrefSize(120, 115); 
        
        card.setOnMouseClicked(e -> showForecastDetails(dayName, desc, min, max, feelsLike, pressure, clouds, windDeg, pop, precip));
        card.setCursor(javafx.scene.Cursor.HAND);

        ImageView ic = new ImageView("https://openweathermap.org/img/wn/" + icon + "@2x.png");
        ic.setFitWidth(45);
        ic.setFitHeight(45);
        ic.getStyleClass().add("icon");

        Label t = new Label(String.format("%.0f°C", temp));
        t.getStyleClass().add("forecast-temp");

        Label descLabel = new Label(capitalize(desc));
        descLabel.getStyleClass().add("forecast-desc");
        descLabel.setWrapText(true);
        descLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label d = new Label(dayName);
        d.getStyleClass().add("forecast-date-outside");

        card.getChildren().addAll(ic, t, descLabel);
        container.getChildren().addAll(d, card);
        
        return container;
    }
    
    private void showForecastDetails(String day, String desc, double min, double max, double feelsLike, double pressure, double clouds, double windDeg, double pop, double precip) {
        forecastDetailPane.setVisible(true);
        forecastDetailPane.setManaged(true);
        
        detailDayLabel.setText("Detailed Forecast for " + day);
        detailTempMinMaxLabel.setText(String.format("Min/Max Temp: %.1f°C / %.1f°C", min, max));
        detailFeelsLikeLabel.setText(String.format("Feels Like: %.1f°C", feelsLike));
        detailPressureLabel.setText(String.format("Pressure: %.0f hPa", pressure));
        detailCloudinessLabel.setText(String.format("Cloudiness: %.0f%%", clouds));
        detailWindDirectionLabel.setText(String.format("Wind Direction: %.0f°", windDeg));
        detailPopLabel.setText(String.format("Precip. Chance: %.0f%%", pop));
    }
    
    private void updatePrecipLabel(Label label, double precip, String desc) {
        if (precip > 0) {
            label.setText("Precip: " + String.format("%.2f", precip) + " mm");
        } else {
            String lowerDesc = desc.toLowerCase();
            if (lowerDesc.contains("rain") || lowerDesc.contains("drizzle") || lowerDesc.contains("snow")) {
                 label.setText("Precip: Trace");
            } else {
                 label.setText("Precip: 0 mm");
            }
        }
    }

    private void showWeatherAnimation(String desc) {
        animationPane.getChildren().clear();
        backgroundAnimationPane.getChildren().clear();
        
        desc = desc.toLowerCase();
        
        if (desc.contains("rain")) {
            showRainAnimation(animationPane); 
        } else if (desc.contains("clear") || desc.contains("sun")) {
            Circle sun = new Circle(30, Color.GOLD);
            animationPane.getChildren().add(sun);
            RotateTransition rot = new RotateTransition(Duration.seconds(5), sun);
            rot.setByAngle(360);
            rot.setCycleCount(Animation.INDEFINITE);
            rot.play();
        } else if (desc.contains("cloud")) {
            for (int i = 0; i < 3; i++) {
                Circle c = new Circle(25, Color.LIGHTGRAY);
                c.setLayoutX(30 + (i * 40));
                c.setLayoutY(50);
                animationPane.getChildren().add(c);
                TranslateTransition tt = new TranslateTransition(Duration.seconds(6), c);
                tt.setFromX(-20);
                tt.setToX(20);
                tt.setCycleCount(Animation.INDEFINITE);
                tt.setAutoReverse(true);
                tt.play();
            }
        }
    }

    private void showRainAnimation(Pane pane) {
        pane.getChildren().clear();
        int dropCount = 40; 
        double width = pane.getWidth() > 0 ? pane.getWidth() : 150;
        double height = pane.getHeight() > 0 ? pane.getHeight() : 80;
        for (int i = 0; i < dropCount; i++) {
            Line drop = new Line(0, 0, 0, 5);
            drop.setStroke(Color.web("#b3e5fc"));
            drop.setOpacity(0.7);
            drop.setStrokeWidth(1);
            drop.setTranslateX(Math.random() * width - (width/2));
            drop.setTranslateY(Math.random() * height - (height/2));
            pane.getChildren().add(drop);
            TranslateTransition fall = new TranslateTransition(Duration.seconds(0.6 + Math.random() * 0.5), drop);
            fall.setByY(height);
            fall.setCycleCount(Animation.INDEFINITE);
            fall.setDelay(Duration.seconds(Math.random()));
            fall.setInterpolator(Interpolator.LINEAR);
            fall.play();
        }
    }

    private String capitalize(String t) {
        if (t == null || t.isEmpty()) return "";
        return t.substring(0, 1).toUpperCase() + t.substring(1);
    }

    private double getDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }
}
