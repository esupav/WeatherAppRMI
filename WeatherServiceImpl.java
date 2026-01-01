package org.example.weatherapp;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class WeatherServiceImpl extends UnicastRemoteObject implements WeatherService {

    public WeatherServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String getWeather(String city) throws RemoteException {
        // In a real application, you would fetch weather data from an API
        return "The weather in " + city + " is sunny.";
    }
}
