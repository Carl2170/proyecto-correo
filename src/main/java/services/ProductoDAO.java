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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.Producto;

public class ProductoDAO {

    // Método para verificar si existe un producto con un valor específico en alguna columna
    public boolean existeRegistro(String columna, Object valor) {
        String query = "SELECT COUNT(*) FROM products WHERE " + columna + " = ?";
        
        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, valor); // Establece el valor dinámicamente
            
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0; // Devuelve true si existe al menos un registro
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar el registro: " + e.getMessage());
        }
        
        return false; // Retorna false si no encuentra el registro o si ocurre algún error
    }

    // Método para insertar un nuevo producto en la base de datos
    public boolean insertarProducto(String name, String slug, String code, int qty, String size, double price, 
                                    Double discountPrice, String image, boolean mostPopular, boolean bestSeller, String status, 
                                    Integer cityId, Integer categoryId, Integer menuId, Integer clientId) {
        String sql = "INSERT INTO products (name, slug, code, qty, size, price, discount_price, image, most_populer, best_seller, status, "
                   + "city_id, category_id, menu_id, client_id) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.obtenerConexion(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, slug);
            stmt.setString(3, code);
            stmt.setInt(4, qty);
            stmt.setString(5, size);
            stmt.setDouble(6, price);
            stmt.setObject(7, discountPrice); // Puede ser null
            stmt.setString(8, image);
            stmt.setBoolean(9, mostPopular);
            stmt.setBoolean(10, bestSeller);
            stmt.setString(11, status);
            stmt.setObject(12, cityId); // Puede ser null
            stmt.setObject(13, categoryId); // Puede ser null
            stmt.setObject(14, menuId); // Puede ser null
            stmt.setObject(15, clientId); // Puede ser null
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0; // Retorna true si se insertaron filas correctamente
        } catch (SQLException e) {
            System.out.println("Error al insertar el producto: " + e.getMessage());
            return false; // En caso de error, retorna false
        }
    }

    // Método para obtener todos los productos
    public List<Producto> obtenerTodos() {
        List<Producto> productos = new ArrayList<>();
        String query = "SELECT id, name, slug, code, qty, size, price, discount_price, image, most_populer, best_seller, status, "
                     + "city_id, category_id, menu_id, client_id FROM products";

        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setId(rs.getInt("id"));
                producto.setName(rs.getString("name"));
                producto.setSlug(rs.getString("slug"));
                producto.setCode(rs.getString("code"));
                producto.setQty(rs.getInt("qty"));
                producto.setSize(rs.getString("size"));
                producto.setPrice(rs.getDouble("price"));
                producto.setDiscountPrice(rs.getDouble("discount_price"));
                producto.setImage(rs.getString("image"));
                producto.setMostPopuler(rs.getBoolean("most_populer"));
                producto.setBestSeller(rs.getBoolean("best_seller"));
                producto.setStatus(rs.getString("status"));
                producto.setCityId(rs.getInt("city_id"));
                producto.setCategoryId(rs.getInt("category_id"));
                producto.setMenuId(rs.getInt("menu_id"));
                producto.setClientId(rs.getInt("client_id"));

                productos.add(producto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }

    // Método para eliminar un producto basado en criterios dinámicos
    public boolean eliminarProducto(Map<String, Object> criterios) {
        StringBuilder query = new StringBuilder("DELETE FROM products WHERE ");
        List<Object> parametros = new ArrayList<>();
        
        // Agregar condiciones según los criterios proporcionados
        boolean esPrimerCriterio = true;
        if (criterios.containsKey("id")) {
            if (!esPrimerCriterio) query.append(" AND ");
            query.append("id = ?");
            parametros.add(criterios.get("id"));
            esPrimerCriterio = false;
        }

        if (criterios.containsKey("name")) {
            if (!esPrimerCriterio) query.append(" AND ");
            query.append("name = ?");
            parametros.add(criterios.get("name"));
            esPrimerCriterio = false;
        }

        if (criterios.containsKey("status")) {
            if (!esPrimerCriterio) query.append(" AND ");
            query.append("status = ?");
            parametros.add(criterios.get("status"));
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
            return false; // Si ocurre un error, retornamos false
        }
    }

    // Método para actualizar un producto
    public boolean actualizarProducto(String columna, Object valor, String columnaClave, Object valorClave) {
        String query = "UPDATE products SET " + columna + " = ? WHERE " + columnaClave + " = ?";

        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, valor);
            stmt.setObject(2, valorClave);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0; // Si se actualizó al menos una fila, retorna true
        } catch (SQLException e) {
            System.out.println("Error al actualizar el producto: " + e.getMessage());
        }

        return false; // Si no se actualizó ninguna fila, retorna false
    }

    // Método auxiliar para obtener el valor actual de una columna de un producto
    public Object obtenerValorActual(String columnaClave, Object valorClave) {
        String query = "SELECT " + columnaClave + " FROM products WHERE id = ?";

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

