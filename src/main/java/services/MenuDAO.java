/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import config.DatabaseConfig;
import java.sql.*;
import java.util.*;
import models.Menu;

public class MenuDAO {
    
    // Verifica si existe un registro en la tabla "menu" según un valor en una columna
    public boolean existeRegistro(String columna, Object valor) {
        String query = "SELECT COUNT(*) FROM menu WHERE " + columna + " = ?";
        
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
    
    // Método para insertar un nuevo menú en la base de datos
    public boolean insertarMenu(String menuName, String image) {
        String sql = "INSERT INTO menu (menu_name, image) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, menuName);
            stmt.setString(2, image);
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0; // Devuelve true si se insertaron filas correctamente
        } catch (SQLException e) {
            System.out.println("Error al insertar el menú: " + e.getMessage());
            return false; // En caso de error, devuelve false
        }
    }

    // Método para obtener todos los menús
    public List<Menu> obtenerTodos() {
        List<Menu> menus = new ArrayList<>();
        String query = "SELECT id, menu_name, image FROM menu";

        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Menu menu = new Menu();
                menu.setId(rs.getInt("id"));
                menu.setMenuName(rs.getString("menu_name"));
                menu.setImage(rs.getString("image"));
                
                menus.add(menu);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return menus;
    }

    // Método para eliminar un menú
    public boolean eliminarMenu(Map<String, Object> criterios) {
        StringBuilder query = new StringBuilder("DELETE FROM menu WHERE ");
        List<Object> parametros = new ArrayList<>();

        // Agregar condiciones según los criterios proporcionados
        boolean esPrimerCriterio = true;
        if (criterios.containsKey("id")) {
            if (!esPrimerCriterio) query.append(" AND ");

            query.append("id = ?");
            parametros.add(Integer.parseInt((String) criterios.get("id")));
            esPrimerCriterio = false;
        }

        if (criterios.containsKey("menu_name")) {
            if (!esPrimerCriterio) query.append(" AND ");
            query.append("menu_name = ?");
            parametros.add(criterios.get("menu_name"));
            esPrimerCriterio = false;
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

    // Método para actualizar un registro de menú
    public boolean actualizarRegistro(String columna, Object valor, String columnaClave, Object valorClave) {
        String query = "UPDATE menu SET " + columna + " = ? WHERE " + columnaClave + " = ?";

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
        String query = "SELECT " + columnaClave + " FROM menu WHERE id = ?";

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
