package web;

import db.CommentDao;

public class AppInit implements jakarta.servlet.ServletContextListener {
    @Override public void contextInitialized(jakarta.servlet.ServletContextEvent e) {
        try { new CommentDao().init(); }
        catch (Exception ex) { throw new RuntimeException(ex); }
    }
}
