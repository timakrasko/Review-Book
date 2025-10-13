package core.domain;

import java.time.Instant;
import java.time.LocalDateTime;

public class Comment {
    private final Long id;
    private final Long bookId;
    private final String author;
    private final String text;
    private final Instant createdAt;
}
