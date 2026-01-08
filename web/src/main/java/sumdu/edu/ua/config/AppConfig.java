package sumdu.edu.ua.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфігураційний клас Spring
 * @Configuration позначає клас як джерело конфігурації бінів
 * 
 * Демонструє створення кастомного біна через метод з анотацією @Bean
 */
@Configuration
public class AppConfig {

    /**
     * Кастомний бін для ObjectMapper
     * @Bean позначає метод як фабрику бінів
     * Spring автоматично викличе цей метод і зареєструє результат як бін
     * 
     * @return налаштований ObjectMapper для JSON серіалізації/десеріалізації
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper;
    }
}

