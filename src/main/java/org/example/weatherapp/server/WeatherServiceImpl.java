package org.example.weatherapp.server;

import org.example.weatherapp.shared.WeatherService;
import org.json.simple.JSONArray;
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

    // Reverting to the known working free API Key
    private static final String API_KEY = "9e1dc5cd71b67bc9790decaf552bf82c";

    public WeatherServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String getWeatherJson(String city) throws RemoteException {
        try {
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8.toString());
            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedCity + "&units=metric&appid=" + API_KEY;
            JSONObject json = readJsonFromUrl(url);
            return json != null ? json.toJSONString() : "{}";
        } catch (Exception e) {
            throw new RemoteException("Error fetching weather data", e);
        }
    }

    @Override
    public String getForecastJson(String city) throws RemoteException {
        try {
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8.toString());
            // Reverting to standard forecast endpoint (5 days / 3 hours)
            String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + encodedCity + "&units=metric&appid=" + API_KEY;
            JSONObject json = readJsonFromUrl(url);
            return json != null ? json.toJSONString() : "{}";
        } catch (Exception e) {
            throw new RemoteException("Error fetching forecast data", e);
        }
    }

    @Override
    public String getCityDescription(String city) throws RemoteException {
        return "";
    }

    @Override
    public String getCityFacts(String city) throws RemoteException {
        return "";
    }

    // --- Helper Methods ---

    private JSONObject readJsonFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            br.close();
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private double getDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }
}
