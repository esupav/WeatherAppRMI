package org.example.weatherapp.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WeatherService extends Remote {
    String getWeatherJson(String city) throws RemoteException;
    String getForecastJson(String city) throws RemoteException;
    String getCityDescription(String city) throws RemoteException;
    String getCityFacts(String city) throws RemoteException;
}
