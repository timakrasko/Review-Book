package config;

import core.port.CatalogRepositoryPort;
import core.port.CommentRepositoryPort;
import core.service.CommentService;
import jdbc.DbInit;
import jdbc.JdbcBookRepository;
import jdbc.JdbcCommentRepository;

public class Beans {
    private static CommentService commentService;

    public static void init() {
        DbInit.init();

        var repo = new JdbcCommentRepository();
        commentService = new CommentService(repo);
    }

    private static final CatalogRepositoryPort bookRepo = new JdbcBookRepository();
    private static final CommentRepositoryPort commentRepo = new JdbcCommentRepository();

    public static CatalogRepositoryPort getBookRepo() {
        return bookRepo;
    }

    public static CommentRepositoryPort getCommentRepo() {
        return commentRepo;
    }
}
