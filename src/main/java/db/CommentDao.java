package db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.*;

public class CommentDao {
    private static final Logger log = LoggerFactory.getLogger(CommentDao.class);

    public void init() throws SQLException {
        try (var c = Db.get(); var st = c.createStatement()) {
            st.execute("""
        create table if not exists comments(
          id identity primary key,
          author varchar(64) not null,
          text   varchar(1000) not null,
          created_at timestamp not null default current_timestamp
        )""");
        }
    }

    public long add(String author, String text) throws SQLException {
        try (var c = Db.get();
             var ps = c.prepareStatement("insert into comments(author,text) values(?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, author);
            ps.setString(2, text);
            ps.executeUpdate();

            long id;
            try (var keys = ps.getGeneratedKeys()) {
                keys.next();
                id = keys.getLong(1);
            }
            log.info("Inserted new comment id={} author={} and text={}",id, author, text);
            return id;
        }
    }

    public List<Map<String,Object>> list() throws SQLException {
        try (var c = Db.get();
             var ps = c.prepareStatement("select id,author,text,created_at from comments order by id desc");
             var rs = ps.executeQuery()) {
            var out = new java.util.ArrayList<java.util.Map<String,Object>>();
            while (rs.next()) {
                out.add(java.util.Map.of(
                        "id", rs.getLong("id"),
                        "author", rs.getString("author"),
                        "text", rs.getString("text"),
                        "createdAt", rs.getTimestamp("created_at").toInstant().toString()
                ));
            }
            return out;
        }
    }
}
