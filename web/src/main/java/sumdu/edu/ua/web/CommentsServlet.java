package sumdu.edu.ua.web;

import sumdu.edu.ua.config.Beans;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.PageRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

public class CommentsServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(CommentsServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String bookIdStr = req.getParameter("bookId");
        if (bookIdStr == null) {
            log.warn("GET /comments - missing bookId parameter");
            resp.sendRedirect(req.getContextPath() + "/books");
            return;
        }

        try {
            long bookId = Long.parseLong(bookIdStr);
            log.info("GET /comments - bookId={}", bookId);

            Book book = Beans.getBookRepo().findById(bookId);

            if (book == null) {
                log.warn("GET /comments - book #{} not found", bookId);
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
                return;
            }

            // Параметри фільтрації
            String authorFilter = req.getParameter("author");
            String sinceStr = req.getParameter("since");
            Instant since = null;
            if (sinceStr != null && !sinceStr.isBlank()) {
                try {
                    since = Instant.parse(sinceStr);
                } catch (Exception e) {
                    log.warn("Invalid 'since' parameter: {}", sinceStr);
                }
            }

            PageRequest pageRequest = new PageRequest(0, 20);
            List<Comment> comments = Beans.getCommentRepo()
                    .list(bookId, authorFilter, since, pageRequest)
                    .getItems();

            req.setAttribute("book", book);
            req.setAttribute("comments", comments);
            req.getRequestDispatcher("/WEB-INF/views/book-comments.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            log.warn("GET /comments - invalid bookId: {}", bookIdStr);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid book ID");

        } catch (Exception e) {
            log.error("GET /comments - error loading book details", e);
            throw new ServletException("Cannot load book details", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        req.setCharacterEncoding("UTF-8");
        String method = req.getParameter("_method");

        // Видалення коментаря
        if ("delete".equalsIgnoreCase(method)) {
            handleDelete(req, resp);
            return;
        }

        // Додавання коментаря
        handleAdd(req, resp);
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        String bookIdStr = req.getParameter("bookId");
        String author = req.getParameter("author");
        String text = req.getParameter("text");

        if (bookIdStr == null) {
            log.warn("POST /comments - missing bookId");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Book ID is required");
            return;
        }

        try {
            long bookId = Long.parseLong(bookIdStr);

            // Валідація
            if (author == null || author.isBlank()) {
                log.warn("POST /comments - empty author for book {}", bookId);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Author is required");
                return;
            }

            if (text == null || text.isBlank()) {
                log.warn("POST /comments - empty text for book {}", bookId);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Comment text is required");
                return;
            }

            if (author.length() > 64) {
                log.warn("POST /comments - author too long ({} chars) for book {}",
                        author.length(), bookId);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Author name too long (max 64 characters)");
                return;
            }

            if (text.length() > 1000) {
                log.warn("POST /comments - text too long ({} chars) for book {}",
                        text.length(), bookId);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Comment too long (max 1000 characters)");
                return;
            }

            // Перевіряємо чи існує книга
            Book book = Beans.getBookRepo().findById(bookId);
            if (book == null) {
                log.warn("POST /comments - book #{} not found", bookId);
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
                return;
            }

            // Зберігаємо коментар
            Beans.getCommentRepo().add(bookId, author.trim(), text.trim());

            log.info("POST /comments - added comment for book #{} by '{}'",
                    bookId, author.trim());

            resp.sendRedirect(req.getContextPath() + "/comments?bookId=" + bookId);

        } catch (NumberFormatException e) {
            log.warn("POST /comments - invalid bookId: {}", bookIdStr);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid book ID");

        } catch (Exception e) {
            log.error("POST /comments - error saving comment", e);
            throw new ServletException("Cannot save comment", e);
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        String bookIdStr = req.getParameter("bookId");
        String commentIdStr = req.getParameter("commentId");

        if (bookIdStr == null || commentIdStr == null) {
            log.warn("DELETE /comments - missing parameters");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Book ID and Comment ID are required");
            return;
        }

        try {
            long bookId = Long.parseLong(bookIdStr);
            long commentId = Long.parseLong(commentIdStr);

            log.info("DELETE /comments - deleting comment #{} for book #{}",
                    commentId, bookId);

            Beans.getCommentRepo().delete(bookId, commentId);

            resp.sendRedirect(req.getContextPath() + "/comments?bookId=" + bookId);

        } catch (NumberFormatException e) {
            log.warn("DELETE /comments - invalid IDs: bookId={}, commentId={}",
                    bookIdStr, commentIdStr);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid IDs");

        } catch (Exception e) {
            log.error("DELETE /comments - error deleting comment", e);
            throw new ServletException("Cannot delete comment", e);
        }
    }
}