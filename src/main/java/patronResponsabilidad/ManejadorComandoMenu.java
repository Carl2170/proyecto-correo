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
import models.Menu;
import services.ClienteDAO;
import services.MenuDAO;

/**
 *
 * @author C.Vargas
 */
public class ManejadorComandoMenu extends ManejadorComandoAbs{
    
    private final String comandos;
    private MenuDAO menuDAO;
    private int cantidadParametros = 9;
    private List<String> errores ;
    private String[] filtros;
    
       public ManejadorComandoMenu(){
        
        this.comandos = "^(LISTAR|CREAR|EDITAR|ELIMINAR)(MENU)\\[.*\\]$";
        this.menuDAO = new MenuDAO();
        this.errores = new ArrayList<>();
    }
    
    @Override
    public boolean VerificarProceso(String comando) {
         return comando.matches(comandos);

    }

    @Override
public Map<String, Object> procesar(String command) {
    if (!VerificarProceso(command)) {
        return siguienteManejadorComando.procesar(command);
    }
    Map<String, Object> resultado = new HashMap<>();

    // Implementa lógica para listar, crear, modificar o eliminar menú según el comando
    if (command.startsWith("LISTARMENU")) {
        System.out.println("Entro en LISTARMENU");
        resultado = ListarMenus(command);

    } else if (command.startsWith("CREARMENU")) {
        System.out.println("Entro en CREARMENU");
        resultado = crearMenu(command);

    } else if (command.startsWith("EDITARMENU")) {
        System.out.println("Entro en EDITARMENU");
        resultado = EditarMenu(command);

    } else if (command.startsWith("ELIMINARMENU")) {
        System.out.println("Entro en ELIMINARMENU");
        return eliminarMenu(command);
    }
    return resultado;
}

    
    public Map<String, Object> crearMenu(String comando) {
       boolean resultado;
       Map<String, Object> response = new HashMap<>();
       String[] parametros = extraerParametros(comando);
       Map<String, Object> validacionParametros = verificarCantidadParametros(parametros, cantidadParametros);

       // Si hay un error en la validación de cantidad de parámetros, devuelve el error y termina la función
       if (!"Correcto".equals(validacionParametros.get("body"))) {
           return validacionParametros; // Retorna el mensaje de error si falta o sobra algún parámetro
       }

       // Valida la existencia del nombre del menú en la BD antes de intentar la inserción
       if (menuDAO.existeRegistro("menuName", parametros[0])) {
           response.put("subject", "Parámetro ya existente");
           response.put("body", "Ya existe un menú registrado con el nombre: " + parametros[0]);
           return response;
       }

       // Valida el formato de cada parámetro enviado en el comando
       if (validarParametros(parametros)) {
           resultado = menuDAO.insertarMenu(parametros[0], // menuName
                                         parametros[1]); // Additional parameters like description or price (if any)

           if (resultado) {
               // Éxito al crear el menú
               System.out.println("Se creó un nuevo menú");
               // Se asigna al Map para guardar el contenido de respuesta que se va a enviar
               response.put("subject", "Menú creado con éxito");
               response.put("body", "Se creó un nuevo menú con los siguientes detalles:\n" + Arrays.toString(parametros));
           } else {
               // Problemas en la base de datos
               response.put("subject", "Error al crear el menú");
               response.put("body", "No se pudo insertar el menú. Error en la inserción.\n");
           }
       } else {
           // Si existen errores en los parámetros
           String listadoParametros = generarEstructuraCorrecta();
           String detalleParametros = obtenerDetallesParametros();
           response.put("subject", "Error en la validación de parámetros");
           response.put("body", "Algunos parámetros no son válidos.\n"
                   + "Parámetros esperados: " + listadoParametros + "\n"
                   + "Parámetros enviados: " + Arrays.toString(parametros) + "\n"
                   + "Errores encontrados: \n"
                   + this.errores
                   + "Revisa los detalles de cada parámetro para la creación de menú: " + "\n"
                   + detalleParametros
           );
       }
       return response;
   }
    
    public Map<String, Object> ListarMenus(String comando) 
    {
        Map<String, Object> response = new HashMap<>();
        String[] parametro = extraerParametros(comando);
        List<Menu> listaMenus = new ArrayList<>();

        try {
            // Verificación de parámetros
            if ((parametro.length < 1) || (parametro.length > 1)) {
                response.put("subject", "Error en el parámetro");
                response.put("body", "Se esperaba un parámetro, pero se enviaron: " + parametro.length + " parámetros");
                return response;
            }

            // Verifica que sea '*' el único parámetro
            if (parametro.length == 1 && "*".equals(parametro[0])) {
                listaMenus = menuDAO.obtenerTodos();  // Obtener todos los menús

                // Verifica si hay menús en la base de datos
                if (listaMenus.isEmpty()) {
                    response.put("subject", "Sin menús");
                    response.put("body", "No hay menús registrados en el sistema.");
                    return response;
                }

                // Crear una lista de mapas para formatear cada menú como un objeto clave-valor
                List<Map<String, String>> menusFormateados = new ArrayList<>();
                List<String> columnas = Arrays.asList("Id", "Nombre", "Descripción", "Precio", "Disponibilidad");

                for (Menu m : listaMenus) {
                    Map<String, String> menuData = new HashMap<>();
                    menuData.put("Id", String.valueOf(m.getId()));
                    menuData.put("Nombre", m.getMenuName());
                    menuData.put("Imagen", m.getImage());
               
                    // Agrega los datos formateados del menú a la lista
                    menusFormateados.add(menuData);
                }

                response.put("subject", "Listado de menús");
                response.put("body", menusFormateados);
                response.put("columnas", columnas);
                response.put("esListado", true);

            } else {
                response.put("subject", "Parámetros incorrectos");
                response.put("body", "El comando de listado de menús no es válido.");
            }

        } catch (Exception e) {
            response.put("subject", "Error al listar menús");
            response.put("body", "Ocurrió un error al intentar obtener los menús: " + e.getMessage());
        }

        return response;
    }
    public Map<String, Object> eliminarMenu(String comando) {
        errores.clear();
        String parametros = extraerContenidoCorchetes(comando);
        Map<String, Object> response = new HashMap<>();
        System.out.println("Parámetros para parsear: " + parametros);
        Map<String, Object> resultado = parsearCriterios(parametros);
        boolean exito = false;

        Map<String, Object> criterios = (Map<String, Object>) resultado.get("criterios");
        errores = (List<String>) resultado.get("errores");

        if (!errores.isEmpty()) {
            System.out.println("Errores en el parseo:");
            System.out.println(errores);
            response.put("subject", "Error en el formato de parámetros");
            response.put("body", "Los parámetros no son válidos: " + errores);
            return response;
        }

        // Verificar los parámetros de eliminación del menú
        if (verificarParametrosEliminacion(criterios)) {
            exito = menuDAO.eliminarMenu(criterios); // Llamada al método de eliminación de menú
            if (exito) {
                response.put("subject", "Eliminación de menú exitosa");
                response.put("body", "Se ha eliminado el menú con el parámetro: " + resultado);
                return response;
            }

            response.put("subject", "Error al eliminar menú");
            response.put("body", "Ha ocurrido un error al eliminar el menú con el parámetro: " + resultado);
            return response;
        }

        response.put("subject", "Error en parámetros");
        response.put("body", "Se encontraron los siguientes errores: " + this.errores);
        return response;
    }

    public Map<String, Object> EditarMenu(String comando) {
        errores.clear(); // Limpiar errores previos

        System.out.println("Entró a modificar el menú");
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
                if (!columna.equals("id") && !menuDAO.existeRegistro(columna, valor)) {    
                    exito = menuDAO.actualizarRegistro(columna, valor, "id", id);
                } else {
                    response.put("subject", "Problema en la edición");
                    response.put("body", "El menú con ID " + id + " ya tiene el valor: " + valor + " en la columna: " + columna);
                    return response;
                }
            }

            // Responder si la actualización fue exitosa o no
            if (exito) {
                response.put("subject", "Modificación de menú exitosa");
                response.put("body", "El menú con ID " + id + " ha sido modificado exitosamente.");
            } else {
                response.put("subject", "Error al modificar menú");
                response.put("body", "No se pudo modificar el menú con ID " + id + ".");
            }
        } else {
            response.put("subject", "Error en la validación de parámetros");
            response.put("body", "Los parámetros no son válidos: " + errores);
        }

        return response;    
    }

    public boolean validarParametros(String[] parametros) { 
        errores.clear(); 

        // Validación para 'menu_name' (nombre del menú)
        if (!validarTextoConEspacios(parametros[0])) {
            errores.add("El nombre del menú no es válido. Debe contener solo letras y espacios.");
        }

        // Validación para 'image' (URL de la imagen)
        if (!validarUrlImagen(parametros[1])) {
            errores.add("La URL de la imagen no es válida. Debe ser una URL válida.");
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
        return "[nombre del menu, url de la imagen]";
    }
    
    public boolean verificarParametrosModificacion(Map<String, Object> criterios) {
        errores.clear();

        // Parametros válidos para la tabla 'menu'
        Set<String> parametrosValidos = Set.of("id", "menu_name", "image");

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
                errores.add("Parámetro inválido, o no pertenece a 'menu': " + parametro);
                return false;
            }

            // Validación para "id"
            if (parametro.equals("id")) {
                if (!validarNumeroEntero(valor)) {
                    errores.add("Parámetro ID no es un número entero: " + valor);
                    return false;
                }

                if (!menuDAO.existeRegistro("id", Integer.parseInt(valor))) {
                    errores.add("No existe menú con el id: " + valor);
                    return false;
                }
            }

            // Validación para "menu_name"
            if (parametro.equals("menu_name")) {
                if (!validarTextoConEspacios(valor)) {
                    errores.add("El valor para 'menu_name' no es válido: " + valor);
                    return false;
                }
            }

            // Validación para "image" (URL de imagen)
            if (parametro.equals("image")) {
                if (!validarUrlImagen(valor)) {
                    errores.add("La URL de la imagen no es válida: " + valor);
                    return false;
                }
            }

            // Verificar que el valor sea del tipo adecuado
            if (!esTipoValido(parametro, valor)) {
                System.out.println("Parámetro: " + parametro + " valor: " + valor);
                errores.add("El valor para " + parametro + " no es del tipo adecuado.");
                return false;
            }

            // Verificación adicional de si el valor ha cambiado (comparando con el valor actual en la base de datos)
            // Object valorActual = menu.obtenerValorActual(parametro, valor);
            // if (valorActual == null || !valorActual.equals(valor)) {
            //     errores.add("El valor para " + parametro + " no es válido o no ha cambiado.");
            //     return false;
            // }
        }
        return true; // Todos los parámetros son válidos
    }
    
    public boolean verificarParametrosEliminacion(Map<String, Object> criterios) {
    errores.clear();
    Set<String> parametrosValidos = Set.of("id", "menu_name", "image");
   
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

        // Validación para 'id' (debe ser un número entero)
        if (parametro.equals("id")) {
            if (!validarNumeroEntero(valor)) {
                errores.add("Parámetro ID no es un número entero: " + valor);
                return false;
            }
            if (!menuDAO.existeRegistro("id", valor)) {
                errores.add("No existe menú con el ID: " + valor);
                return false;
            }
        }
        
        // Validación para 'menu_name' (verifica que el nombre del menú existe)
        if (parametro.equals("menu_name")) {
            if (!menuDAO.existeRegistro("menu_name", valor)) {
                errores.add("No existe menú con el nombre: " + valor);
                return false;
            }
        }

        // Validación para 'image' (debe ser una URL válida)
        if (parametro.equals("image")) {
            if (!validarUrlImagen(valor)) {
                errores.add("La URL de la imagen no es válida: " + valor);
                return false;
            }
        }
    }
    return true; // Todos los parámetros son válidos
}
  
    public String obtenerDetallesParametros() {
        return "id: Identificador único del menú (un número entero único)\n" +
               "menu_name: Nombre del menú (debe ser una cadena de texto, sin números)\n" +
               "image: URL de la imagen del menú (debe ser una URL válida que apunte a una imagen)";
    }

    public boolean esTipoValido(String columna, Object valor) {
    boolean res = false;

    if (columna.equals("id")) {
        res = validarNumeroEntero(valor.toString());
    }

    if (columna.equals("menu_name")) {
        res = validarTextoConEspacios(valor.toString());  // Validates that the menu name is a text string
    }

    if (columna.equals("image")) {
        res = validarUrlImagen(valor.toString());  // Validates that the image is a valid URL
    }

    return res;
}

}
