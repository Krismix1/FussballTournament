package technicalservices;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by antonia on 2017/04/10.
 */
public class DBConnection {
    private final static String URL = "jdbc:mysql://52.36.22.101:3306/";
    private final static String DB_NAME = "tournament";
    private final static String USER = "antoniapinkdino";
    private final static String PASS = "dinopink";

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