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
import models.Producto;
import models.Usuario;
import static patronResponsabilidad.ManejadorComandoAbs.extraerParametros;
import services.ProductoDAO;
/**
 *
 * @author C.Vargas
 */
public class ManejadorComandoProducto  extends ManejadorComandoAbs {
    private static final String COMANDOS = "^(LISTARPRODUCTOS|CREARPRODUCTO|EDITARPRODUCTO|ELIMINARPRODUCTO)\\[.*\\]$";
    private ProductoDAO productoDAO;
    private static final int CANTIDAD_PARAMETROS = 15; // Para los 16 atributos + id
    private List<String> errores;
    private String[] filtros;

    public ManejadorComandoProducto() {
        productoDAO = new ProductoDAO();
        errores = new ArrayList<>();
       // filtros = new ArrayList<>();
    }

    // Verifica si el comando es válido
    public boolean VerificarProceso(String comando) {
       return COMANDOS.matches(comando);

    }
    

    // Procesa el comando de acuerdo a la acción (listar, crear, editar, eliminar)
    public Map<String, Object> procesar(String comando) {
        if(!VerificarProceso(comando)){
            if (siguienteManejadorComando != null) {
             return siguienteManejadorComando.procesar(comando);
            }
            System.out.println("siguiente manejador de producto nulo");
        }
        Map<String, Object> resultado = new HashMap<>();
            // Implementa lógica para listar, crear, modificar o eliminar usuario según el comando
        if (comando.startsWith("LISTARPRODUCTOS")) {
            System.out.println("Entro en LISTARPRODUCTO");
            resultado = ListarProductos(comando);
            
        } else if (comando.startsWith("CREARPRODUCTO")) {
            System.out.println("Entro en CREARPRODUCTO");
            resultado = crearProducto(comando);
            
        } else if (comando.startsWith("EDITARPRODUCTO")) {
            System.out.println("Entro en EDITARPRODUCTO");
            resultado = EditarProducto(comando) ;
            
        } else if (comando.startsWith("ELIMINARPRODUCTO")) {
            System.out.println("Entro en ELIMINAPRODUCTO");
            return  EliminarProducto(comando);  
        } 
        System.out.println("no entro a ninugno");
        return resultado;
    }

    public Map<String, Object> ListarProductos(String comando) {
          Map<String, Object> response = new HashMap<>();
        String[] parametro = extraerParametros(comando);
        List<Producto> listaProductos = new ArrayList<>();
    try {
                    
        //verificacion de parámetros 
        if ( (parametro.length < 1) || (parametro.length > 1) ){
            response.put("subject", "Error en el parámetro");
            response.put("body", "Se esperaba un parámetro, pero se enviaron: "+ parametro.length + " parametros");
            return response;
        }

        if(parametro.length == 1 && "*".equals(parametro[0])){
            listaProductos = productoDAO.obtenerTodos();
            
            if (listaProductos.isEmpty()) {
                response.put("subject", "Sin usuarios");
                response.put("body", "No hay usuarios registrados en el sistema.");
                return response;            
            }     
        List<Map<String, String>> productosFormateados = new ArrayList<>();
        List<String> columnas = Arrays.asList(    "Id", "Nombre", "Slug", "Código", "Cantidad", "Tamaño", 
                                                "Precio", "Precio con descuento", "Imagen", "Más popular", 
                                                "Más vendido", "Estado", "Id de ciudad", "Id de categoría", 
                                                "Id de menú", "Id de cliente"); 
        for (Producto p : listaProductos) {
            Map<String, String> productoData = new HashMap<>();
            productoData.put("Id", String.valueOf(p.getId()));
            productoData.put("Nombre", p.getName());
            productoData.put("Slug", p.getSlug());
            productoData.put("Código", p.getCode());
            productoData.put("Cantidad", String.valueOf(p.getQty()));
            productoData.put("Tamaño", p.getSize());
            productoData.put("Precio", String.valueOf(p.getPrice()));
            productoData.put("Precio con descuento", String.valueOf(p.getDiscountPrice()));
            productoData.put("Imagen", p.getImage());
            productoData.put("Más popular", String.valueOf(p.isMostPopuler()));
            productoData.put("Más vendido", String.valueOf(p.isBestSeller()));
            productoData.put("Estado", p.getStatus());
            productoData.put("Id de ciudad", String.valueOf(p.getCityId()));
            productoData.put("Id de categoría", String.valueOf(p.getCategoryId()));
            productoData.put("Id de menú", String.valueOf(p.getMenuId()));
            productoData.put("Id de cliente", String.valueOf(p.getClientId()));

            // Agrega los datos formateados del producto a la lista
            productosFormateados.add(productoData);
        }
            response.put("subject", "Listado de productos");
            response.put("body", productosFormateados);
            response.put("columnas",columnas);
            response.put("esListado", true);
           
        }else{
             response.put("subject", "Parámetros incorrectos");
                response.put("body", "El comando de listado de productos no es válido.");
        }
    }catch (Exception e){
            response.put("subject", "Error al listar usuarios");
            response.put("body", "Ocurrió un error al intentar obtener los productos: " + e.getMessage());
    }
    return response;
    }

    public Map<String, Object> crearProducto(String comando) {
        boolean resultado;
        Map<String, Object> response = new HashMap<>();  
        String[] parametros = extraerParametros(comando);
        Map<String, Object> validacionParametros = verificarCantidadParametros(parametros,CANTIDAD_PARAMETROS);
        if (!"Correcto".equals(validacionParametros.get("body"))) {
               return validacionParametros; // Retorna el mensaje de error si falta o sobra algún parámetro
        }   
        
        if(validarParametros(parametros)){
        resultado = productoDAO.insertarProducto(parametros[0], 
                                                  parametros[1], 
                                                  parametros[2], 
                                                  convertirAEntero(parametros[3]), 
                                                  parametros[4], 
                                                  convertirADecimal(parametros[5]), 
                                                  convertirADecimal(parametros[6]),
                                                  parametros[7], 
                                                  convertirABooleano( parametros[8]),
                                                  convertirABooleano( parametros[9]),
                                                  parametros[10],
                                                  convertirAEntero(parametros[11]), 
                                                  convertirAEntero(parametros[12]), 
                                                  convertirAEntero(parametros[13]), 
                                                  convertirAEntero(parametros[14]));
                                                
          if(resultado){
              //Exito al crear el usuario
              System.out.println("Se creó un nuevo producto ");
              //Se asigna al Map para guardar el contenido de respuesta que se va enviar al correo 
              response.put("subject", "Producto creado con éxito");
              response.put("body", "Se creó un nuevo usuario con los siguientes detalles:\n" + Arrays.toString(parametros));
          }else{
              //Problemas en la base de datos
              response.put("subject", "Error al crear el Producto");
              response.put("body", "No se pudo insertar el Producto. Error en la inserción.\n");    
          }
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
    

    public Map<String, Object> EditarProducto(String comando) {
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
                if (!columna.equals("id") && !productoDAO.existeRegistro(columna, valor)) {
                    exito = productoDAO.actualizarProducto(columna, valor, "id", id);
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

  public Map<String, Object> EliminarProducto(String comando) {
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

    if (verificarParametrosEliminacion(criterios)) {
        exito = productoDAO.eliminarProducto(criterios);  // Método de eliminación de producto
        if (exito) {
            response.put("subject", "Eliminación de producto exitosa");
            response.put("body", "Se ha eliminado el producto con parámetros: " + resultado);
            return response;
        }
        response.put("subject", "Error al eliminar producto");
        response.put("body", "Ha ocurrido un error al eliminar el producto con parámetros: " + resultado);
        return response;
    }

    response.put("subject", "Error en parámetros");
    response.put("body", "Se encontraron los siguientes errores: " + this.errores);
    return response;
}
    
    public String obtenerDetallesParametros() {
    return "id: Identificador único del producto (número entero)\n" +
           "name: Nombre del producto (cadena de texto, no vacío)\n" +
           "slug: Identificador único legible para URL (cadena de texto)\n" +
           "code: Código único del producto (cadena de texto)\n" +
           "qty: Cantidad disponible del producto (número entero)\n" +
           "size: Tamaño del producto (cadena de texto, por ejemplo, 'S', 'M', 'L')\n" +
           "price: Precio del producto (número decimal, mayor a 0)\n" +
           "discountPrice: Precio con descuento del producto (número decimal, puede ser 0 o mayor)\n" +
           "image: URL de la imagen del producto (cadena de texto)\n" +
           "mostPopuler: Indica si el producto es el más popular (booleano, 'true' o 'false')\n" +
           "bestSeller: Indica si el producto es el más vendido (booleano, 'true' o 'false')\n" +
           "status: Estado del producto (cadena de texto, por ejemplo, 'ACTIVO' o 'INACTIVO')\n" +
           "cityId: ID de la ciudad asociada al producto (número entero)\n" +
           "categoryId: ID de la categoría asociada al producto (número entero)\n" +
           "menuId: ID del menú asociado al producto (número entero)\n" +
           "clientId: ID del cliente asociado al producto (número entero)";
}

    public String generarEstructuraCorrecta() {
    return "[id, name, slug, code, qty, size, price, discountPrice, image, mostPopuler, bestSeller, status, cityId, categoryId, menuId, clientId]";
}

    public boolean esTipoValido(String columna, Object valor) {
    boolean res = false;

    if (columna.equals("id")) {
        res = validarNumeroEntero(valor.toString());
    }
    if (columna.equals("name")) {
        res = validarTextoConEspacios(valor.toString());
    }
    if (columna.equals("slug")) {
        res = validarTextoConEspacios(valor.toString());
    }
    if (columna.equals("code")) {
        res = validarTextoConEspacios(valor.toString());
    }
    if (columna.equals("qty")) {
        res = validarNumeroEntero(valor.toString());
    }
    if (columna.equals("size")) {
        res = validarTextoConEspacios(valor.toString());
    }
    if (columna.equals("price")) {
        res = validarNumeroDecimal(valor.toString());
    }
    if (columna.equals("discountPrice")) {
        res = validarNumeroDecimal(valor.toString());
    }
    if (columna.equals("image")) {
        res = validarUrlImagen(valor.toString());
    }
    if (columna.equals("mostPopuler")) {
        res = validarBooleano(valor.toString());
    }
    if (columna.equals("bestSeller")) {
        res = validarBooleano(valor.toString());
    }
    if (columna.equals("status")) {
        res = validarTextoConEspacios(valor.toString());
    }
    if (columna.equals("cityId")) {
        res = validarNumeroEntero(valor.toString());
    }
    if (columna.equals("categoryId")) {
        res = validarNumeroEntero(valor.toString());
    }
    if (columna.equals("menuId")) {
        res = validarNumeroEntero(valor.toString());
    }
    if (columna.equals("clientId")) {
        res = validarNumeroEntero(valor.toString());
    }

    return res;
}

    public boolean validarParametros(String[] parametros) {
    errores.clear();

    // Comienza desde el segundo parámetro (el nombre)
    if (!validarTextoConEspacios(parametros[0])) {
        errores.add("El nombre no es válido. Debe contener solo letras y espacios.");
    }
    if (!validarTextoConEspacios(parametros[1])) {
        errores.add("El slug no es válido. Debe ser un texto sin espacios.");
    }
    if (!validarTextoConEspacios(parametros[2])) {
        errores.add("El código no es válido. Debe ser un texto sin espacios.");
    }
    if (!validarNumeroEntero(parametros[3])) {
        errores.add("La cantidad no es válida. Debe ser un número entero.");
    }
    if (!validarTextoConEspacios(parametros[4])) {
        errores.add("El tamaño no es válido. Debe ser un texto.");
    }
    if (!validarNumeroDecimal(parametros[5])) {
        errores.add("El precio no es válido. Debe ser un número decimal.");
    }
    if (!validarNumeroDecimal(parametros[6])) {
        errores.add("El precio con descuento no es válido. Debe ser un número decimal.");
    }
    if (!validarUrlImagen(parametros[7])) {
        errores.add("La URL de la imagen no es válida. Debe ser una URL válida.");
    }
    if (!validarBooleano(parametros[8])) {
        errores.add("El atributo 'mostPopuler' no es válido. Debe ser true o false.");
    }
    if (!validarBooleano(parametros[9])) {
        errores.add("El atributo 'bestSeller' no es válido. Debe ser true o false.");
    }
    if (!validarTextoConEspacios(parametros[10])) {
        errores.add("El estado no es válido. Debe ser un texto que represente el estado (por ejemplo, 'activo' o 'inactivo').");
    }
    if (!validarNumeroEntero(parametros[11])) {
        errores.add("El ID de la ciudad no es válido. Debe ser un número entero.");
    }
    if (!validarNumeroEntero(parametros[12])) {
        errores.add("El ID de la categoría no es válido. Debe ser un número entero.");
    }
    if (!validarNumeroEntero(parametros[13])) {
        errores.add("El ID del menú no es válido. Debe ser un número entero.");
    }
    if (!validarNumeroEntero(parametros[14])) {
        errores.add("El ID del cliente no es válido. Debe ser un número entero.");
    }

    // Si hay errores, construir el mensaje de respuesta y retornar false
    if (!errores.isEmpty()) {
        String mensajeError = "Errores encontrados:\n" + String.join("\n", errores);
        System.out.println(mensajeError); // También puedes enviar esto como parte de la respuesta
        return false;
    }

    // Si no hay errores, retornar true
    System.out.println("Todos los parámetros del producto son válidos.");
    return true;
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
            if (parametro.equals("id")){
                if(!validarNumeroEntero(valor)){
                errores.add("Parámetro ID no es un número entero: " + valor);
                    return false;
                }
                if(!productoDAO.existeRegistro("id",valor)){
                    errores.add("No existe usuario con el id :" + valor);
                    return false;
                    
                }
            }
        }
    return true; // Todos los parámetros son válidos
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

            if (!productoDAO.existeRegistro("id", Integer.parseInt(valor))) {
                errores.add("No existe usuario con el id: " + valor);
                return false;
            }
        }

        // Verificar que el valor sea del tipo adecuado
        if (!esTipoValido(parametro, valor)) {
            System.out.println("parametro: "+ parametro+ "valor: "+ valor);
            errores.add("El valor para " + parametro + " no es del tipo adecuado.");
            return false;
        }

    }
    return true; // Si todas las validaciones pasaron
}
     

}
