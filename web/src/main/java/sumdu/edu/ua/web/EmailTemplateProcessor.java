package sumdu.edu.ua.web;


import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.Map;

@Component
public class EmailTemplateProcessor {

    private final freemarker.template.Configuration cfg;

    // Вказуємо нове ім'я біна
    public EmailTemplateProcessor(@Qualifier("mailFreemarkerConfiguration") freemarker.template.Configuration cfg) {
        this.cfg = cfg;
    }

    public String process(String templateName, Map<String, Object> model) {
        try {
            // ... код без змін ...
            Template template = cfg.getTemplate(templateName);
            StringWriter writer = new StringWriter();
            template.process(model, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Cannot render email template: " + templateName, e);
        }
    }
}
