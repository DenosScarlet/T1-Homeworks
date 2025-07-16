package com.denos.weather;

public record WeatherReport(
        String city,
        Integer temperature,
        String status
) {
}
