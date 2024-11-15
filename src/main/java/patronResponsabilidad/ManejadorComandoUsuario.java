/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package patronResponsabilidad;

import Utils.Constants;
import Utils.Validador;
import config.SMTP;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import models.Usuario;
import services.UsuarioDAO;

/**
 *
 * @author C.Vargas
 */
public class ManejadorComandoUsuario extends ManejadorComandoAbs{
    
    private final String comandos;
    private UsuarioDAO usuario;
    private int cantidadParametros = 9;
    private List<String> errores ;
    private String[] filtros;
    
    
    public ManejadorComandoUsuario(){
        this.comandos = "^(LISTAR|CREAR|EDITAR|ELIMINAR)(USUARIO|USUARIOS)\\[.*\\]$";
        this.filtros = new String[] {"*","id", "name", "email", "email_verified_at", "status"};

        this.usuario = new UsuarioDAO();
        this.errores = new ArrayList<>();
    }
    
    @Override
    //funcion que verifica si a esta clase pertenece el comando entrante
    public boolean VerificarProceso(String comand) {
        return comand.matches(comandos);

    }
    
    // función que procesa el comando para usuarios
    @Override
    public Map<String, Object> procesar(String command) {
        if(!VerificarProceso(command)){
            System.out.println("entro aqui");
            if (siguienteManejadorComando != null) {
             return siguienteManejadorComando.procesar(command);
            }
            System.out.println("siguiente manejador nulo");
        }
        Map<String, Object> resultado= new HashMap<>(); ;
        
        // Implementa lógica para listar, crear, modificar o eliminar usuario según el comando
        if (command.startsWith("LISTARUSUARIOS")) {
            System.out.println("Entro en LISTARUSUARIOS");
            resultado = listarUsuarios(command);
            
        } else if (command.startsWith("CREARUSUARIO")) {
            System.out.println("Entro en CREARUSUARIO");
            resultado = crearUsuario(command);
            
        } else if (command.startsWith("EDITARUSUARIO")) {
            System.out.println("Entro en EDITARUSUARIO");
            resultado = ModificarUsuario(command) ;
            
        } else if (command.startsWith("ELIMINARUSUARIO")) {
            System.out.println("Entro en ELIMINARUSUARIO");
            return  EliminarUsuario(command);  
        } 
        System.out.println("no entro a ninugno");
        return resultado;
    }
       
    //funcion con validaciones para crear el usuario en la BD 
    public Map<String, Object>  crearUsuario(String comando){
        boolean resultado;
        Map<String, Object> response = new HashMap<>();  
        String[] parametros = extraerParametros(comando);
        Map<String, Object> validacionParametros = verificarCantidadParametros(parametros,cantidadParametros);

        // Si hay un error en la validación de cantidad de parámetros, devuelve el error y termina la función
        if (!"Correcto".equals(validacionParametros.get("body"))) {
            return validacionParametros; // Retorna el mensaje de error si falta o sobra algún parámetro
        }   
        
        // Valida la  existencia del email en la BD antes de intentar la inserción
        if (usuario.existeRegistro("email",parametros[1])) { 
            response.put("subject", "Parámetro ya existente");
            response.put("body", "Ya existe un usuario registrado con el correo:  " + parametros[1]);
            return response;
        }
        
        // Valida la  existencia del telefono en la BD antes de intentar la inserción
        if (usuario.existeRegistro("phone",parametros[4])){
            response.put("subject", "Parámetro ya existente");
            response.put("body", "Ya existe un usuario registrado con el teléfono:  " + parametros[4]);
            return response;
        }

        //Valida los formatos de cada parametros mandado en el comando
        if(validarParametros(parametros)){
         resultado = usuario.insertarUsuario(parametros[0], 
                                                    parametros[1], 
                                                    convertirAFecha(parametros[2]), 
                                                    parametros[3], 
                                                    parametros[4], 
                                                    parametros[5], 
                                                    parametros[6], 
                                                    parametros[7], 
                                                    parametros[8]);
                                                  
            if(resultado){
                //Exito al crear el usuario
                System.out.println("Se creó un nuevo usuario ");
                //Se asigna al Map para guardar el contenido de respuesta que se va enviar al correo 
                response.put("subject", "Usuario creado con éxito");
                response.put("body", "Se creó un nuevo usuario con los siguientes detalles:\n" + Arrays.toString(parametros));
            }else{
                //Problemas en la base de datos
                response.put("subject", "Error al crear el usuario");
                response.put("body", "No se pudo insertar el usuario. Error en la inserción.\n");    
            }
        
        //si  existe errores en los parámetros
        }else {
            String listadoParametros = generarEstructuraCorrecta();
            String detalleParametros = obtenerDetallesParametros();
            response.put("subject", "Error en la validación de parámetros");
            response.put("body", "Algunos parámetros no son válidos.\n"
                        + "Parámetros esperados: "+ listadoParametros+ "\n"
                        + "Parametros enviados : " + Arrays.toString(parametros)+ "\n"
                        + "Errores encontrados: \n" 
                        + this.errores
                        + "Revisa los datalles de cada parámetro para la creación de usuario: " + "\n"
                        + detalleParametros
                           );
        }
        return response;
    }
    
    //funcion para listar los usuarios con todos sus parametros
    public Map<String, Object> listarUsuarios(String comando) {
        Map<String, Object> response = new HashMap<>();
        String[] parametro = extraerParametros(comando);
        List<Usuario> listaUsuarios = new ArrayList<>();
        try {
            
            //verificacion de parámetros 
            if ( (parametro.length < 1) || (parametro.length > 1) ){
                response.put("subject", "Error en el parámetro");
                response.put("body", "Se esperaba un parámetro, pero se enviaron: "+ parametro.length + " parametros");
                return response;
            }
            
            //verifica que sea * el unico parametro
            if (parametro.length == 1 && "*".equals(parametro[0])){
                listaUsuarios = usuario.obtenerTodos();
                
                // Verifica si hay usuarios en la base de datos
                if (listaUsuarios.isEmpty()) {
                    response.put("subject", "Sin usuarios");
                    response.put("body", "No hay usuarios registrados en el sistema.");
                    return response;            
                }             
                
            // Crear una lista de mapas para formatear cada usuario como un objeto clave-valor
            List<Map<String, String>> usuariosFormateados = new ArrayList<>();
            List<String> columnas = Arrays.asList("Id", "Nombre", "Email", "Verificacion email", "Foto", "Teléfono", "Dirección", "Rol", "Estado");

            for (Usuario u : listaUsuarios) {
                Map<String, String> usuarioData = new HashMap<>();
              //  usuarioData.put("Id", String.valueOf((char) u.getId()));
                usuarioData.put("Id", String.valueOf(u.getId()));
                usuarioData.put("Nombre", u.getName());
                usuarioData.put("Email", u.getEmail());
                usuarioData.put("Verificacion email", u.getEmail_verified_at().toString());
                usuarioData.put("Foto", u.getPhoto());
                usuarioData.put("Teléfono", u.getPhone());
                usuarioData.put("Dirección", u.getAddress());
                usuarioData.put("Rol", u.getRole());
                usuarioData.put("Estado", u.getStatus());

                // Agrega los datos formateados del usuario a la lista
                usuariosFormateados.add(usuarioData);
            }

                response.put("subject", "Listado de usuarios");
                response.put("body", usuariosFormateados);
                response.put("columnas", columnas);
                response.put("esListado", true);

            }else{
                response.put("subject", "Parámetros incorrectos");
                response.put("body", "El comando de listado de usuarios no es válido.");
            }
       
        } catch (Exception e) {
            response.put("subject", "Error al listar usuarios");
            response.put("body", "Ocurrió un error al intentar obtener los usuarios: " + e.getMessage());
        }

        return response;
    }
    
    public Map<String, Object>  EliminarUsuario(String comando) {
       errores.clear();
       String parametros = extraerContenidoCorchetes(comando);
       Map<String, Object> response = new HashMap<>();
        System.out.println("parametros para parsear: "+ parametros);
       Map<String, Object> resultado = parsearCriterios(parametros);
       boolean exito = false;
       
       Map<String, Object> criterios = (Map<String, Object>) resultado.get("criterios");
        errores = (List<String>) resultado.get("errores");
       
       if (!errores.isEmpty()) {
            System.out.println("Errores en el parseo:");
            System.out.println(errores);
            response.put("subject", "Error en el formato de parámetros");
            response.put("body", "Los parámetros no son válidos: "+
                                  errores);
            return response;
        }
        if (verificarParametrosEliminacion(criterios)){
            exito = usuario.eliminarUsuario(criterios);
            if(exito) {
                 response.put("subject", "Elimnación de usuario exitosa");
                 response.put("body", "Se ha eliminado al usuario com parametro: "+ resultado);
                 return response;
            }
                 response.put("subject", "Error en Elimnar usuario");
                 response.put("body", "Ha ocurrido un error al eliminar al usuario com parametro: "+ resultado);
                 return response;
        }
        
            response.put("subject", "Error en parámetros");
            response.put("body", "Se encontraron los siguientes errores : "+ this.errores);
            return response;
    }
    
    public Map<String, Object> ModificarUsuario(String comando) {
         errores.clear(); // Limpiar errores previos
         
        System.out.println("entro a modificar");
        String parametros = extraerContenidoCorchetes(comando); // Extraer parámetros del comando
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> resultado = parsearCriterios(parametros); // Parsear criterios del comando
         boolean exito = false;

        // Obtener los criterios y errores del resultado del parseo
        Map<String, Object> criterios = (Map<String, Object>) resultado.get("criterios");
        errores = (List<String>) resultado.get("errores");

        // Verificar si hay errores en el parseo de parámetros
        if (!errores.isEmpty()) {
            System.out.println("volvio a modificar, hay errores");

            System.out.println("Errores en el parseo:");
            System.out.println(errores);
            response.put("subject", "Error en el formato de parámetros");
            response.put("body", "Los parámetros no son válidos: " + errores);
            return response;
        }

        // Verificar que los parámetros sean válidos antes de proceder
        if (verificarParametrosModificacion(criterios)) {
            System.out.println(criterios);
            // Si todo es válido, actualizar el registro
                int id = Integer.parseInt((String) criterios.get("id"));

            // Obtener los valores de cada campo para actualizar
            for (Map.Entry<String, Object> entry : criterios.entrySet()) {
                String columna = entry.getKey();
                Object valor = entry.getValue();

                // Evitar modificar el 'id', ya que no es modificable
                if (!columna.equals("id") && !usuario.existeRegistro(columna, valor)) {
                    exito = usuario.actualizarRegistro(columna, valor, "id", id);
                }else{
                  response.put("subject", "Problema en la edición");
                 response.put("body", "El usuario con ID " + id + " ya tiene el valor: "+ valor +" en la columna: "+columna);   
                return response;
                }
            }

            // Responder si la actualización fue exitosa o no
            if (exito) {
                response.put("subject", "Modificación de usuario exitosa");
                response.put("body", "El usuario con ID " + id + " ha sido modificado exitosamente.");
            } else {
                response.put("subject", "Error al modificar usuario");
                response.put("body", "No se pudo modificar el usuario con ID " + id + ".");
            }
        } else {
            response.put("subject", "Error en la validación de parámetros");
            response.put("body", "Los parámetros no son válidos."+ errores);
        }

        return response;
    }

   public boolean verificarParametrosModificacion(Map<String, Object> criterios) {
    errores.clear();

    Set<String> parametrosValidos = Set.of("id","name", "email", "password", "photo", "phone", "address", "role","status");

    for (Map.Entry<String, Object> criterio : criterios.entrySet()) {
        String parametro = criterio.getKey();
        String valor = (String) criterio.getValue();

        // Verificar si el valor está vacío
        if (valor.isEmpty()) {
            errores.add("El valor para " + parametro + " no puede estar vacío.");
            return false;
        }

        // Verificar si el parámetro está en la lista de parámetros válidos
        if (!parametrosValidos.contains(parametro)) {
            System.out.println("Parámetro inválido: " + parametro);
            errores.add("Parámetro inválido, o no pertenece a Usuario: " + parametro);
            return false;
        }

        // Validación para "id"
        if (parametro.equals("id")) {
            if (!validarNumeroEntero(valor)) {
                errores.add("Parámetro ID no es un número entero: " + valor);
                return false;
            }

            if (!usuario.existeRegistro("id", Integer.parseInt(valor))) {
                errores.add("No existe usuario con el id: " + valor);
                return false;
            }
        }
        // Validación para "status"
        if (parametro.equals("status") && !valor.matches("^(activo|inactivo)$")) {
            System.out.println("El valor de 'status' debe ser 'activo' o 'inactivo': " + valor);
            errores.add("Parámetro inválido: " + parametro + ". El valor de 'status' debe ser 'activo' o 'inactivo'.");
            return false;
        }

        // Verificar que el valor sea del tipo adecuado
        if (!esTipoValido(parametro, valor)) {
            System.out.println("parametro: "+ parametro+ "valor: "+ valor);
            errores.add("El valor para " + parametro + " no es del tipo adecuado.");
            return false;
        }

        // Verificación adicional de si el valor ha cambiado (comparando con el valor actual en la base de datos)
//        Object valorActual =usuario.obtenerValorActual(parametro, valor);
//        if (valorActual == null || !valorActual.equals(valor)) {
//            errores.add("El valor para " + parametro + " no es válido o no ha cambiado.");
//            return false;
//        }
    }

    return true; // Si todas las validaciones pasaron
}

    public boolean verificarParametrosEliminacion(Map<String, Object> criterios) {
        errores.clear();
        Set<String> parametrosValidos = Set.of("id", "name", "role", "status");
   
        for (Map.Entry<String, Object> criterio : criterios.entrySet()) {
            String parametro = criterio.getKey();
            String valor = (String) criterio.getValue();
            
            //si el valor es vacio
            if (valor.isEmpty()){
                errores.add("El valor para " + parametro + " no puede estar vacío.");
                return false;
            }

            // Verificar si el parámetro está en la lista de parámetros válidos
            if (!parametrosValidos.contains(parametro)) {
                System.out.println("Parámetro inválido: " + parametro);
                errores.add("Parámetro inválido: " + parametro);
                return false;
            }

            if (parametro.equals("id")){
                if(!validarNumeroEntero(valor)){
                errores.add("Parámetro ID no es un número entero: " + valor);
                    return false;
                }
                if(!usuario.existeRegistro("id",valor)){
                    errores.add("No existe usuario con el id :" + valor);
                    return false;
                    
                }
            }
            
            if(parametro.equals("name")){
                errores.add("No existe usuario con el nombre :" + valor);
                return usuario.existeRegistro("name", valor);
            }
            
              // Ejemplo de validación adicional para "status" (si fuera necesario)
            if (parametro.equals("status") && !valor.matches("^(activo|inactivo)$")) {
                System.out.println("El valor de 'status' debe ser 'activo' o 'inactivo': " + valor);
                errores.add("Parámetro inválido: " + parametro + "El valor de 'status' debe ser 'activo' o 'inactivo'");
                return false;
            }
        }
    return true; // Todos los parámetros son válidos
    }
     
    //Valida cada parametro y existe error lo agrega al arrayList de errores
    public boolean validarParametros(String[] parametros){ 
        errores.clear(); 
        if (!validarTextoConEspacios(parametros[0])) {
            errores.add("El nombre no es válido. Debe contener solo letras y espacios.");
        }
        if (!validarEmail(parametros[1])) {
            errores.add("El correo electrónico no es válido. Debe estar en formato ejemplo@dominio.com.");
        }
        if (!validarFecha(parametros[2])) {
            errores.add("La fecha de verificación no es válida. Debe estar en formato yyyy-MM-dd HH:mm:ss.");
        }
        if (!verificarPassword(parametros[3])) {
            errores.add("La contraseña no es válida. Debe tener al menos 8 caracteres, incluir una letra mayúscula, un número y un carácter especial.");
        }
        if (!validarUrlImagen(parametros[4])) {
            errores.add("La URL de la foto no es válida. Debe ser una URL válida.");
        }
        if (!validarTelefono(parametros[5])) {
            errores.add("El teléfono no es válido. Debe contener solo números.");
        }
        if (!validarDireccion(parametros[6])) {
            errores.add("La dirección no es válida.");
        }
        if (!validarTextoConEspacios(parametros[7]) || !"USUARIO".equals(parametros[7])) {
            errores.add("El rol no es válido. Debe ser un texto que represente el rol ('USUARIO').");
        }
        if (!validarTextoConEspacios(parametros[8])) {
            errores.add("El estado no es válido. Debe ser un texto que represente el estado (por ejemplo, 'activo' o 'inactivo').");
        }

        // Si hay errores, construir el mensaje de respuesta y retornar false
        if (!errores.isEmpty()) {
            String mensajeError = "Errores encontrados:\n" + String.join("\n", errores);
            System.out.println(mensajeError);  // También puedes enviar esto como parte de la respuesta
            return false;
        }

        // Si no hay errores, retornar true
        System.out.println("Todos los parámetros son válidos.");
        return true;
}
    
    //Genera la estructura correcta como ejemplo
    public String generarEstructuraCorrecta() {
        return "[nombre, email, fecha_verificacion, password, foto, telefono, direccion, estado]";
    }

    // Método auxiliar para obtener la descripción de cada parámetro
    public String obtenerDetallesParametros() {
        return "nombre: Nombre completo del usuario. No se aceptan números\n" +
               "email: Correo electrónico en formato adecuado (ejemplo: ejemplo@dominio.com)\n" +
               "fecha_verificacion: Fecha de verificación del email (formato yyyy-MM-dd HH:mm:ss)\n" +
               "password: Contraseña del usuario (Debes ser de 8 caracteres minimo, debe incluir mínimo una letra mayúscula,debe incluir mínimo un número, debe incluir mínimo caracter especial)\n" +
               "foto: URL de la foto del usuario \n" +
               "telefono: Número de teléfono del usuario (Solo numeros)\n" +
               "direccion: Dirección del usuario\n" +
                "rol: el rol  debe ser: USUARIO\n"+
               "estado: Estado del usuario (String, por ejemplo, 'ACTIVO' o 'INACTIVO')";
}
    
    public boolean esTipoValido(String columna, Object valor) {
      boolean res = false;

       if (columna.equals("id")){
           res= validarNumeroEntero(valor.toString());
       }
       
       if (columna.equals("name")){
            res= validarTextoConEspacios(valor.toString());
       }
       if (columna.equals("email_verified_at")){
            res= validarFecha(valor.toString());
       }
        if (columna.equals("password")){
            res= validarTextoConEspacios(valor.toString());
       }
        if (columna.equals("photo")){
            res= validarUrlImagen(valor.toString());
       }
        if (columna.equals("phone")){
            res= validarTelefono(valor.toString());
       } 
        if (columna.equals("address")){
            res= validarDireccion(valor.toString());
       } 
        if (columna.equals("role")){
            res= validarTextoConEspacios(valor.toString());
       }
        if (columna.equals("status")){
            res= validarTextoConEspacios(valor.toString());
       }
       return res;
    }

}
