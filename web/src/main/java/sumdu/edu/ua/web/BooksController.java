package sumdu.edu.ua.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BooksController {

    @Autowired
    private CatalogRepositoryPort bookRepository;

    // 1. Відображення списку книг
    @GetMapping
    public String listBooks(Model model) {
        PageRequest pageRequest = new PageRequest(0, 100);
        List<Book> books = bookRepository.search(null, pageRequest).getItems();

        // Передаємо список книг у шаблон
        model.addAttribute("books", books);

        // Повертаємо назву шаблону (books.html)
        return "books";
    }

    // 2. Форма додавання книги (GET)
    @GetMapping("/add")
    public String showAddForm(Model model) {
        // Створюємо порожній об'єкт для прив'язки полів форми
        model.addAttribute("book", new Book(null, "", "", 0));
        return "book-form";
    }

    // 3. Обробка форми (POST)
    @PostMapping("/add")
    public String saveBook(@ModelAttribute("book") Book book) {
        // Зберігаємо книгу через репозиторій
        bookRepository.add(book.getTitle(), book.getAuthor(), book.getPubYear());

        // Редірект на список після успішного збереження
        return "redirect:/books";
    }
}

