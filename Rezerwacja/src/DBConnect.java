import java.sql.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

public class DBConnect {
    Connection con;
    public Connection conect() {
        String host = "jdbc:mysql://localhost:3306/bazydanych";
        String uName = "root";
        String uPass = "Qwe123$";
        try {
            con = DriverManager.getConnection(host, uName, uPass);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return con;
    }
}
