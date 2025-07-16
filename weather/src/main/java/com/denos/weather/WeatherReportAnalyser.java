package com.denos.weather;

import org.springframework.stereotype.Service;

@Service
public class WeatherReportAnalyser {
    public void analyse(WeatherReport weatherReport) {
        System.out.println("В городе " + weatherReport.city() + " " + weatherReport.temperature() + "°C. Состояние: " + weatherReport.status());
    }
}
