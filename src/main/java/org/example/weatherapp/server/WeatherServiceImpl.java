package org.example.weatherapp.server;

import org.example.weatherapp.shared.WeatherService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class WeatherServiceImpl extends UnicastRemoteObject implements WeatherService {

    private static final String API_KEY = "9e1dc5cd71b67bc9790decaf552bf82c";

    public WeatherServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String getWeather(String city) throws RemoteException {
        try {
            String jsonString = getWeatherJson(city);
            // Basic parsing to return a simple string, can be enhanced
            if (jsonString != null && !jsonString.equals("{}")) {
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
                if (jsonObject.containsKey("weather")) {
                    return "Weather in " + city + ": " + jsonObject.get("weather");
                }
            }
        } catch (Exception e) {
            System.err.println("Error in getWeather: " + e.getMessage());
        }
        return "Weather data not available for " + city;
    }

    @Override
    public String getWeatherJson(String city) throws RemoteException {
        try {
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedCity + "&units=metric&appid=" + API_KEY;
            JSONObject json = readJsonFromUrl(url);
            return json != null ? json.toJSONString() : "{}";
        } catch (Exception e) {
            throw new RemoteException("Error fetching weather data for " + city, e);
        }
    }

    @Override
    public String getForecastJson(String city) throws RemoteException {
        try {
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
            String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + encodedCity + "&units=metric&appid=" + API_KEY;
            JSONObject json = readJsonFromUrl(url);
            return json != null ? json.toJSONString() : "{}";
        } catch (Exception e) {
            throw new RemoteException("Error fetching forecast data for " + city, e);
        }
    }

    @Override
    public String getCityDescription(String city) throws RemoteException {
        // Placeholder implementation
        return "A beautiful city.";
    }

    @Override
    public String getCityFacts(String city) throws RemoteException {
        // Placeholder implementation
        return "An interesting fact about this city.";
    }

    private JSONObject readJsonFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                JSONParser parser = new JSONParser();
                return (JSONObject) parser.parse(sb.toString());
            }
        } catch (Exception e) {
            System.err.println("Failed to read from URL: " + urlString + " | Error: " + e.getMessage());
            return null;
        }
    }
}
