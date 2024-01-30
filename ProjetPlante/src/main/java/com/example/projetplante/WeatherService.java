package com.example.projetplante;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class WeatherService {
    private static final String API_KEY = "186e31cb8f1e362fdc5695dffcd74498";
    private static final String API_URL = "http://api.openweathermap.org/data/2.5/forecast?lat=%s&lon=%s&appid=%s&units=metric";

    public WeatherData getWeatherData(LocalDate date, double latitude, double longitude) throws IOException {
        URL url = new URL(String.format(API_URL, latitude, longitude, API_KEY));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("Erreur lors de la connexion à l'API météo : " + connection.getResponseMessage());
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            JsonObject jsonObject = new Gson().fromJson(reader, JsonObject.class);

            double temperatureSum = 0;
            int temperatureCount = 0;
            String icon = "";
            for (int i = 0; i < jsonObject.getAsJsonArray("list").size(); i++) {
                Instant timestamp = Instant.ofEpochSecond(jsonObject.getAsJsonArray("list").get(i).getAsJsonObject().get("dt").getAsLong());
                LocalDate forecastDate = LocalDate.ofInstant(timestamp, ZoneOffset.UTC);

                if (forecastDate.equals(date)) {
                    if (icon.isEmpty()) {
                        icon = jsonObject.getAsJsonArray("list").get(i).getAsJsonObject().get("weather").getAsJsonArray().get(0).getAsJsonObject().get("icon").getAsString();
                    }
                    double temperature = jsonObject.getAsJsonArray("list").get(i).getAsJsonObject().get("main").getAsJsonObject().get("temp").getAsDouble();
                    temperatureSum += temperature;
                    temperatureCount++;
                }
            }

            if (temperatureCount > 0) {
                double averageTemperature = temperatureSum / temperatureCount;
                return new WeatherData(icon, averageTemperature);
            } else {
                throw new IOException("Pas de météo disponible pour la date spécifiée");
            }

        } finally {
            connection.disconnect();
        }
    }
}
