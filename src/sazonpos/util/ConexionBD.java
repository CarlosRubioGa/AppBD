package sazonpos.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    // 👇 Ajusta el nombre de BD, usuario y contraseña
    private static final String URL =
            "jdbc:mysql://localhost:3306/pos_sazon_de_ali?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";   // tu usuario MySQL
    private static final String PASS = "";       // tu contraseña MySQL (si tienes)

    // Método "oficial"
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // Alias en español para que no truene el código viejo
    public static Connection getConexion() throws SQLException {
        return getConnection();
    }
}
