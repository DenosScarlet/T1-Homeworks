package com.denos.weather;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Random;

@Service
public class WeatherReportGenerator {
    ArrayList<String> cities = new ArrayList<>() {
        {
            add("Москва");
            add("Санкт-Петербург");
            add("Нижний Новгород");
            add("Самара");
        }
    };
    ArrayList<String> statuses = new ArrayList<>() {
        {
            add("Солнечно");
            add("Пасмурно");
            add("Дождь");
        }
    };
    Random random = new Random(123);

    public WeatherReport generateWeatherReport() {
        return new WeatherReport(
                cities.get(random.nextInt(cities.size())),
                random.nextInt(-30,31),
                statuses.get(random.nextInt(statuses.size()))
        );
    }
}
