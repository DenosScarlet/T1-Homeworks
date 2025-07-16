# 1. Погода
1. Кафка поднята в _docker_ с помощью _docker-compose_.
   ```yaml
   services:
    kafka:
      image: bitnami/kafka:latest
      ports:
        - "9092:9092"
      environment:
        - KAFKA_CFG_NODE_ID=0
        - KAFKA_CFG_PROCESS_ROLES=controller,broker
        - KAFKA_CFG_LISTENERS=PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093
        - KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT
        - KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=0@kafka:9093
        - KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER
        - KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092
        - ALLOW_PLAINTEXT_LISTENER=yes
   ```
2. Использована автоконфигурация кафки с помощью _application.yaml_.
   ```yaml
   spring:
    kafka:
      template:
        default-topic: weather-topic
      producer:
        bootstrap-servers: localhost:9092
        key-serializer: org.apache.kafka.common.serialization.StringSerializer
        value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
        properties:
          enable.idempotence: true
      consumer:
        group-id: weather-group
        bootstrap-servers: localhost:9092
        auto-offset-reset: earliest
        key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
        value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
        properties:
          spring.json.trusted.packages: '*'
          spring.json.value.default.type: com.denos.weather.WeatherReport
   ```
3. Создан класс *WeatherProducer*, который, используя логику генерирования данных о погоде (*WeatherReportGenerator*), посылает сообщения о погоде в топик *weather-report* каждые 2 секунды.
   Тайминг отправки реализован с помощью аннотации *@EnableScheduling* над главным классом и *@Scheduled(fixedRate = 2000)* с указанием времени над методом *sendMessage()* в классе *WeatherProducer*.
   ```java
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
   ```
   ```java
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
   ```
   
4. Создан класс WeatherConsumer, слушающий топик *weather-report* и с помощью класса _WeatherReportAnalyser_ выводящий данные о погоде в консоль.
   ```java
   @Service
   public class WeatherConsumer {

     WeatherReportAnalyser analyser = new WeatherReportAnalyser();
  
     @KafkaListener(topics = "weather-topic")
     public void listen(WeatherReport weatherReport) {
       analyser.analyse(weatherReport);
     }
   }
   ```

   ```java
   @Service
   public class WeatherReportAnalyser {
     public void analyse(WeatherReport weatherReport) {
       System.out.println("В городе " + weatherReport.city() + " " + weatherReport.temperature() + "°C. Состояние: " + weatherReport.status());
     }
   }
   ```

Пример вывода:

<img width="450" height="232" alt="image" src="https://github.com/user-attachments/assets/ec696855-7e47-4e4c-967b-f71c732fb7a4" />

