package sumdu.edu.ua.jdbc;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component

public class DbInit {

    private static final Logger log = LoggerFactory.getLogger(DbInit.class);

    private final JdbcTemplate jdbcTemplate;



// DI через конструктор

    public DbInit(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;

    }



    /**

     * Ініціалізує схему бази даних при старті застосунку

     * Виконується автоматично після створення біна

     */

    @PostConstruct

    public void init() {

        try {

            ClassPathResource resource = new ClassPathResource("schema.sql");

            String sql = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);


            String[] commands = sql.split(";");

            for (String cmd : commands) {

                if (!cmd.isBlank()) {

                    jdbcTemplate.execute(cmd.trim());

                }

            }

            log.info("Database schema initialized successfully");

        } catch (Exception e) {

            log.error("Failed to initialize database schema", e);

            throw new RuntimeException("DB schema init failed", e);

        }

    }

}

