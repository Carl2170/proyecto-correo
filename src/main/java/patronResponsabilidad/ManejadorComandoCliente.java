/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package patronResponsabilidad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import models.Cliente;
import static patronResponsabilidad.ManejadorComandoAbs.extraerParametros;
import services.ClienteDAO;

/**
 *
 * @author C.Vargas
 */
public class ManejadorComandoCliente extends ManejadorComandoAbs{
    
    private final String comandos;
    private ClienteDAO cliente;
    private int cantidadParametros = 9;
    private List<String> errores ;
    private String[] filtros;
    
    
    public ManejadorComandoCliente(){
        
        this.comandos = "^(LISTAR|CREAR|EDITAR|ELIMINAR)(CLIENTE|CLIENTES)\\[.*\\]$";
        this.filtros = new String[] {"*","id", "name", "email", "email_verified_at", "role", "status"};

        this.cliente = new ClienteDAO();
        this.errores = new ArrayList<>();
    }
    @Override
    public boolean VerificarProceso(String comando) {
        return comando.matches(comandos);
    }
    
    @Override
    public Map<String, Object> procesar(String command) {
       if(!VerificarProceso(command)){
             return siguienteManejadorComando.procesar(command);
        }
        Map<String, Object> resultado= new HashMap<>(); ;
        
        // Implementa lógica para listar, crear, modificar o eliminar clientesegún el comando
        if (command.startsWith("LISTARCLIENTES")) {
            System.out.println("Entro en LISTARCLIENTES");
            resultado = ListarClientes(command);
            
        } else if (command.startsWith("CREARCLIENTE")) {
            System.out.println("Entro en CREARCLIENTE");
            resultado = crearCliente(command);
            
        } else if (command.startsWith("EDITARCLIENTE")) {
            System.out.println("Entro en EDITARCLIENTE");
            resultado = EditarCliente(command) ;
            
        } else if (command.startsWith("ELIMINARCLIENTE")) {
            System.out.println("Entro en ELIMINARCLIENTE");
            return eliminarCliente(command);
        }
        return resultado;    
    }
    
    public Map<String, Object> crearCliente(String comando){
     boolean resultado;
        Map<String, Object> response = new HashMap<>();  
        String[] parametros = extraerParametros(comando);
        Map<String, Object> validacionParametros = verificarCantidadParametros(parametros,cantidadParametros);

        // Si hay un error en la validación de cantidad de parámetros, devuelve el error y termina la función
        if (!"Correcto".equals(validacionParametros.get("body"))) {
            return validacionParametros; // Retorna el mensaje de error si falta o sobra algún parámetro
        }   
        
        // Valida la  existencia del email en la BD antes de intentar la inserción
        if (cliente.existeRegistro("email",parametros[1])) { 
            response.put("subject", "Parámetro ya existente");
            response.put("body", "Ya existe un clienteregistrado con el correo:  " + parametros[1]);
            return response;
        }
        
        // Valida la  existencia del telefono en la BD antes de intentar la inserción
        if (cliente.existeRegistro("phone",parametros[4])){
            response.put("subject", "Parámetro ya existente");
            response.put("body", "Ya existe un clienteregistrado con el teléfono:  " + parametros[4]);
            return response;
        }

        //Valida los formatos de cada parametros mandado en el comando
        if(validarParametros(parametros)){
         resultado = cliente.insertarcliente(parametros[0], 
                                                    parametros[1], 
                                                    convertirAFecha(parametros[2]), 
                                                    parametros[3], 
                                                    parametros[4], 
                                                    parametros[5], 
                                                    parametros[6], 
                                                    parametros[7], 
                                                    parametros[8]);
                                                  
            if(resultado){
                //Exito al crear el cliente
                System.out.println("Se creó un nuevo cliente");
                //Se asigna al Map para guardar el contenido de respuesta que se va enviar al correo 
                response.put("subject", "clientecreado con éxito");
                response.put("body", "Se creó un nuevo clientecon los siguientes detalles:\n" + Arrays.toString(parametros));
            }else{
                //Problemas en la base de datos
                response.put("subject", "Error al crear el cliente");
                response.put("body", "No se pudo insertar el cliente. Error en la inserción.\n");    
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
                        + "Revisa los datalles de cada parámetro para la creación de cliente: " + "\n"
                        + detalleParametros
                           );
        }
        return response;    }
    
    public Map<String, Object> ListarClientes(String comando){
           Map<String, Object> response = new HashMap<>();
        String[] parametro = extraerParametros(comando);
        List<Cliente> listaClientes = new ArrayList<>();
        try {
            
            //verificacion de parámetros 
            if ( (parametro.length < 1) || (parametro.length > 1) ){
                response.put("subject", "Error en el parámetro");
                response.put("body", "Se esperaba un parámetro, pero se enviaron: "+ parametro.length + " parametros");
                return response;
            }
            
            //verifica que sea * el unico parametro
            if (parametro.length == 1 && "*".equals(parametro[0])){
                listaClientes = cliente.obtenerTodos();
                
                // Verifica si hay clientes en la base de datos
                if (listaClientes.isEmpty()) {
                    response.put("subject", "Sin clientes");
                    response.put("body", "No hay clientes registrados en el sistema.");
                    return response;            
                }             
                
            // Crear una lista de mapas para formatear cada cliente como un objeto clave-valor
            List<Map<String, String>> clientesFormateados = new ArrayList<>();
            List<String> columnas = Arrays.asList("Id", "Nombre", "Email", "Verificacion email", "Foto", "Teléfono", "Dirección", "Rol", "Estado");

            for (Cliente u : listaClientes) {
                Map<String, String> clienteData = new HashMap<>();
                clienteData.put("Id", String.valueOf(u.getId()));
                clienteData.put("Nombre", u.getName());
                clienteData.put("Email", u.getEmail());
                clienteData.put("Verificacion email", u.getEmail_verified_at().toString());
                clienteData.put("Foto", u.getPhoto());
                clienteData.put("Teléfono", u.getPhone());
                clienteData.put("Dirección", u.getAddress());
                clienteData.put("Rol", u.getRole());
                clienteData.put("Estado", u.getStatus());

                // Agrega los datos formateados del cliente a la lista
                clientesFormateados.add(clienteData);
            }

                response.put("subject", "Listado de clientes");
                response.put("body", clientesFormateados);
                response.put("columnas", columnas);
                response.put("esListado", true);

            }else{
                response.put("subject", "Parámetros incorrectos");
                response.put("body", "El comando de listado de clientes no es válido.");
            }
       
        } catch (Exception e) {
            response.put("subject", "Error al listar clientes");
            response.put("body", "Ocurrió un error al intentar obtener los clientes: " + e.getMessage());
        }

        return response;
    }
    
    public Map<String, Object> eliminarCliente(String comando){
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
            exito = cliente.eliminarcliente(criterios);
            if(exito) {
                 response.put("subject", "Elimnación de cliente exitosa");
                 response.put("body", "Se ha eliminado al cliente com parametro: "+ resultado);
                 return response;
            }
                 response.put("subject", "Error en Elimnar cliente");
                 response.put("body", "Ha ocurrido un error al eliminar al cliente com parametro: "+ resultado);
                 return response;
        }
        
            response.put("subject", "Error en parámetros");
            response.put("body", "Se encontraron los siguientes errores : "+ this.errores);
            return response;    
    }
    
    public Map<String, Object> EditarCliente(String comando){
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
                if (!columna.equals("id") && !cliente.existeRegistro(columna, valor)) {    
                    exito = cliente.actualizarRegistro(columna, valor, "id", id);
                }else{
                  response.put("subject", "Problema en la edición");
                 response.put("body", "El cliente con ID " + id + " ya tiene el valor: "+ valor +" en la columna: "+columna);   
                return response;
                }
              }

            // Responder si la actualización fue exitosa o no
            if (exito) {
                response.put("subject", "Modificación de cliente exitosa");
                response.put("body", "El cliente con ID " + id + " ha sido modificado exitosamente.");
            } else {
                response.put("subject", "Error al modificar cliente");
                response.put("body", "No se pudo modificar el cliente con ID " + id + ".");
            }
        } else {
            response.put("subject", "Error en la validación de parámetros");
            response.put("body", "Los parámetros no son válidos."+ errores);
        }

        return response;    }
    
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
        if (!validarTextoConEspacios(parametros[7]) || !"CLIENTE".equals(parametros[7])) {
            errores.add("El rol no es válido. Debe ser un texto que represente el rol (por ejemplo, 'admin' o 'cliente').");
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
    private String generarEstructuraCorrecta() {
        return "[nombre, email, fecha_verificacion, password, foto, telefono, direccion, rol, estado]";
    }

    public boolean verificarParametrosModificacion(Map<String, Object> criterios) {
    errores.clear();

    Set<String> parametrosValidos = Set.of("id","name", "email", "password", "photo", "phone", "address", "role", "status");

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
            errores.add("Parámetro inválido, o no pertenece a cliente: " + parametro);
            return false;
        }

        // Validación para "id"
        if (parametro.equals("id")) {
            if (!validarNumeroEntero(valor)) {
                errores.add("Parámetro ID no es un número entero: " + valor);
                return false;
            }

            if (!cliente.existeRegistro("id", Integer.parseInt(valor))) {
                errores.add("No existe cliente con el id: " + valor);
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
//        Object valorActual =cliente.obtenerValorActual(parametro, valor);
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
                if(!cliente.existeRegistro("id",valor)){
                    errores.add("No existe cliente con el id :" + valor);
                    return false;
                    
                }
            }
            
            if(parametro.equals("name")){
                errores.add("No existe cliente con el nombre :" + valor);
                return cliente.existeRegistro("name", valor);
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

    
    public String obtenerDetallesParametros() {
    return "nombre: Nombre completo del cliente. No se aceptan números\n" +
           "email: Correo electrónico en formato adecuado (ejemplo: ejemplo@dominio.com)\n" +
           "fecha_verificacion: Fecha de verificación del email (formato yyyy-MM-dd HH:mm:ss)\n" +
           "password: Contraseña del cliente(Debes ser de 8 caracteres minimo, debe incluir mínimo una letra mayúscula,debe incluir mínimo un número, debe incluir mínimo caracter especial)\n" +
           "foto: URL de la foto del cliente\n" +
           "telefono: Número de teléfono del cliente(Solo numeros)\n" +
           "direccion: Dirección del cliente\n" +
           "rol: Rol asignado al cliente(por ejemplo,'CLIENTE')\n" +
           "estado: Estado del cliente(String, por ejemplo, 'activo' o 'inactivo')";
}


     public boolean esTipoValido(String columna, Object valor) {
      boolean res = false;

       if (columna.equals("id")){
           res= validarNumeroEntero(valor.toString());
       }
       
       if (columna.equals("name")){
           System.out.println(valor.toString());
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
    