package sumdu.edu.ua.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;

/**
 * Сервіс для роботи з каталогом книг
 * Демонструє використання @Service та DI через поле
 */
@Service
public class CatalogService {
    private static final Logger log = LoggerFactory.getLogger(CatalogService.class);

    @Autowired
    private CatalogRepositoryPort repository;

    public Page<Book> searchBooks(String query, PageRequest pageRequest) {
        log.info("Searching books with query: '{}', page: {}, size: {}",
                query, pageRequest.getPage(), pageRequest.getSize());
        return repository.search(query, pageRequest);
    }

    public Book findById(long id) {
        log.info("Finding book by id: {}", id);
        Book book = repository.findById(id);
        if (book == null) {
            throw new IllegalArgumentException("Book not found: " + id);
        }
        return book;
    }

    public Book addBook(String title, String author, int pubYear) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Author cannot be empty");
        }
        if (pubYear < 1000 || pubYear > java.time.Year.now().getValue()) {
            throw new IllegalArgumentException("Invalid publication year");
        }

        log.info("Adding new book: '{}' by {}, year: {}", title, author, pubYear);
        return repository.add(title, author, pubYear);
    }
}
