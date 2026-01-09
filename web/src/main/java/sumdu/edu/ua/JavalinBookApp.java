package sumdu.edu.ua;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.jdbc.JdbcBookRepository;
import sumdu.edu.ua.jdbc.JdbcCommentRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavalinBookApp {

    public static void main(String[] args) {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:file:./data/guest");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        CatalogRepositoryPort bookRepo = new JdbcBookRepository(dataSource);
        CommentRepositoryPort commentRepo = new JdbcCommentRepository(jdbcTemplate);

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> cors.addRule(it -> it.anyHost()));
        }).start(8080);

        app.before(ctx -> {
            System.out.println("LOG: Incoming request " + ctx.method() + " " + ctx.path());
        });

        app.exception(Exception.class, (e, ctx) -> {
            e.printStackTrace(); // Лог помилки в консоль
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.json(Map.of("error", "Internal Server Error", "details", e.getMessage()));
        });

        app.get("/books", ctx -> {
            String q = ctx.queryParam("q");
            int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(0);
            int size = ctx.queryParamAsClass("size", Integer.class).getOrDefault(20);

            PageRequest pageRequest = new PageRequest(page, size);
            List<Book> books = bookRepo.search(q, pageRequest).getItems();

            ctx.json(books);
        });

        app.get("/books/{id}", ctx -> {
            long id = Long.parseLong(ctx.pathParam("id"));

            Book book = bookRepo.findById(id);
            if (book == null) {
                ctx.status(HttpStatus.NOT_FOUND).json(Map.of("error", "Book not found"));
                return;
            }

            PageRequest pageRequest = new PageRequest(0, 50);
            List<Comment> comments = commentRepo.list(id, null, null, pageRequest).getItems();

            Map<String, Object> result = new HashMap<>();
            result.put("book", book);
            result.put("comments", comments);

            ctx.json(result);
        });

        app.post("/comments", ctx -> {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);

            if (!body.containsKey("bookId") || !body.containsKey("author") || !body.containsKey("text")) {
                ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("error", "Missing fields"));
                return;
            }

            long bookId = ((Number) body.get("bookId")).longValue();
            String author = (String) body.get("author");
            String text = (String) body.get("text");

            commentRepo.add(bookId, author, text);

            ctx.status(HttpStatus.CREATED).json(Map.of("status", "created", "bookId", bookId));
        });

        System.out.println("Server started on http://localhost:8080/");
    }
}
