package com.denos.weather;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final WeatherReportGenerator weatherReportGenerator;

    @Scheduled(fixedRate = 2000)
    public void sendMessage(){
        WeatherReport weatherReport = weatherReportGenerator.generateWeatherReport();
        kafkaTemplate.send("weather-topic", weatherReport);
    }
}
