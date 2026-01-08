package sumdu.edu.ua.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;

import java.util.List;

/**
 * Контролер для роботи з книгами (веб-інтерфейс)
 * @Controller позначає клас як Spring MVC контролер
 * Демонструє ін'єкцію залежностей через поле (@Autowired)
 */
@Controller
public class BooksController {

    private static final Logger log = LoggerFactory.getLogger(BooksController.class);

    @Autowired
    private CatalogRepositoryPort bookRepository;

    /**
     * Відображає список книг
     * @GetMapping відповідає за обробку GET запитів на /books
     */
    @GetMapping("/books")
    public String listBooks(Model model) {
        log.info("BooksController.listBooks() called");
        if (bookRepository == null) {
            log.error("bookRepository is NULL!");
            throw new IllegalStateException("Repository not injected");
        }
        try {
            log.info("Searching books with repository: {}", bookRepository.getClass().getName());
            PageRequest pageRequest = new PageRequest(0, 20);
            List<Book> books = bookRepository.search(null, pageRequest).getItems();
            log.info("Found {} books", books.size());
            model.addAttribute("books", books);
            return "books";
        } catch (Exception e) {
            log.error("Error loading books", e);
            throw new RuntimeException("Cannot load books", e);
        }
    }
}

