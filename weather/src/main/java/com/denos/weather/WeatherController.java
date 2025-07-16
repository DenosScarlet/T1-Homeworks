package com.denos.weather;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    @PostMapping("/weather")
    public void weatherReport() {}

}
