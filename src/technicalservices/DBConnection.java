package technicalservices;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by antonia on 2017/04/10.
 */
public class DBConnection {
    private final static String URL = "jdbc:mysql://localhost:3306/";
    private final static String DB_NAME = "fussball_tournament";
    private final static String USER = "root";
    private final static String PASS = "";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    URL + DB_NAME,
                    USER,
                    PASS);
            return con;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}