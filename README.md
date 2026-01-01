<<<<<<< HEAD
# WeatherApp

A distributed Weather Application built using **JavaFX** for the client interface and **Java RMI** (Remote Method Invocation) for client-server communication. The application fetches real-time weather data and forecasts from the **OpenWeatherMap API**.

## Features

*   **Current Weather**: Displays temperature, humidity, wind speed, precipitation, and weather conditions for a specific city.
*   **5-Day Forecast**: Shows forecast details including min/max temperature, "feels like" temperature, pressure, cloudiness, and precipitation chance.
*   **Client-Server Architecture**:
    *   **Server**: Handles API requests to OpenWeatherMap and processes JSON data.
    *   **Client**: Renders the UI and communicates with the server via RMI.

## Prerequisites

*   **Java Development Kit (JDK)**: Version 17 or higher.
*   **JavaFX SDK**: Required if not bundled with your JDK.
*   **Internet Connection**: To fetch data from OpenWeatherMap.

## Project Structure

*   `org.example.weatherapp.client`: Contains JavaFX UI logic (`HelloApplication`, `FXMLDocumentController`, `ClientLauncher`).
*   `org.example.weatherapp.server`: Contains RMI Server implementation (`WeatherServiceImpl`, `ServerLauncher`).
*   `org.example.weatherapp`: Contains the shared RMI interface (`WeatherService`).

## How to Run

### 1. Compile the Project
Ensure all dependencies (JavaFX, JSON-Simple) are resolved via your build tool (Maven/Gradle) or classpath configuration.

### 2. Start the Server
Run the `ServerLauncher` class first. This starts the RMI registry and binds the Weather Service.

```bash
java org.example.weatherapp.server.ServerLauncher
```

### 3. Start the Client
Once the server is running, run the `ClientLauncher` class to open the application window.

```bash
java org.example.weatherapp.client.ClientLauncher
```

### 4. Usage
1.  Enter a city name (e.g., "Addis Ababa", "Dilla") in the search bar.
2.  Press Enter or click the search button.
3.  View the current weather and forecast. Click on forecast cards for more details.
=======
# WeatherAppRMI
This project is Weather App RMI using client-server and the client calls method from server through its given IP
>>>>>>> ea5310db8ee5a9fa75ca44ddbc8fce15b6a1ff08
