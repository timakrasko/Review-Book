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

    @Autowired
    private MailService mailService; // Інжектимо наш поштовий сервіс

    @GetMapping
    public String listBooks(Model model) {
        PageRequest pageRequest = new PageRequest(0, 100);
        List<Book> books = bookRepository.search(null, pageRequest).getItems();
        model.addAttribute("books", books);
        return "books";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("book", new Book(null, "", "", 0));
        return "book-form";
    }

    @PostMapping("/add")
    public String saveBook(@ModelAttribute("book") Book book) {
        // 1. Зберігаємо книгу в БД
        Book savedBook = bookRepository.add(book.getTitle(), book.getAuthor(), book.getPubYear());

        // 2. Відправляємо e-mail (асинхронно в реальних проектах, але тут синхронно)
        mailService.sendNewBookEmail(savedBook);

        return "redirect:/books";
    }
}

