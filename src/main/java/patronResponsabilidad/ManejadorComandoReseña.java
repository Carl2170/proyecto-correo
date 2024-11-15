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
import models.Reseña;
import services.ReseñaDAO;
import services.ClienteDAO;
import services.UsuarioDAO;



/**
 *
 * @author C.Vargas
 */
public class ManejadorComandoReseña  extends ManejadorComandoAbs {
        
    private final String comandos ;
    private ReseñaDAO reseñaDAO;
    private int cantidadParametros = 5;
    private List<String> errores ;
    private String[] filtros;
    private ClienteDAO clienteDAO;
    private UsuarioDAO usuarioDAO;
    
    
    public ManejadorComandoReseña(){
        this.comandos= "^(LISTAR|CREAR|EDITAR|ELIMINAR)(RESEÑA)\\[.*\\]$";
        this.reseñaDAO =  new ReseñaDAO();
        this.errores = new ArrayList<>(); 
        this.clienteDAO = new ClienteDAO();
        this.usuarioDAO = new UsuarioDAO();
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
        
        // Implementa lógica para listar, crear, modificar o eliminar Reseñaegún el comando
        if (command.startsWith("LISTARRESEÑA")) {
            System.out.println("Entro en LISTARRESEÑA");
            resultado = ListarReseña(command);
            
        } else if (command.startsWith("CREARRESEÑA")) {
            System.out.println("Entro en CREARRESEÑA");
            resultado = crearReseña(command);
            
        } else if (command.startsWith("EDITARRESEÑA")) {
            System.out.println("Entro en EDITARRESEÑA");
            resultado = EditarReseña(command) ;
            
        } else if (command.startsWith("ELIMINARRESEÑA")) {
            System.out.println("Entro en ELIMINARRESEÑA");
            return eliminarReseña(command);
        }
        return resultado;
    }

    public Map<String, Object> ListarReseña(String comando) {
        Map<String, Object> response = new HashMap<>();
        String[] parametro = extraerParametros(comando);
        List<Reseña> listaReseñas = new ArrayList<>();

        try {
            // Verificación de parámetros 
            if (parametro.length < 1 || parametro.length > 1) {
                response.put("subject", "Error en el parámetro");
                response.put("body", "Se esperaba un parámetro, pero se enviaron: " + parametro.length + " parámetros");
                return response;
            }

            // Verifica que sea "*" el único parámetro
            if (parametro.length == 1 && "*".equals(parametro[0])) {
                listaReseñas = reseñaDAO.obtenerTodas();

                // Verifica si hay reseñas en la base de datos
                if (listaReseñas.isEmpty()) {
                    response.put("subject", "Sin reseñas");
                    response.put("body", "No hay reseñas registradas en el sistema.");
                    return response;
                }

                // Crear una lista de mapas para formatear cada reseña como un objeto clave-valor
                List<Map<String, String>> reseñasFormateadas = new ArrayList<>();
                List<String> columnas = Arrays.asList("Id", "Comentario", "Calificación", "Estado", "Cliente", "Usuario");

                for (Reseña r : listaReseñas) {
                    Map<String, String> reseñaData = new HashMap<>();
                    reseñaData.put("Id", String.valueOf(r.getId()));
                    reseñaData.put("Comentario", r.getComentario());
                    reseñaData.put("Calificación", String.valueOf(r.getCalificacion()));
                    reseñaData.put("Estado", r.getStatus());
                    reseñaData.put("Cliente", String.valueOf(r.getClienteId()));
                    reseñaData.put("Usuario", String.valueOf(r.getUserId()));

                    // Agrega los datos formateados de la reseña a la lista
                    reseñasFormateadas.add(reseñaData);
                }

                response.put("subject", "Listado de reseñas");
                response.put("body", reseñasFormateadas);
                response.put("columnas", columnas);
                response.put("esListado", true);

            } else {
                response.put("subject", "Parámetros incorrectos");
                response.put("body", "El comando de listado de reseñas no es válido.");
            }

        } catch (Exception e) {
            response.put("subject", "Error al listar reseñas");
            response.put("body", "Ocurrió un error al intentar obtener las reseñas: " + e.getMessage());
        }

        return response;
    }


    public Map<String, Object> crearReseña(String comando) {
    boolean resultado;
    Map<String, Object> response = new HashMap<>();
    String[] parametros = extraerParametros(comando);
    Map<String, Object> validacionParametros = verificarCantidadParametros(parametros, cantidadParametros);

    // Si hay un error en la validación de cantidad de parámetros, devuelve el error y termina la función
    if (!"Correcto".equals(validacionParametros.get("body"))) {
        return validacionParametros; // Retorna el mensaje de error si falta o sobra algún parámetro
    }

    // Valida la existencia del cliente en la BD antes de intentar la inserción
    if (!reseñaDAO.existeRegistro("id", parametros[3])) {
        response.put("subject", "Cliente no encontrado");
        response.put("body", "No existe una Reseña registrado con el ID: " + parametros[0]);
        return response;
    }

    // Valida la existencia del producto en la BD antes de intentar la inserción
    if (!reseñaDAO.existeRegistro("id", parametros[4])) {
        response.put("subject", "Usuario no encontrado");
        response.put("body", "No existe una Reseña registrado con el ID: " + parametros[1]);
        return response;
    }

    // Valida los formatos de cada parámetro enviado en el comando
    if (validarParametros(parametros)) {
        resultado = reseñaDAO.insertarReseña(
            (parametros[0]), 
            Integer.parseInt(parametros[1]), 
            (parametros[2]), 
            Integer.parseInt(parametros[3]), 
            Integer.parseInt(parametros[4]));

        if (resultado) {
            // Éxito al crear la reseña
            System.out.println("Se creó una nueva reseña");
            // Se asigna al Map para guardar el contenido de la respuesta
            response.put("subject", "Reseña creada con éxito");
            response.put("body", "Se creó una nueva reseña para el producto con los siguientes detalles:\n" + Arrays.toString(parametros));
        } else {
            // Problemas en la base de datos
            response.put("subject", "Error al crear la reseña");
            response.put("body", "No se pudo insertar la reseña. Error en la inserción.\n");
        }

    // Si existen errores en los parámetros
    } else {
        String listadoParametros = generarEstructuraCorrecta();
        String detalleParametros = obtenerDetallesParametros();
        response.put("subject", "Error en la validación de parámetros");
        response.put("body", "Algunos parámetros no son válidos.\n"
                + "Parámetros esperados: " + listadoParametros + "\n"
                + "Parámetros enviados: " + Arrays.toString(parametros) + "\n"
                + "Errores encontrados: \n"
                + this.errores
                + "Revisa los detalles de cada parámetro para la creación de la reseña: " + "\n"
                + detalleParametros
        );
    }
    return response;
}

    private Map<String, Object> EditarReseña(String comando) {
        errores.clear(); // Limpiar errores previos

    System.out.println("entro a modificar reseña");
    String parametros = extraerContenidoCorchetes(comando); // Extraer parámetros del comando
    Map<String, Object> response = new HashMap<>();
    Map<String, Object> resultado = parsearCriterios(parametros); // Parsear criterios del comando
    boolean exito = false;

    // Obtener los criterios y errores del resultado del parseo
    Map<String, Object> criterios = (Map<String, Object>) resultado.get("criterios");
    errores = (List<String>) resultado.get("errores");

    // Verificar si hay errores en el parseo de parámetros
    if (!errores.isEmpty()) {
        System.out.println("volvió a modificar, hay errores");
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
            if (!columna.equals("id") && !reseñaDAO.existeRegistro(columna, valor)) {    
                exito = reseñaDAO.actualizarReseña(columna, valor, "id", id);
            } else {
                response.put("subject", "Problema en la edición");
                response.put("body", "La reseña con ID " + id + " ya tiene el valor: " + valor + " en la columna: " + columna);
                return response;
            }
        }

        // Responder si la actualización fue exitosa o no
        if (exito) {
            response.put("subject", "Modificación de reseña exitosa");
            response.put("body", "La reseña con ID " + id + " ha sido modificada exitosamente.");
        } else {
            response.put("subject", "Error al modificar reseña");
            response.put("body", "No se pudo modificar la reseña con ID " + id + ".");
        }
    } else {
        response.put("subject", "Error en la validación de parámetros");
        response.put("body", "Los parámetros no son válidos. " + errores);
    }

    return response;

    }

    private Map<String, Object> eliminarReseña(String comando) {
            // Limpiar la lista de errores
        errores.clear();

        // Extraer los parámetros del comando
        String parametros = extraerContenidoCorchetes(comando);
        Map<String, Object> response = new HashMap<>();

        System.out.println("Parámetros para parsear: " + parametros);

        // Parsear los parámetros
        Map<String, Object> resultado = parsearCriterios(parametros);
        boolean exito = false;

        // Extraer los criterios y errores del resultado
        Map<String, Object> criterios = (Map<String, Object>) resultado.get("criterios");
        errores = (List<String>) resultado.get("errores");

        // Si hay errores en el parseo
        if (!errores.isEmpty()) {
            System.out.println("Errores en el parseo:");
            System.out.println(errores);

            response.put("subject", "Error en el formato de parámetros");
            response.put("body", "Los parámetros no son válidos: " + errores);
            return response;
        }

        // Verificar los parámetros antes de proceder con la eliminación
        if (verificarParametrosEliminacion(criterios)) {
            // Intentar eliminar la reseña
            exito = reseñaDAO.eliminarReseña(criterios);

            // Si la eliminación es exitosa
            if (exito) {
                response.put("subject", "Eliminación de reseña exitosa");
                response.put("body", "Se ha eliminado la reseña con los parámetros: " + resultado);
                return response;
            }

            // Si la eliminación falla
            response.put("subject", "Error al eliminar reseña");
            response.put("body", "Ha ocurrido un error al eliminar la reseña con los parámetros: " + resultado);
            return response;
        }

        // Si los parámetros no son válidos
        response.put("subject", "Error en parámetros");
        response.put("body", "Se encontraron los siguientes errores: " + this.errores);
        return response;
    }
  
    public boolean validarParametros(String[] parametros) {
    errores.clear();
    if (!validarTextoConEspacios(parametros[0])) {  // comentario
        errores.add("El comentario no es válido. No debe exceder los 255 caracteres.");
    }
    if (Integer.parseInt(parametros[1]) < 1 || Integer.parseInt(parametros[1]) > 5) {  // raiting
        errores.add("La calificación debe ser un número entre 1 y 5.");
    }
    if (!validarTextoConEspacios(parametros[2])) {  // estado
        errores.add("El estado no es válido. Debe ser un texto como 'activo' o 'pendiente'.");
    }
    if (!validarNumeroEntero(parametros[3])) {  // client_id
        errores.add("El ID del cliente no es válido.");
    }
    if (!validarNumeroEntero(parametros[4])) {  // user_id
        errores.add("El ID del usuario no es válido.");
    }

    if (!errores.isEmpty()) {
        String mensajeError = "Errores encontrados:\n" + String.join("\n", errores);
        System.out.println(mensajeError);  // También puedes enviar esto como parte de la respuesta
        return false;
    }

    System.out.println("Todos los parámetros son válidos.");
    return true;
}
    
    
  public String generarEstructuraCorrecta() {
    return "[comentario, calificación (1-5), estado (activo/pendiente), cliente_id, usuario_id]";
}

    public String obtenerDetallesParametros() {
        return "comentario: Texto de la reseña (máximo 255 caracteres)\n" +
               "calificación: Calificación numérica entre 1 y 5\n" +
               "estado: Estado de la reseña (por ejemplo, 'activo', 'pendiente')\n" +
               "cliente_id: ID del cliente que deja la reseña\n" +
               "usuario_id: ID del usuario o entidad relacionada con la reseña";
    }
    
    public boolean verificarParametrosEliminacion(Map<String, Object> criterios) {
    errores.clear();
    Set<String> parametrosValidos = Set.of("id", "comentario", "calificacion", "status", "clienteId", "userId");

    for (Map.Entry<String, Object> criterio : criterios.entrySet()) {
        String parametro = criterio.getKey();
        String valor = (String) criterio.getValue();
        
        // Si el valor está vacío
        if (valor.isEmpty()) {
            errores.add("El valor para " + parametro + " no puede estar vacío.");
            return false;
        }

        // Verificar si el parámetro está en la lista de parámetros válidos
        if (!parametrosValidos.contains(parametro)) {
            errores.add("Parámetro inválido: " + parametro);
            return false;
        }

        // Validación para "id"
        if (parametro.equals("id")) {
            if (!validarNumeroEntero(valor)) {
                errores.add("Parámetro ID no es un número entero: " + valor);
                return false;
            }
            if (!reseñaDAO.existeRegistro("id", valor)) {
                errores.add("No existe reseña con el id: " + valor);
                return false;
            }
        }

        // Validación para "comentario"
        if (parametro.equals("comentario")) {
            // Puede añadir validaciones adicionales si es necesario
            if (valor.length() > 500) {
                errores.add("El comentario no puede ser mayor de 500 caracteres.");
                return false;
            }
        }

        // Validación para "calificacion" (calificación de la reseña)
        if (parametro.equals("calificacion")) {
            try {
                int calificacion = Integer.parseInt(valor);
                if (calificacion < 1 || calificacion > 5) {
                    errores.add("La calificación debe estar entre 1 y 5: " + valor);
                    return false;
                }
            } catch (NumberFormatException e) {
                errores.add("La calificación no es un número válido: " + valor);
                return false;
            }
        }

        // Validación para "status"
        if (parametro.equals("status") && !valor.matches("^(activo|inactivo)$")) {
            errores.add("El valor de 'status' debe ser 'activo' o 'inactivo': " + valor);
            return false;
        }

        // Validación para "clienteId" (debe existir un cliente asociado)
        if (parametro.equals("clienteId")) {
            if (!validarNumeroEntero(valor) || !clienteDAO.existeRegistro("id", valor)) {
                errores.add("No existe cliente con el id: " + valor);
                return false;
            }
        }

        // Validación para "userId" (debe existir un usuario asociado)
        if (parametro.equals("userId")) {
            if (!validarNumeroEntero(valor) || !usuarioDAO.existeRegistro("id", valor)) {
                errores.add("No existe usuario con el id: " + valor);
                return false;
            }
        }
    }
    return true; // Todos los parámetros son válidos
}

    
    private boolean verificarParametrosModificacion(Map<String, Object> criterios) {
        errores.clear();  // Limpiar errores previos

        Set<String> parametrosValidos = Set.of("id", "title", "content", "rating", "status");

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
                errores.add("Parámetro inválido, o no pertenece a reseña: " + parametro);
                return false;
            }

            // Validación para "id"
            if (parametro.equals("id")) {
                if (!validarNumeroEntero(valor)) {
                    errores.add("Parámetro ID no es un número entero: " + valor);
                    return false;
                }

                if (!reseñaDAO.existeRegistro("id", Integer.parseInt(valor))) {
                    errores.add("No existe reseña con el id: " + valor);
                    return false;
                }
            }

            // Validación para "status"
            if (parametro.equals("status") && !valor.matches("^(activo|inactivo)$")) {
                System.out.println("El valor de 'status' debe ser 'activo' o 'inactivo': " + valor);
                errores.add("Parámetro inválido: " + parametro + ". El valor de 'status' debe ser 'activo' o 'inactivo'.");
                return false;
            }

            // Validación para "rating" (calificación), debe ser un número entre 1 y 5
            if (parametro.equals("rating")) {
                try {
                    int rating = Integer.parseInt(valor);
                    if (rating < 1 || rating > 5) {
                        errores.add("El valor de 'rating' debe ser un número entre 1 y 5.");
                        return false;
                    }
                } catch (NumberFormatException e) {
                    errores.add("El valor de 'rating' no es un número válido: " + valor);
                    return false;
                }
            }

            // Verificar que el valor sea del tipo adecuado para los otros parámetros
            if (!esTipoValido(parametro, valor)) {
                System.out.println("Parámetro: " + parametro + " valor: " + valor);
                errores.add("El valor para " + parametro + " no es del tipo adecuado.");
                return false;
            }
        }

        // Si todas las validaciones son exitosas
            return true;
    }
    
    public boolean esTipoValido(String columna, Object valor) {
    boolean res = false;

    // Validación para "id" (asegurando que sea un número entero)
    if (columna.equals("id")) {
        res = validarNumeroEntero(valor.toString());
    }

    // Validación para "title" (asegurando que sea texto con espacios)
    if (columna.equals("title")) {
        res = validarTextoConEspacios(valor.toString());
    }

    // Validación para "content" (asegurando que sea texto con espacios)
    if (columna.equals("content")) {
        res = validarTextoConEspacios(valor.toString());
    }

    // Validación para "rating" (asegurando que sea un número entero entre 1 y 5)
    if (columna.equals("rating")) {
        try {
            int rating = Integer.parseInt(valor.toString());
            if (rating >= 1 && rating <= 5) {
                res = true;
            }
        } catch (NumberFormatException e) {
            res = false;
        }
    }

    // Validación para "status" (asegurando que sea "activo" o "inactivo")
    if (columna.equals("status")) {
        res = valor.toString().matches("^(activo|inactivo)$");
    }

    return res;
}

}
