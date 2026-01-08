package sumdu.edu.ua;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Головний клас Spring Boot застосунку
 * @SpringBootApplication включає:
 * - @Configuration: позначає клас як джерело конфігурації бінів
 * - @EnableAutoConfiguration: вмикає автоконфігурацію Spring Boot
 * - @ComponentScan: сканує пакети для пошуку компонентів (@Component, @Service, @Repository, @Controller)
 */
@SpringBootApplication(scanBasePackages = {
    "sumdu.edu.ua.web",
    "sumdu.edu.ua.config",
    "sumdu.edu.ua.core.service",
    "sumdu.edu.ua.jdbc"
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("\n=== Spring Boot Application Started ===");
        System.out.println("Application available at: http://localhost:8080/books\n");
    }
}

