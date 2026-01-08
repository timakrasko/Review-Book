package sumdu.edu.ua.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.core.service.CommentService;

import java.time.Instant;
import java.util.List;

/**
 * Контролер для роботи з коментарями
 * Демонструє ін'єкцію залежностей через конструктор та через поле
 */
@Controller
@RequestMapping("/comments")
public class CommentsController {

    private static final Logger log = LoggerFactory.getLogger(CommentsController.class);

    // DI через поле
    @Autowired
    private CatalogRepositoryPort bookRepository;

    @Autowired
    private CommentRepositoryPort commentRepository;

    // DI через конструктор (для сервісу)
    private final CommentService commentService;

    public CommentsController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Відображає коментарі до книги
     */
    @GetMapping
    public String listComments(
            @RequestParam long bookId,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String since,
            Model model) {

        if (bookId <= 0) {
            log.warn("GET /comments - invalid bookId: {}", bookId);
            return "redirect:/books";
        }

        try {
            log.info("GET /comments - bookId={}", bookId);

            Book book = bookRepository.findById(bookId);
            if (book == null) {
                log.warn("GET /comments - book #{} not found", bookId);
                return "error/404";
            }

            Instant sinceInstant = null;
            if (since != null && !since.isBlank()) {
                try {
                    sinceInstant = Instant.parse(since);
                } catch (Exception e) {
                    log.warn("Invalid 'since' parameter: {}", since);
                }
            }

            PageRequest pageRequest = new PageRequest(0, 20);
            List<Comment> comments = commentRepository
                    .list(bookId, author, sinceInstant, pageRequest)
                    .getItems();

            model.addAttribute("book", book);
            model.addAttribute("comments", comments);
            return "book-comment"; // повертає назву view (book-comment.jsp)

        } catch (NumberFormatException e) {
            log.warn("GET /comments - invalid bookId: {}", bookId);
            return "error/400";
        } catch (Exception e) {
            log.error("GET /comments - error loading book details", e);
            throw new RuntimeException("Cannot load book details", e);
        }
    }

    /**
     * Додає новий коментар або видаляє (залежно від _method)
     */
    @PostMapping
    public String handlePost(
            @RequestParam(required = false) String _method,
            @RequestParam long bookId,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Long commentId,
            @RequestParam(required = false) String createdAt) {

        // Обробка видалення через _method=delete
        if ("delete".equalsIgnoreCase(_method) && commentId != null && createdAt != null) {
            return deleteComment(bookId, commentId, createdAt);
        }

        // Обробка додавання коментаря
        return addComment(bookId, author, text);
    }

    /**
     * Додає новий коментар
     */
    private String addComment(long bookId, String author, String text) {

        if (bookId <= 0) {
            log.warn("POST /comments - missing or invalid bookId");
            return "redirect:/books";
        }

        try {
            if (author == null || author.isBlank()) {
                log.warn("POST /comments - empty author for book {}", bookId);
                return "redirect:/comments?bookId=" + bookId + "&error=author_required";
            }

            if (text == null || text.isBlank()) {
                log.warn("POST /comments - empty text for book {}", bookId);
                return "redirect:/comments?bookId=" + bookId + "&error=text_required";
            }

            if (author.length() > 64) {
                log.warn("POST /comments - author too long ({} chars) for book {}",
                        author.length(), bookId);
                return "redirect:/comments?bookId=" + bookId + "&error=author_too_long";
            }

            if (text.length() > 1000) {
                log.warn("POST /comments - text too long ({} chars) for book {}",
                        text.length(), bookId);
                return "redirect:/comments?bookId=" + bookId + "&error=text_too_long";
            }

            Book book = bookRepository.findById(bookId);
            if (book == null) {
                log.warn("POST /comments - book #{} not found", bookId);
                return "redirect:/books";
            }

            commentRepository.add(bookId, author.trim(), text.trim());

            log.info("POST /comments - added comment for book #{} by '{}'",
                    bookId, author.trim());

            return "redirect:/comments?bookId=" + bookId;

        } catch (Exception e) {
            log.error("POST /comments - error saving comment", e);
            return "redirect:/comments?bookId=" + bookId + "&error=save_failed";
        }
    }

    /**
     * Видаляє коментар
     */
    private String deleteComment(long bookId, long commentId, String createdAt) {

        if (bookId <= 0 || commentId <= 0) {
            log.warn("DELETE /comments - missing parameters");
            return "redirect:/books";
        }

        try {
            log.info("DELETE /comments - deleting comment #{} for book #{}",
                    commentId, bookId);

            Instant createdAtInstant = Instant.parse(createdAt);
            commentService.delete(bookId, commentId, createdAtInstant);

            return "redirect:/comments?bookId=" + bookId;

        } catch (IllegalStateException e) {
            log.warn("DELETE /comments - cannot delete: {}", e.getMessage());
            return "redirect:/comments?bookId=" + bookId + "&error=too_old";
        } catch (Exception e) {
            log.error("DELETE /comments - error deleting comment", e);
            return "redirect:/comments?bookId=" + bookId + "&error=delete_failed";
        }
    }
}

