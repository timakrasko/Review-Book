package core.port;

import core.domain.Comment;
import core.domain.Page;
import core.domain.PageRequest;

import java.time.Instant;

public interface CommentRepositoryPort {
    void add(long bookId, String author, String text);
    Page<Comment> list(long bookId, String author, Instant since, PageRequest request);
    void delete(long bookId, long commentId);

}
