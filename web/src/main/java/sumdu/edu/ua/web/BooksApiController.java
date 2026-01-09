package sumdu.edu.ua.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;

import java.util.Map;

/**
 * REST API контролер для роботи з книгами
 * @RestController = @Controller + @ResponseBody
 * Демонструє ін'єкцію залежностей через конструктор та через поле
 */
@RestController
@RequestMapping("/api/books")
public class BooksApiController {

    private static final Logger log = LoggerFactory.getLogger(BooksApiController.class);

    @Autowired
    private CatalogRepositoryPort bookRepository;

    private final ObjectMapper objectMapper;

    public BooksApiController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Отримує список книг з пагінацією та пошуком
     */
    @GetMapping
    public ResponseEntity<?> getBooks(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String sort) {

        log.info("GET /api/books - page={}, size={}, q='{}', sort='{}'", page, size, q, sort);

        try {
            PageRequest pageRequest = new PageRequest(page, size);
            Page<Book> result = bookRepository.search(q, pageRequest);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("Bad request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid parameters: " + e.getMessage()));
        } catch (Exception e) {
            log.error("DB error while GET /api/books", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Database error"));
        }
    }

    /**
     * Створює нову книгу
     */
    @PostMapping
    public ResponseEntity<?> createBook(@RequestBody Map<String, Object> data) {
        try {
            String title = (String) data.get("title");
            String author = (String) data.get("author");
            Integer pubYear = data.get("pubYear") instanceof Number 
                    ? ((Number) data.get("pubYear")).intValue() 
                    : null;

            if (title == null || title.isBlank()) {
                log.warn("POST /api/books - missing title");
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Title is required"));
            }

            if (author == null || author.isBlank()) {
                log.warn("POST /api/books - missing author");
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Author is required"));
            }

            if (pubYear == null || pubYear <= 0) {
                log.warn("POST /api/books - invalid pubYear: {}", pubYear);
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Valid publication year is required"));
            }

            Book saved = bookRepository.add(
                    title.trim(),
                    author.trim(),
                    pubYear
            );

            log.info("POST /api/books - created book #{}: '{}' by {}",
                    saved.getId(), saved.getTitle(), saved.getAuthor());

            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            log.error("DB error while POST /api/books", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Database error"));
        }
    }
}

