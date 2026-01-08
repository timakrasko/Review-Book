package sumdu.edu.ua.web;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import sumdu.edu.ua.core.domain.Book;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final EmailTemplateProcessor templateProcessor;

    // Читаємо e-mail відправника з properties, щоб не хардкодити
    @Value("${spring.mail.username}")
    private String senderEmail;

    public MailService(JavaMailSender mailSender, EmailTemplateProcessor templateProcessor) {
        this.mailSender = mailSender;
        this.templateProcessor = templateProcessor;
    }

    public void sendNewBookEmail(Book book) {
        log.info("Preparing to send email for book: {}", book.getTitle());

        // 1. Формування моделі для шаблону
        Map<String, Object> model = new HashMap<>();
        model.put("title", book.getTitle());
        model.put("author", book.getAuthor());
        model.put("year", book.getPubYear()); // Переконайтеся, що у Book метод називається getPubYear() або getYear()
        model.put("added", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        // 2. Рендеринг HTML
        String htmlContent = templateProcessor.process("new_book.ftl", model);

        // 3. Відправка листа
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // MULTIPART_MODE_MIXED_RELATED дозволяє вставляти картинки та стилі
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(senderEmail); // Для тесту відправляємо самі собі
            helper.setSubject("📚 Нова книга: " + book.getTitle());
            helper.setText(htmlContent, true); // true означає, що це HTML

            mailSender.send(message);
            log.info("Email sent successfully!");

        } catch (Exception e) {
            log.error("Failed to send email", e);
            // Не кидаємо виняток далі, щоб не блокувати збереження книги у разі помилки пошти
        }
    }
}
