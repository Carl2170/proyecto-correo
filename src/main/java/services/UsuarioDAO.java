/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import Utils.Constants;
import config.DatabaseConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.Usuario;

/**
 *
 * @author C.Vargas
 */
public class UsuarioDAO {
    
    public boolean existeRegistro(String columna, Object valor) {
        String query = "SELECT COUNT(*) FROM users WHERE " + columna + " = ?";
        
        try (Connection conn = DatabaseConfig.obtenerConexion();
         PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, valor); // Establece el valor de manera dinámica
            
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0; // Retorna true si existe al menos un registro
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar el registro: " + e.getMessage());
        }
        
        return false; // Retorna false si no encuentra el registro o si ocurre algún error
    }
    
    // Método para insertar un nuevo usuario en la base de datos
    public boolean insertarUsuario(String name, String email, Timestamp emailVerifiedAt, String password, String photo, String phone, String address, String role, String status) {
        String sql = "INSERT INTO users (name, email, email_verified_at, password, photo, phone, address, role, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        DatabaseConfig.probarConexion();

        try (Connection conn = DatabaseConfig.obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setTimestamp(3, emailVerifiedAt); // El formato debe ser Timestamp en la BD
            stmt.setString(4, password);
            stmt.setString(5, photo);
            stmt.setString(6, phone);
            stmt.setString(7, address);
            stmt.setString(8, role);
            stmt.setString(9, status);
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0; // Devuelve true si se insertaron filas correctamente
        } catch (SQLException e) {
            System.out.println("error en la funcion de insercion");
            e.printStackTrace();
            return false; // En caso de error, devuelve false
        }
    }

    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String query = "SELECT id, name, email, email_verified_at, photo, phone, address, role, status FROM users";

        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setName(rs.getString("name"));
                usuario.setEmail(rs.getString("email"));
                usuario.setEmail_verified_at(rs.getTimestamp("email_verified_at"));
                usuario.setPhoto(rs.getString("photo"));
                usuario.setPhone(rs.getString("phone"));
                usuario.setAddress(rs.getString("address"));
                usuario.setRole(rs.getString("role"));
                usuario.setStatus(rs.getString("status"));
          
                usuarios.add(usuario);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuarios;
    }
       
    public boolean eliminarUsuario(Map<String, Object> criterios) {
    StringBuilder query = new StringBuilder("DELETE FROM users WHERE ");
        List<Object> parametros = new ArrayList<>();

        // Agregar condiciones según los criterios proporcionados
        boolean esPrimerCriterio = true;
        if (criterios.containsKey("id")) {
            if (!esPrimerCriterio) query.append(" AND ");

            System.out.println("entro en eliminar usuario en ID");
            query.append("id = ?");
            parametros.add(Integer.parseInt((String) criterios.get("id")));
            esPrimerCriterio = false;
        }

        if (criterios.containsKey("name")) {
            if (!esPrimerCriterio) query.append(" AND ");
            query.append("name = ?");
            parametros.add(criterios.get("name"));
            esPrimerCriterio = false;
        }

        if (criterios.containsKey("role")) {
            if (!esPrimerCriterio) query.append(" AND ");
            query.append("role = ?");
            parametros.add(criterios.get("role"));
            esPrimerCriterio = false;
        }

        if (criterios.containsKey("status")) {
            if (!esPrimerCriterio) query.append(" AND ");
            query.append("status = ?");
            parametros.add(criterios.get("status"));
        }

        // Si no hay criterios para eliminar, devolver false
        if (parametros.isEmpty()) {
            System.out.println("Error: No se proporcionaron criterios válidos para eliminar.");
            return false;
        }

        // Ejecutar la consulta SQL
        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            // Asignar los parámetros de la consulta
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }

            // Ejecutar la eliminación
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Si se afectaron filas, la eliminación fue exitosa

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Si ocurre un error en la base de datos, retornar false
        }
    }   
    
    //Actualizar o modificar
    public boolean actualizarRegistro(String columna, Object valor, String columnaClave, Object valorClave) {
        String query = "UPDATE users SET " + columna + " = ? WHERE " + columnaClave + " = ?";

        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, valor);
            stmt.setObject(2, valorClave);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0; // Si se actualizó al menos una fila, se retornará true
        } catch (SQLException e) {
            System.out.println("Error al actualizar el registro: " + e.getMessage());
        }

        return false; // Si no se actualizó ninguna fila, retornamos false
    }

    // Método auxiliar para obtener el valor actual de una columna
    public Object obtenerValorActual(String columnaClave, Object valorClave) {
        String query = "SELECT " + columnaClave + " FROM users WHERE id = ?";

        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, valorClave);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getObject(columnaClave);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el valor actual: " + e.getMessage());
        }
        return null; // Si no se encuentra el valor actual
    }
    

}