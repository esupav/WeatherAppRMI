package org.example.weatherapp.server;

import org.example.weatherapp.shared.WeatherService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) {
        try {
            WeatherService weatherService = new WeatherServiceImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("WeatherService", weatherService);
            System.out.println("Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
