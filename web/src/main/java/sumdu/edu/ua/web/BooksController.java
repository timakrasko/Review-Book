package sumdu.edu.ua.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books")
public class BooksController {

    private static final Logger log = LoggerFactory.getLogger(BooksController.class);

    @Autowired
    private CatalogRepositoryPort bookRepository;

    @Autowired
    private CommentRepositoryPort commentRepository;

    /**
     * GET /books
     * Повертає список книг у форматі JSON.
     */
    @GetMapping
    public List<Book> listBooks() {
        log.info("REST GET /books called");

        // Отримуємо перші 20 книг (як приклад)
        PageRequest pageRequest = new PageRequest(0, 20);
        return bookRepository.search(null, pageRequest).getItems();
    }

    /**
     * GET /books/{id}
     * Повертає дані однієї книги з коментарями у форматі JSON.
     */
    @GetMapping("/{id}")
    public Map<String, Object> getBookWithComments(@PathVariable long id) {
        log.info("REST GET /books/{} called", id);

        Book book = bookRepository.findById(id);
        if (book == null) {
            throw new RuntimeException("Book not found with id: " + id);
        }

        PageRequest pageRequest = new PageRequest(0, 50); // Завантажуємо до 50 коментарів
        List<Comment> comments = commentRepository.list(id, null, null, pageRequest).getItems();

        Map<String, Object> response = new HashMap<>();
        response.put("book", book);
        response.put("comments", comments);

        return response;
    }
}

