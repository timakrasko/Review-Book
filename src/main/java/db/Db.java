package db;

public final class Db {
    private static final String URL = "jdbc:h2:~/tim4ik";
    static {
        try { Class.forName("org.h2.Driver"); }
        catch (Exception e) { throw new RuntimeException(e); }
    }
    public static java.sql.Connection get() throws java.sql.SQLException {
        return java.sql.DriverManager.getConnection(URL, "sa", "");
    }
}
