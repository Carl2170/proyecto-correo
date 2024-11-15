/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

/**
 *
 * @author C.Vargas
 */
import config.DatabaseConfig;
import java.sql.*;
import java.util.*;
import models.Cupon;

public class CuponDAO {

    // Verificar si existe un cupón con un valor específico en una columna
    public boolean existeRegistro(String columna, Object valor) {
        String query = "SELECT COUNT(*) FROM coupons WHERE " + columna + " = ?";

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

    // Método para insertar un nuevo cupón en la base de datos
    public boolean insertarCupon(String couponName, String couponDesc, int discount, String validity, int status, int clientId) {
        String sql = "INSERT INTO coupons (coupon_name, coupon_desc, discount, validity, status, client_id) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.obtenerConexion(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, couponName);
            stmt.setString(2, couponDesc);
            stmt.setInt(3, discount);
            stmt.setString(4, validity);
            stmt.setInt(5, status);
            stmt.setInt(6, clientId);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0; // Devuelve true si se insertaron filas correctamente
        } catch (SQLException e) {
            System.out.println("Error en la función de inserción");
            e.printStackTrace();
            return false; // En caso de error, devuelve false
        }
    }

    // Método para obtener todos los cupones
    public List<Cupon> obtenerTodos() {
        List<Cupon> cupones = new ArrayList<>();
        String query = "SELECT id, coupon_name, coupon_desc, discount, validity, status, client_id FROM coupons";

        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cupon cupon = new Cupon();
                cupon.setId(rs.getInt("id"));
                cupon.setCoupon_name(rs.getString("coupon_name"));
                cupon.setCoupon_desc(rs.getString("coupon_desc"));
                cupon.setDiscount(rs.getInt("discount"));
                cupon.setValidity(rs.getString("validity"));
                cupon.setStatus(rs.getInt("status"));
                cupon.setClient_id(rs.getInt("client_id"));

                cupones.add(cupon);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cupones;
    }

    // Método para eliminar cupones según criterios proporcionados
    public boolean eliminarCupon(Map<String, Object> criterios) {
        StringBuilder query = new StringBuilder("DELETE FROM coupons WHERE ");
        List<Object> parametros = new ArrayList<>();

        // Agregar condiciones según los criterios proporcionados
        boolean esPrimerCriterio = true;
        if (criterios.containsKey("id")) {
            if (!esPrimerCriterio) query.append(" AND ");
            query.append("id = ?");
            parametros.add(Integer.parseInt((String) criterios.get("id")));
            esPrimerCriterio = false;
        }

        if (criterios.containsKey("coupon_name")) {
            if (!esPrimerCriterio) query.append(" AND ");
            query.append("coupon_name = ?");
            parametros.add(criterios.get("coupon_name"));
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

    // Método para actualizar los detalles de un cupón
    public boolean actualizarRegistro(String columna, Object valor, String columnaClave, Object valorClave) {
        String query = "UPDATE coupons SET " + columna + " = ? WHERE " + columnaClave + " = ?";

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
        String query = "SELECT " + columnaClave + " FROM coupons WHERE id = ?";

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
