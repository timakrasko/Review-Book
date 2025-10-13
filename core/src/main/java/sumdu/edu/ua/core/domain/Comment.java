package sumdu.edu.ua.core.domain;

import java.time.Instant;

public class Comment {
    private final Long id;
    private final Long bookId;
    private final String author;
    private final String text;
    private final Instant createdAt;

    public Comment(Long id, Long bookId, String author, String text, Instant createdAt) {
        this.id = id;
        this.bookId = bookId;
        this.author = author;
        this.text = text;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
