package web;

import com.fasterxml.jackson.databind.ObjectMapper;
import db.CommentDao;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CommentsServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(CommentsServlet.class);
    private final CommentDao dao = new CommentDao();
    private final ObjectMapper om = new ObjectMapper();
    /**
     * GET /comments
     * Очікувано: 200 OK + JSON масив коментарів.
     * Помилка БД: 500 + "DB error".
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            om.writeValue(resp.getWriter(), dao.list());
        } catch (Exception e) {
            log.error("DB getting error", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB error");
        }
    }

/**
 * POST /comments
 * Очікувано: 204 No Content при успіху.
 * Валідація: 400 (порожні/надто довгі поля або некоректний todoId).
 * Помилка БД: 500 + "DB error".
 */
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        String author = req.getParameter("author");
        String text = req.getParameter("text");
        log.debug(author + " " + text);

        if (author == null || text == null || author.isBlank() || text.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "author or text is null or empty");
            return;
        }

        if (author.length() > 64 || text.length() > 64) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "author or text is to longer");
            return;
        }

        try {
            dao.add(author.trim(), text.trim());
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e){
            log.error("DB posting error", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB error");
        }
    }
}
