/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 * @author C.Vargas
 */
public class Usuario {
    private int id;
    private String name;
    private String email;
    private Timestamp email_verified_at;
    private String password;
    private String photo;
    private String phone;
    private String address;
    private String role;
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getEmail_verified_at() {
        return email_verified_at;
    }

    public void setEmail_verified_at(Timestamp email_verified_at) {
        this.email_verified_at = email_verified_at;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    


    public boolean validarName(String name) {
    return name != null && !name.isEmpty() && name.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$");
    }

    public boolean validarEmail(String email) {
        // Expresión regular simple para validar el email
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    public boolean validarEmailVerifiedAt(String timestampString) {
        // Verificar si la cadena es nula o vacía
        if (timestampString == null || timestampString.isEmpty()) {
            return false;
        }

        // Definir el formato de fecha esperado
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false); // Para asegurarse de que la fecha sea válida

        try {
            // Intentar parsear la fecha
            dateFormat.parse(timestampString);
        } catch (ParseException e) {
            // Si no se puede parsear la fecha, es inválido
            return false;
        }

        // Si la fecha es válida, crear un objeto Timestamp y verificar si es razonable
        Timestamp timestamp = Timestamp.valueOf(timestampString);
        if (timestamp.after(new Timestamp(System.currentTimeMillis()))) {
            // Si el Timestamp es posterior a la fecha actual, es inválido
            return false;
        }

        return true; // Fecha válida
    }

    public boolean validarPassword(String password) {
        // Validar si tiene una longitud mínima
        return password != null && password.length() >= 6;
    }

    public boolean validarPhone(String phone) {
        // Validar si es un número de teléfono con formato básico
        return phone != null && phone.matches("\\d{10}");
    }

    public boolean validarRole(String role) {
        // Validar si el rol está entre los roles permitidos
        return role != null && (role.equals("Admin") || role.equals("User"));
    }

    public boolean validarStatus(String status) {
        // Validar si el estado está entre los estados permitidos
        return status != null && (status.equals("Active") || status.equals("Inactive"));
    }
    
 
}
