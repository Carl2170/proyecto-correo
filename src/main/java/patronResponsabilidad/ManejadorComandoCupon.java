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
import models.Cupon;
import services.ClienteDAO;
import services.CuponDAO;

/**
 *
 * @author C.Vargas
 */
public class ManejadorComandoCupon  extends ManejadorComandoAbs {
    private final String comandos;
    private CuponDAO cuponDAO;
    private ClienteDAO clienteDAO;
    private int cantidadParametros = 9;
    private List<String> errores ;
    private String[] filtros;
    
       public ManejadorComandoCupon(){
        
        this.comandos = "^(LISTAR|CREAR|EDITAR|ELIMINAR)(CUPON)\\[.*\\]$";

        this.cuponDAO = new CuponDAO();
        this.errores = new ArrayList<>();
    }
    @Override
    public boolean VerificarProceso(String comando) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    

    @Override
    public Map<String, Object> procesar(String command) {
        if(!VerificarProceso(command)){
             return siguienteManejadorComando.procesar(command);
        }
        Map<String, Object> resultado= new HashMap<>(); ;
        
        // Implementa lógica para listar, crear, modificar o eliminar clientesegún el comando
        if (command.startsWith("LISTARCUPON")) {
            System.out.println("Entro en LISTARCUPON");
            resultado = ListarCupones(command);
            
        } else if (command.startsWith("CREARCUPON")) {
            System.out.println("Entro en CREARCUPON");
            resultado = crearCupon(command);
            
        } else if (command.startsWith("EDITARCUPON")) {
            System.out.println("Entro en EDITARCUPON");
            resultado = EditarCupon(command) ;
            
        } else if (command.startsWith("ELIMINARCUPON")) {
            System.out.println("Entro en ELIMINARCUPON");
            return eliminarCupon(command);
        }
        return resultado;    
    }    
    
public Map<String, Object> ListarCupones(String comando){
    Map<String, Object> response = new HashMap<>();
    String[] parametro = extraerParametros(comando); // Extraer parámetros del comando
    List<Cupon> listaCupones = new ArrayList<>();
    
    try {
        // Verificación de parámetros
        if ((parametro.length < 1) || (parametro.length > 1)) {
            response.put("subject", "Error en el parámetro");
            response.put("body", "Se esperaba un parámetro, pero se enviaron: " + parametro.length + " parámetros");
            return response;
        }
        
        // Verifica que sea '*' el único parámetro
        if (parametro.length == 1 && "*".equals(parametro[0])) {
            listaCupones = cuponDAO.obtenerTodos(); // Método para obtener todos los cupones
            
            // Verifica si hay cupones en la base de datos
            if (listaCupones.isEmpty()) {
                response.put("subject", "Sin cupones");
                response.put("body", "No hay cupones registrados en el sistema.");
                return response;
            }
            
            // Crear una lista de mapas para formatear cada cupón como un objeto clave-valor
            List<Map<String, String>> cuponesFormateados = new ArrayList<>();
            List<String> columnas = Arrays.asList("Id", "Código", "Descuento", "Fecha Expiración", "Estado");

            for (Cupon c : listaCupones) {
                Map<String, String> cuponData = new HashMap<>();
                cuponData.put("Id", String.valueOf(c.getId()));
                cuponData.put("Nombre", c.getCoupon_name());
                cuponData.put("Descripcion", c.getCoupon_desc());
                cuponData.put("Descuento",  String.valueOf(c.getDiscount()));
                cuponData.put("Validez", c.getValidity().toString());
                cuponData.put("Estado",String.valueOf(c.getStatus()));
                cuponData.put("Cliente_id",  String.valueOf(c.getClient_id()));

                // Agrega los datos formateados del cupón a la lista
                cuponesFormateados.add(cuponData);
            }

            response.put("subject", "Listado de cupones");
            response.put("body", cuponesFormateados);
            response.put("columnas", columnas);
            response.put("esListado", true);

        } else {
            response.put("subject", "Parámetros incorrectos");
            response.put("body", "El comando de listado de cupones no es válido.");
        }
        
    } catch (Exception e) {
        response.put("subject", "Error al listar cupones");
        response.put("body", "Ocurrió un error al intentar obtener los cupones: " + e.getMessage());
    }

    return response;
}


    public Map<String, Object> crearCupon(String command) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public Map<String, Object> EditarCupon(String comando){
    errores.clear(); // Limpiar errores previos
    
    System.out.println("Entro a modificar el cupón");
    String parametros = extraerContenidoCorchetes(comando); // Extraer parámetros del comando
    Map<String, Object> response = new HashMap<>();
    Map<String, Object> resultado = parsearCriterios(parametros); // Parsear criterios del comando
    boolean exito = false;

    // Obtener los criterios y errores del resultado del parseo
    Map<String, Object> criterios = (Map<String, Object>) resultado.get("criterios");
    errores = (List<String>) resultado.get("errores");

    // Verificar si hay errores en el parseo de parámetros
    if (!errores.isEmpty()) {
        System.out.println("Volvió a modificar, hay errores");
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
            if (!columna.equals("id") && !cuponDAO.existeRegistro(columna, valor)) {    
                exito = cuponDAO.actualizarRegistro(columna, valor, "id", id);
            } else {
                response.put("subject", "Problema en la edición");
                response.put("body", "El cupón con ID " + id + " ya tiene el valor: " + valor + " en la columna: " + columna);   
                return response;
            }
        }

        // Responder si la actualización fue exitosa o no
        if (exito) {
            response.put("subject", "Modificación de cupón exitosa");
            response.put("body", "El cupón con ID " + id + " ha sido modificado exitosamente.");
        } else {
            response.put("subject", "Error al modificar cupón");
            response.put("body", "No se pudo modificar el cupón con ID " + id + ".");
        }
    } else {
        response.put("subject", "Error en la validación de parámetros");
        response.put("body", "Los parámetros no son válidos: " + errores);
    }

    return response;
}


    public Map<String, Object> eliminarCupon(String comando){
        errores.clear(); // Limpiar errores previos
        String parametros = extraerContenidoCorchetes(comando); // Extraer parámetros del comando
        Map<String, Object> response = new HashMap<>();
        System.out.println("Parámetros para parsear: " + parametros);

        Map<String, Object> resultado = parsearCriterios(parametros); // Parsear criterios del comando
        boolean exito = false;

        Map<String, Object> criterios = (Map<String, Object>) resultado.get("criterios");
        errores = (List<String>) resultado.get("errores");

        // Verificar si hay errores en el parseo de los parámetros
        if (!errores.isEmpty()) {
            System.out.println("Errores en el parseo:");
            System.out.println(errores);
            response.put("subject", "Error en el formato de parámetros");
            response.put("body", "Los parámetros no son válidos: " + errores);
            return response;
        }

        // Verificar que los parámetros sean válidos antes de proceder
        if (verificarParametrosEliminacion(criterios)) {
            exito = cuponDAO.eliminarCupon(criterios); // Llamar al método de eliminación de cupones

            if (exito) {
                response.put("subject", "Eliminación de cupón exitosa");
                response.put("body", "Se ha eliminado el cupón con parámetros: " + resultado);
                return response;
            } else {
                response.put("subject", "Error al eliminar cupón");
                response.put("body", "Ha ocurrido un error al eliminar el cupón con parámetros: " + resultado);
                return response;
            }
        }

        // Si los parámetros no son válidos, devolver un mensaje de error
        response.put("subject", "Error en parámetros");
        response.put("body", "Se encontraron los siguientes errores: " + this.errores);
        return response;
    }

    public boolean validarParametros(String[] parametros) {
        errores.clear();

        // Validar nombre del cupón
        if (!validarTextoConEspacios(parametros[0])) {
            errores.add("El nombre del cupón no es válido. Debe contener solo letras y espacios.");
        }

        // Validar descripción del cupón
        if (!validarTextoConEspacios(parametros[1])) {
            errores.add("La descripción del cupón no es válida.");
        }

        // Validar descuento
        if (!validarNumeroDecimal(parametros[2])) {
            errores.add("El descuento no es válido. Debe ser un número decimal.");
        }

        // Validar fecha de validez
        if (!validarFecha(parametros[3])) {
            errores.add("La fecha de validez no es válida. Debe estar en formato yyyy-MM-dd.");
        }

        // Validar estado (activo o inactivo)
        if (!"activo".equals(parametros[4]) && !"inactivo".equals(parametros[4])) {
            errores.add("El estado no es válido. Debe ser 'activo' o 'inactivo'.");
        }

        // Validar ID de cliente (debe ser un número entero)
        if (!validarNumeroEntero(parametros[5])) {
            errores.add("El ID de cliente no es válido. Debe ser un número entero.");
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

    
    public String generarEstructuraCorrecta() {
    return "[id, coupon_name, coupon_desc, discount, validity, status, client_id]";
    }
  
    public boolean verificarParametrosModificacion(Map<String, Object> criterios) {
        errores.clear();

        // Lista de parámetros válidos para un cupón
        Set<String> parametrosValidos = Set.of("id", "coupon_name", "coupon_desc", "discount", "validity", "status", "client_id");

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
                errores.add("Parámetro inválido, o no pertenece a cupón: " + parametro);
                return false;
            }

            // Validación para "id"
            if (parametro.equals("id")) {
                if (!validarNumeroEntero(valor)) {
                    errores.add("Parámetro ID no es un número entero: " + valor);
                    return false;
                }

                if (!cuponDAO.existeRegistro("id", Integer.parseInt(valor))) {
                    errores.add("No existe cupón con el id: " + valor);
                    return false;
                }
            }

            // Validación para "status"
            if (parametro.equals("status") && !valor.matches("^(activo|inactivo)$")) {
                System.out.println("El valor de 'status' debe ser 'activo' o 'inactivo': " + valor);
                errores.add("Parámetro inválido: " + parametro + ". El valor de 'status' debe ser 'activo' o 'inactivo'.");
                return false;
            }

            // Verificación que el valor sea del tipo adecuado
            if (!esTipoValido(parametro, valor)) {
                System.out.println("Parámetro: " + parametro + " Valor: " + valor);
                errores.add("El valor para " + parametro + " no es del tipo adecuado.");
                return false;
            }

            // Verificación adicional de si el valor ha cambiado (si es necesario comparar con la base de datos)
    //        Object valorActual = obtenerValorActual(parametro, valor);
    //        if (valorActual == null || !valorActual.equals(valor)) {
    //            errores.add("El valor para " + parametro + " no es válido o no ha cambiado.");
    //            return false;
    //        }
        }

        return true; // Si todas las validaciones pasaron
    }

    public boolean verificarParametrosEliminacion(Map<String, Object> criterios) {
        errores.clear();
        Set<String> parametrosValidos = Set.of("id", "coupon_name", "status", "client_id");

        for (Map.Entry<String, Object> criterio : criterios.entrySet()) {
            String parametro = criterio.getKey();
            String valor = (String) criterio.getValue();

            // Si el valor es vacío
            if (valor.isEmpty()) {
                errores.add("El valor para " + parametro + " no puede estar vacío.");
                return false;
            }

            // Verificar si el parámetro está en la lista de parámetros válidos
            if (!parametrosValidos.contains(parametro)) {
                System.out.println("Parámetro inválido: " + parametro);
                errores.add("Parámetro inválido: " + parametro);
                return false;
            }

            // Validar el parámetro 'id'
            if (parametro.equals("id")) {
                if (!validarNumeroEntero(valor)) {
                    errores.add("Parámetro ID no es un número entero: " + valor);
                    return false;
                }
                if (!cuponDAO.existeRegistro("id", valor)) {
                    errores.add("No existe cupón con el id: " + valor);
                    return false;
                }
            }

            // Validar el parámetro 'coupon_name'
            if (parametro.equals("coupon_name")) {
                if (!validarTextoConEspacios(valor)) {
                    errores.add("El nombre del cupón no es válido: " + valor);
                    return false;
                }
            }

            // Validar el parámetro 'status'
            if (parametro.equals("status") && !valor.matches("^(activo|inactivo)$")) {
                System.out.println("El valor de 'status' debe ser 'activo' o 'inactivo': " + valor);
                errores.add("El valor de 'status' debe ser 'activo' o 'inactivo'.");
                return false;
            }

            // Validar 'client_id'
            if (parametro.equals("client_id")) {
                if (!validarNumeroEntero(valor)) {
                    errores.add("El ID del cliente no es un número entero: " + valor);
                    return false;
                }
                if (!clienteDAO.existeRegistro("id", valor)) {
                    errores.add("No existe cliente con el id: " + valor);
                    return false;
                }
            }
        }
        return true; // Todos los parámetros son válidos
    }

    
    public String obtenerDetallesParametros() {
        return "id: Identificador único del cupón (número entero)\n" +
               "coupon_name: Nombre del cupón (solo texto)\n" +
               "coupon_desc: Descripción del cupón (texto)\n" +
               "discount: Descuento del cupón (valor numérico entero)\n" +
               "validity: Fecha de validez del cupón (formato yyyy-MM-dd)\n" +
               "status: Estado del cupón ('activo' o 'inactivo')\n" +
               "client_id: Identificador del cliente asociado al cupón (número entero)";
    }
    
    public boolean esTipoValido(String columna, Object valor) {
    boolean res = false;

    if (columna.equals("id")) {
        res = validarNumeroEntero(valor.toString());
    }

    if (columna.equals("coupon_name")) {
        res = validarTextoConEspacios(valor.toString());
    }

    if (columna.equals("coupon_desc")) {
        res = validarTextoConEspacios(valor.toString());
    }

    if (columna.equals("discount")) {
        res = validarNumeroEntero(valor.toString());
    }

    if (columna.equals("validity")) {
        res = validarFecha(valor.toString());
    }

    if (columna.equals("status")) {
        res = valor.toString().matches("^(activo|inactivo)$");
    }

    if (columna.equals("client_id")) {
        res = validarNumeroEntero(valor.toString());
    }

    return res;
}



}
    

