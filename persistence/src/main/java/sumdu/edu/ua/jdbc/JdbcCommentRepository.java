package sumdu.edu.ua.jdbc;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC реалізація репозиторію коментарів
 * Використовує Spring JdbcTemplate
 */
@Repository
public class JdbcCommentRepository implements CommentRepositoryPort {

    private static final Logger log = LoggerFactory.getLogger(JdbcCommentRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public JdbcCommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Comment> commentMapper = (rs, rowNum) -> new Comment(
            rs.getLong("id"),
            rs.getLong("book_id"),
            rs.getString("author"),
            rs.getString("text"),
            rs.getTimestamp("created_at").toInstant()
    );

    @Override
    public void add(long bookId, String author, String text) {
        jdbcTemplate.update(
                "INSERT INTO comments(book_id, author, text) VALUES (?, ?, ?)",
                bookId, author, text
        );
        log.info("DB: new comment for book={}, author='{}', len={}",
                bookId, author, text.length());
    }

    @Override
    public Page<Comment> list(long bookId, String author, Instant since, PageRequest request) {
        StringBuilder sql = new StringBuilder(
                "SELECT id, book_id, author, text, created_at FROM comments WHERE book_id = ?"
        );
        List<Object> params = new ArrayList<>();
        params.add(bookId);

        if (author != null && !author.isBlank()) {
            sql.append(" AND LOWER(author) LIKE ?");
            params.add("%" + author.toLowerCase() + "%");
        }
        if (since != null) {
            sql.append(" AND created_at >= ?");
            params.add(Timestamp.from(since));
        }

        sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");
        params.add(request.getSize());
        params.add(request.getPage() * request.getSize());

        List<Comment> items = jdbcTemplate.query(sql.toString(), commentMapper, params.toArray());

        // Підрахунок total
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM comments WHERE book_id = ?");
        List<Object> countParams = new ArrayList<>();
        countParams.add(bookId);

        if (author != null && !author.isBlank()) {
            countSql.append(" AND LOWER(author) LIKE ?");
            countParams.add("%" + author.toLowerCase() + "%");
        }
        if (since != null) {
            countSql.append(" AND created_at >= ?");
            countParams.add(Timestamp.from(since));
        }

        Long total = jdbcTemplate.queryForObject(countSql.toString(), Long.class, countParams.toArray());

        return new Page<>(items, request, total != null ? total : 0);
    }

    @Override
    public void delete(long bookId, long commentId) {
        int rows = jdbcTemplate.update(
                "DELETE FROM comments WHERE id=? AND book_id=?",
                commentId, bookId
        );
        if (rows > 0) {
            log.info("DB: deleted comment #{} for book {}", commentId, bookId);
        } else {
            log.warn("DB: comment #{} not found for book {}", commentId, bookId);
        }
    }
}