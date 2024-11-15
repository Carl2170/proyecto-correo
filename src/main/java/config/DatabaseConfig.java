/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package config;

import Utils.Constants;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author C.Vargas
 */
public class DatabaseConfig {
  // Configura la URL de la base de datos y las credenciales
    private static final String URL = "jdbc:postgresql://"+Constants.TECNO_SERVER_HOST+":5432/"+Constants.NAME_DB;
    private static final String USER = Constants.MAIL_USER;
    private static final String PASSWORD = Constants.PASSOWRD_DB;

    // Método para obtener una conexión con la base de datos
    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Método para probar la conexión con la base de datos
    public static void probarConexion() {
        try (Connection conn = obtenerConexion()) {
            if (conn != null) {
                System.out.println("Conexión exitosa a la base de datos.");
            }
        } catch (SQLException e) {
            System.out.println("Error al conectar con la base de datos.");
            e.printStackTrace();
        }
    }
    
}
