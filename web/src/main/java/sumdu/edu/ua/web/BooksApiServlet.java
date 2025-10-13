package sumdu.edu.ua.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import sumdu.edu.ua.config.Beans;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BooksApiServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(BooksApiServlet.class);
    private final ObjectMapper om = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        int page = parseInt(req.getParameter("page"), 0);
        int size = parseInt(req.getParameter("size"), 10);
        String q = req.getParameter("q");
        String sort = req.getParameter("sort");

        log.info("GET /api/books - page={}, size={}, q='{}', sort='{}'", page, size, q, sort);

        try {
            PageRequest pageRequest = new PageRequest(page, size);
            Page<Book> result = Beans.getBookRepo().search(q, pageRequest);

            om.writeValue(resp.getWriter(), result);

        } catch (IllegalArgumentException e) {
            log.warn("Bad request: {}", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeError(resp, "Invalid parameters: " + e.getMessage());

        } catch (Exception e) {
            log.error("DB error while GET /api/books", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeError(resp, "Database error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        req.setCharacterEncoding("UTF-8");

        try {
            // Читаємо JSON з тіла запиту
            Map<String, Object> data = om.readValue(req.getInputStream(), Map.class);

            String title = (String) data.get("title");
            String author = (String) data.get("author");
            Integer pubYear = (Integer) data.get("pubYear");

            // Валідація
            if (title == null || title.isBlank()) {
                log.warn("POST /api/books - missing title");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeError(resp, "Title is required");
                return;
            }

            if (author == null || author.isBlank()) {
                log.warn("POST /api/books - missing author");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeError(resp, "Author is required");
                return;
            }

            if (pubYear == null || pubYear <= 0) {
                log.warn("POST /api/books - invalid pubYear: {}", pubYear);
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeError(resp, "Valid publication year is required");
                return;
            }

            // Зберігаємо
            Book saved = Beans.getBookRepo().add(
                    title.trim(),
                    author.trim(),
                    pubYear
            );

            log.info("POST /api/books - created book #{}: '{}' by {}",
                    saved.getId(), saved.getTitle(), saved.getAuthor());

            resp.setStatus(HttpServletResponse.SC_CREATED);
            om.writeValue(resp.getWriter(), saved);

        } catch (Exception e) {
            log.error("DB error while POST /api/books", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeError(resp, "Database error");
        }
    }

    private void writeError(HttpServletResponse resp, String message) throws IOException {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        om.writeValue(resp.getWriter(), error);
    }

    private int parseInt(String s, int def) {
        try {
            return (s != null) ? Integer.parseInt(s) : def;
        } catch (NumberFormatException e) {
            return def;
        }
    }
}