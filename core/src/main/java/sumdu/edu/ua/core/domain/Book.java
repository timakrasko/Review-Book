package sumdu.edu.ua.core.domain;

public class Book {
    private final Long id;
    private final String title;
    private final String author;
    private final int pubYear;

    public Book(Long id, String title, String author, int pubYear) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.pubYear = pubYear;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getPubYear() {
        return pubYear;
    }
}