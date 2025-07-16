package com.denos.weather;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class WeatherConsumer {

    WeatherReportAnalyser analyser = new WeatherReportAnalyser();

    @KafkaListener(topics = "weather-topic")
    public void listen(WeatherReport weatherReport) {
        analyser.analyse(weatherReport);
    }
}
