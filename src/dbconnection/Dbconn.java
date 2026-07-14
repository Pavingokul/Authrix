package dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;

public class Dbconn {

    static Connection con;

    public static Connection getconnection() {
        try {
            // MySQL 5 driver
            Class.forName("com.mysql.jdbc.Driver");

            // allowLoadLocalInfile=true added
            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/authrix?allowLoadLocalInfile=true",
                "root",
                "root"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
}
