package sumdu.edu.ua.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

import java.util.Map;

@RestController
@RequestMapping("/comments")
public class CommentsController {

    private static final Logger log = LoggerFactory.getLogger(CommentsController.class);

    @Autowired
    private CatalogRepositoryPort bookRepository;

    @Autowired
    private CommentRepositoryPort commentRepository;

    /**
     * POST /comments
     * Додає новий коментар до книги.
     * Очікує JSON: { "bookId": 1, "author": "name", "text": "comment" }
     */
    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody Map<String, Object> payload) {
        log.info("REST POST /comments called with payload: {}", payload);

        try {
            // 1. Валідація та отримання даних з JSON
            Object bookIdObj = payload.get("bookId");
            String author = (String) payload.get("author");
            String text = (String) payload.get("text");

            if (bookIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "bookId is required"));
            }

            long bookId;
            if (bookIdObj instanceof Number) {
                bookId = ((Number) bookIdObj).longValue();
            } else {
                try {
                    bookId = Long.parseLong(bookIdObj.toString());
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid bookId format"));
                }
            }

            if (author == null || author.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Author is required"));
            }

            if (text == null || text.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Text is required"));
            }

            Book book = bookRepository.findById(bookId);
            if (book == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Book not found with id: " + bookId));
            }

            commentRepository.add(bookId, author, text);
            log.info("Comment added to book #{}", bookId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Comment added successfully", "bookId", bookId));

        } catch (Exception e) {
            log.error("Error adding comment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Server error: " + e.getMessage()));
        }
    }
}
