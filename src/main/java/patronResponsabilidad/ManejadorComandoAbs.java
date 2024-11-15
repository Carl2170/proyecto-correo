/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package patronResponsabilidad;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author C.Vargas
 */
public abstract class ManejadorComandoAbs implements ManejadorComando {
        protected ManejadorComandoAbs siguienteManejadorComando;

        
    public void setManejadorComando(ManejadorComandoAbs  manejadorComando) {
        this.siguienteManejadorComando = manejadorComando;
    }
    
    public abstract boolean VerificarProceso(String comando);

    
//    @Override
//    public String  manejador(String comando) {
//          if(VerificarProceso(comando)){
//              return procesar(comando);
//          }else if(siguienteManejadorComando != null){
//              return siguienteManejadorComando.manejador(comando);
//          }
//          return "Error, comando desconocido o parametros invalidos";
//    }
    
    public abstract Map<String, Object> procesar(String command);
    
    protected boolean validarTextoConEspacios(String texto) {
        
        if(texto != null && !texto.isEmpty() && texto.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")){
            System.out.println("Validar texto con espacio: correcto");
            return true;
        }
        System.out.println("Validar texto con espacio: incorrecto");

        return false;
    }
    
    protected boolean validarNumeroEntero(String valor) {
        try {
            Integer.parseInt(valor);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    protected int convertirAEntero(String comando){
        int entero = Integer.parseInt(comando);
        return entero;
    }
    
    protected boolean validarTelefono(String valor) {
    // Eliminar los espacios antes de validar
    valor = valor.replace(" ", "");

    // Verificar si el valor empieza con "+" o no, pero luego debe ser solo números
    if (valor.matches("^[+]?[0-9]+$")) {
        System.out.println("Validar telefono : correcto");

        return true;  
    }
    System.out.println("Validar telefono : incorrecto");
    return false; 
}

    protected boolean validarNumeroDecimal(String valor) {
        try {
            Double.parseDouble(valor);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    protected Double convertirADecimal(String valor){
        Double numero =  Double.parseDouble(valor);
        return numero;
    }

    protected boolean validarFecha(String fecha) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(fecha);
            System.out.println("validacion fecha correcta");
            return true;
        } catch (ParseException e) {
          System.out.println("error al validar la fecha");
            return false;
        }
    }

    protected Timestamp convertirAFecha(String fecha){
          // Definir el formato esperado de la fecha
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            // Parsear la cadena de fecha a un objeto Date
            java.util.Date date = dateFormat.parse(fecha);

            // Convertir el objeto Date a un Timestamp
            Timestamp timestamp = new Timestamp(date.getTime());

            return timestamp;

        } catch (Exception e) {
            // Manejo de excepciones si la fecha no es válida
            System.out.println("Error al convertir la fecha: " + e.getMessage());
            return null;
        }
    }
  
    protected boolean validarEmail(String email) {
    if (email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            System.out.println("validarEmail: correcto");
            return true;
        }
        System.out.println("validarEmail: incorrecto"); 
        return false;
    }
    
    protected boolean verificarPassword(String password) {
    // Expresión regular para validar una contraseña con al menos:
    // - 8 caracteres de longitud
    // - Al menos una letra minúscula
    // - Al menos una letra mayúscula
    // - Al menos un número
    // - Al menos un carácter especial (por ejemplo, !@#$%^&*()-+=)
    String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-]).{8,}$";
    if (password != null && password.matches(regex)){
        System.out.println("verificarPassword: correcto");
        return true;
    }
    System.out.println("verificarPassword: incorrecto");
    return false;
}
    
    protected String validarPassword(String password) {
    if (password == null || password.isEmpty()) {
        return "La contraseña no puede estar vacía.";
    }
    
    // Verificar longitud mínima
    if (password.length() < 8) {
        return "La contraseña debe tener al menos 8 caracteres.";
    }
    
    // Verificar al menos una letra minúscula
    if (!password.matches(".*[a-z].*")) {
        return "La contraseña debe incluir al menos una letra minúscula.";
    }

    // Verificar al menos una letra mayúscula
    if (!password.matches(".*[A-Z].*")) {
        return "La contraseña debe incluir al menos una letra mayúscula.";
    }

    // Verificar al menos un número
    if (!password.matches(".*\\d.*")) {
        return "La contraseña debe incluir al menos un número.";
    }

    // Verificar al menos un carácter especial
    if (!password.matches(".*[!@#$%^&*()_+=-].*")) {
        return "La contraseña debe incluir al menos un carácter especial (por ejemplo: !@#$%^&*()-+=).";
    }

    // Si pasa todas las validaciones
    return "Contraseña válida";
    }
    
    protected boolean validarUrlImagen(String url) {
        if (url == null || url.isEmpty()) {
            System.out.println("Url de imagen: vacia");
            return false;
        }

        // Expresión regular simple para cualquier URL que termine en extensiones de imagen comunes
        String urlRegex = ".*\\.(jpg|jpeg|png|gif|bmp|svg|webp)$";

        if (!url.matches(urlRegex)) {
          System.out.println("Url de imagen: sin extension de imagen");
            return false;
        }
       System.out.println("Url de imagen: correcta");
       return true;

    }
    
    protected boolean validarDireccion(String direccion) {
        // Expresión regular para validar la dirección
        String regex = "^[a-zA-Z0-9\\s,.-]+$";

        // Comprobar si la dirección coincide con la expresión regular
        if (direccion != null && direccion.matches(regex)){
            System.out.println("validarDireccion: correcta");
            return true;
        }   
        System.out.println("validarDireccion: incorrecta");
        return false;
    }

    //funcion para parsear parametros de eliminacion
    public Map<String, Object> parsearCriterios(String comando) {
        System.out.println("llego los parametros a parsesarCriterios: "+ comando);
        Map<String, String> criterios = new HashMap<>();
        List<String> errores = new ArrayList<>();

        try {
            System.out.println("entro try parsear");
            // Extraer los criterios entre corchetes
            int startIndex = comando.indexOf("[");
            int endIndex = comando.indexOf("]");

            // Verificar si los corchetes están presentes
            if (startIndex == -1 || endIndex == -1) {
                System.out.println("entro en el if parserar");
                errores.add("Formato incorrecto: Falta el bloque de criterios entre corchetes");
                return Map.of("criterios", criterios, "errores", errores);
            }

            String criteriosStr = comando.substring(startIndex + 1, endIndex);

            System.out.println("Criterios extraídos: " + criteriosStr);
            
            // Separar cada criterio por ";", soportando también un único criterio
            String[] paresCriterios = criteriosStr.split(";");
            System.out.println("Pares de criterios: " + Arrays.toString(paresCriterios));

            for (String par : paresCriterios) {
                String[] claveValor = par.split("=");
                System.out.println("Clave-Valor: " + Arrays.toString(claveValor));
                if (claveValor.length == 2 && !claveValor[0].trim().isEmpty() && !claveValor[1].trim().isEmpty()) {
                    String clave = claveValor[0].trim();
                    String valor = claveValor[1].trim();

                    // Eliminar comillas si están presentes
                    if (valor.startsWith("\"") && valor.endsWith("\"")) {
                        valor = valor.substring(1, valor.length() - 1);  // Quitar las comillas
                    }
                    
                    if (clave.equals("id")) {
                        try {
                            Integer.parseInt(valor); // Verificar si es un número
                        } catch (NumberFormatException e) {
                            errores.add("El valor del id no es un número válido.");
                        }
                }

                    criterios.put(clave, valor);
                } else {
                    errores.add("Par mal formado: " + par);
                }
            }
        } catch (Exception e) {
            errores.add("Error en el parseo: " + e.getMessage());
        }

        // Crear un mapa que incluye criterios válidos y errores
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("criterios", criterios);
        resultado.put("errores", errores);

        return resultado;

    }

    public String extraerContenidoCorchetes(String comando) {
        int startIndex = comando.indexOf("[");
        int endIndex = comando.lastIndexOf("]");

        // Verificar si ambos corchetes están presentes y en orden correcto
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            System.out.println("extracion: "+ comando.substring(startIndex, endIndex + 1 ));
            return comando.substring(startIndex, endIndex + 1);  // Incluye el corchete de cierre
        } else {
            return "Error: Formato incorrecto. No se encontraron ambos corchetes correctamente.";
        }
    }

    //Obtiene los parámetros del comando
    public static String[] extraerParametros(String comando) {
        // Extraer el contenido entre los corchetes
        String parametros = comando.substring(comando.indexOf("[") + 1, comando.indexOf("]"));
        
        // Dividir los parámetros por coma
        String[] parametrosArray = parametros.split(",");

        // Eliminar las comillas dobles de cada parámetro
        for (int i = 0; i < parametrosArray.length; i++) {
            // Eliminar las comillas dobles al principio y al final de cada parámetro
            parametrosArray[i] = parametrosArray[i].trim().replaceAll("^\"|\"$", "");
        }

        return parametrosArray;
    }
    
    public Map<String, Object> verificarCantidadParametros(String[] parametros, int cantidadParametros) {
        Map<String, Object> response = new HashMap<>();  
       
        //verifica si se ha enviado menos parametros de los necesarios
        if (parametros.length < cantidadParametros) {
            System.out.println("Error: Faltan parámetros para crear el usuario. Se esperaban " + cantidadParametros + " pero se recibieron " + parametros.length);
            response.put("subject", "Error en la creación del usuario");
            response.put("body", "Faltan parámetros para crear el usuario. Se esperaban " + cantidadParametros + " pero se recibieron " + parametros.length);
            return response;      
        }
        
        //verifica si se ha enviado mas parametros de los necesarios
        if (parametros.length > cantidadParametros) {
            System.out.println("Error: Hay más parámetros de los que se necesita. Has enviado: " + cantidadParametros + " pero se recibieron " + parametros.length);
            response.put("subject", "Error en la creación del usuario");
            response.put("body", "Hay más parámetros de los que se necesita. Se esperaban " + cantidadParametros + " pero se recibieron " + parametros.length);
            return response;
        }
        
        response.put("body", "Correcto");
        return response;
    }

    
}