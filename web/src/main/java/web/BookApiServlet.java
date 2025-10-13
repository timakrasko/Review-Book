package web;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.domain.Book;
import core.domain.PageRequest;
import core.port.CatalogRepositoryPort;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdbc.JdbcBookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class BooksApiServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(BooksApiServlet.class);

    private final CatalogRepositoryPort bookRepo = new JdbcBookRepository();
    private final ObjectMapper om = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        int page = parseInt(req.getParameter("page"), 0);
        int size = parseInt(req.getParameter("size"), 10);
        String q = req.getParameter("q");

        try {
            var result = bookRepo.search(q, new PageRequest(page, size));
            om.writeValue(resp.getWriter(), result);
        } catch (Exception e) {
            log.error("DB error while GET /api/books", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        try {
            Book book = om.readValue(req.getInputStream(), Book.class);

            if (book.getTitle() == null || book.getTitle().isBlank()
                    || book.getAuthor() == null || book.getAuthor().isBlank()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "title & author required");
                return;
            }

            if (book.getPubYear() <= 0) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid pubYear");
                return;
            }

            Book saved = bookRepo.add(
                    book.getTitle().trim(),
                    book.getAuthor().trim(),
                    book.getPubYear()
            );

            resp.setStatus(HttpServletResponse.SC_CREATED);
            om.writeValue(resp.getWriter(), saved);

        } catch (Exception e) {
            log.error("DB error while POST /api/books", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB error");
        }
    }


    private int parseInt(String s, int def) {
        try {
            return (s != null) ? Integer.parseInt(s) : def;
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
