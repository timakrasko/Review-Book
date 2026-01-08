package sumdu.edu.ua.core.service;

import org.springframework.stereotype.Service;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

import java.time.Duration;
import java.time.Instant;

/**
 * Сервіс для роботи з коментарями
 * @Service позначає клас як сервіс (спеціалізований @Component)
 * Демонструє ін'єкцію залежностей через конструктор
 */
@Service
public class CommentService {
    private final CommentRepositoryPort repo;

    // DI через конструктор (рекомендований спосіб)
    public CommentService(CommentRepositoryPort repo) {
        this.repo = repo;
    }

    public void delete(long bookId, long commentId, Instant createdAt) {
        if (Duration.between(createdAt, Instant.now()).toHours() > 24) {
            throw new IllegalStateException("Comment too old to delete");
        }
        repo.delete(bookId, commentId);
    }
}
