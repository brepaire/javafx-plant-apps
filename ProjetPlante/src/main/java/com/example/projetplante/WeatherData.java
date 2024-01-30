package com.example.projetplante;

public class WeatherData {
    private String icon;
    private double temperature;

    public WeatherData(String icon, double temperature) {
        this.icon = icon;
        this.temperature = temperature;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}

