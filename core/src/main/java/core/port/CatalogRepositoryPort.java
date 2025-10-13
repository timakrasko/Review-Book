package core.port;

import core.domain.Book;
import core.domain.Page;
import core.domain.PageRequest;

public interface CatalogRepositoryPort {
    Page<Book> search(String query, PageRequest request);
    Book findById(long id);
    public Book add(String title, String author, int pubYear);
}
