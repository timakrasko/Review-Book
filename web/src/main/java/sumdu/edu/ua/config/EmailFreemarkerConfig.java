package sumdu.edu.ua.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailFreemarkerConfig {

    @Bean("mailFreemarkerConfiguration")
    public freemarker.template.Configuration freemarkerEmailConfig() {
        try {
            freemarker.template.Configuration cfg =
                    new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_32);

            cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "mail-templates");
            cfg.setDefaultEncoding("UTF-8");
            cfg.setLocalizedLookup(false);
            return cfg;
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure FreeMarker for emails", e);
        }
    }
}
