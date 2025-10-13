package web;

import core.domain.Book;
import core.domain.PageRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdbc.JdbcBookRepository;

import java.io.IOException;
import java.util.List;

public class BooksServlet extends HttpServlet {

    private final JdbcBookRepository bookRepo = new JdbcBookRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            PageRequest pageRequest = new PageRequest(0, 20);

            List<Book> books = bookRepo.search(null, pageRequest).getItems();
            req.setAttribute("books", books);

            req.getRequestDispatcher("/WEB-INF/views/books.jsp").forward(req, resp);

        } catch (Exception e) {
            throw new ServletException("Cannot load books", e);
        }
    }
}
