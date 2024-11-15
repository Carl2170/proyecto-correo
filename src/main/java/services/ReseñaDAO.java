/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import config.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import models.Reseña;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReseñaDAO {

    // Verifica si un registro existe en la tabla de reseñas por una columna específica
    public boolean existeRegistro(String columna, Object valor) {
        String query = "SELECT COUNT(*) FROM reviews WHERE " + columna + " = ?";
        
        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, valor);
            
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

    // Método para insertar una nueva reseña en la base de datos
    public boolean insertarReseña(String comentario, int calificacion, String estado, int clienteId, int usuarioId) {
        String sql = "INSERT INTO reviews (comment, raiting, status, client_id, user_id) "
                   + "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, comentario);
            stmt.setInt(2, calificacion);
            stmt.setString(3, estado);
            stmt.setInt(4, clienteId);
            stmt.setInt(5, usuarioId);
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0; // Devuelve true si se insertaron filas correctamente
        } catch (SQLException e) {
            System.out.println("Error en la inserción de reseña");
            e.printStackTrace();
            return false; // En caso de error, devuelve false
        }
    }

    // Obtener todas las reseñas
    public List<Reseña> obtenerTodas() {
        List<Reseña> reseñas = new ArrayList<>();
        String query = "SELECT id, comment, raiting, status, client_id, user_id FROM reviews";

        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Reseña reseña = new Reseña();
                reseña.setId(rs.getInt("id"));
                reseña.setComentario(rs.getString("comment"));
                reseña.setCalificacion(rs.getInt("raiting"));
                reseña.setStatus(rs.getString("status"));
                reseña.setClienteId(rs.getInt("client_id"));
                reseña.setUserId(rs.getInt("user_id"));
                
                reseñas.add(reseña);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reseñas;
    }

    // Eliminar reseña con base en criterios
    public boolean eliminarReseña(Map<String, Object> criterios) {
        StringBuilder query = new StringBuilder("DELETE FROM reviews WHERE ");
        List<Object> parametros = new ArrayList<>();

        // Agregar condiciones según los criterios proporcionados
        boolean esPrimerCriterio = true;
        if (criterios.containsKey("id")) {
            if (!esPrimerCriterio) query.append(" AND ");
            query.append("id = ?");
            parametros.add(criterios.get("id"));
            esPrimerCriterio = false;
        }

        if (criterios.containsKey("client_id")) {
            if (!esPrimerCriterio) query.append(" AND ");
            query.append("client_id = ?");
            parametros.add(criterios.get("client_id"));
            esPrimerCriterio = false;
        }

        if (criterios.containsKey("user_id")) {
            if (!esPrimerCriterio) query.append(" AND ");
            query.append("user_id = ?");
            parametros.add(criterios.get("user_id"));
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

    // Actualizar una reseña en la base de datos
    public boolean actualizarReseña(int id, String comentario, int calificacion, String estado) {
        String query = "UPDATE reviews SET comment = ?, raiting = ?, status = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, comentario);
            stmt.setInt(2, calificacion);
            stmt.setString(3, estado);
            stmt.setInt(4, id);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0; // Si se actualizó al menos una fila, se retornará true
        } catch (SQLException e) {
            System.out.println("Error al actualizar la reseña: " + e.getMessage());
        }

        return false; // Si no se actualizó ninguna fila, retornamos false
    }

    // Método auxiliar para obtener el valor actual de una columna
    public Object obtenerValorActual(String columnaClave, Object valorClave) {
        String query = "SELECT " + columnaClave + " FROM reviews WHERE id = ?";

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

