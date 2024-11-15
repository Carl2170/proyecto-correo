/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

import java.util.ArrayList;

/**
 *
 * @author C.Vargas
 */
public class Validador {
    
   /* private static final String estructuraGeneralComando
            = 
            "^(LISTARUSUARIO\\w+\\[\"\\*\"\\]" + 
            "|INSERTARUSUARIO\\w+\\[\".*?\"\\]" + 
            "|ACTUALIZARUSUARIO\\w+\\[\".*?\"\\]" + 
            "|ELIMINARUSUARIO\\w+\\[\".*?\"\\])$";
    
    */
//    private static final String estructuraGeneralComando = "^(LISTAR|CREAR|MODIFICAR|ELIMINAR)"
//                                                          + "(USUARIO"
//                                                          + "|CLIENTE"
//                                                          + "|PRODUCTO"
//                                                          + "|MENU"
//                                                          + "|CATEGORIA)"
//                                                          + "\\[.*\\]$";  // indica si hay parametros

   
    private static final String estructuraGeneralComando = "^(LISTARUSUARIO|"
                                                           + "CREARUSUARIO|"
                                                           + "EDITARUSUARIO|"
                                                           + "ELIMINARUSUARIO|"
                                                           + "LISTARUSUARIOS|"
                                                           + "ELIMINARUSUARIOS|"
                                                           + "LISTARCLIENTE|"
                                                           + "CREARCLIENTE|"
                                                           + "EDITARCLIENTE|"
                                                           + "ELIMINARCLIENTE)"
                                                           //+"\\\\[[^\\\\s\\\\[\\\\],]+(\\\\s*,\\\\s*[^\\\\s\\\\[\\\\],]+)*\\\\]$";
                                                          // + "\\\\[[^\\\\s\\\\[\\\\]]+\\\\]$";  // indica si hay parametros
                                                            + "\\[.*\\]$";  // indica si hay parametros
    public ValidacionResultado VerificarProceso(String comando) {
        
        
        // Valida si el comando cumple con la estructura general
        if (comando.matches(estructuraGeneralComando)) {
             return new ValidacionResultado(true, null);
        } else {
            System.out.println("Error comando general: La estructura del comando es incorrecta.");
                 return new ValidacionResultado(false, "Error: La estructura del comando es incorrecta.");
        }
    }
    
    public ValidacionResultado VerificarSaltosLinea(String comando) {
    // Dividir el comando en líneas
    String[] lineas = comando.split("\\r?\\n");

    // Verificar si hay saltos de línea
    if (lineas.length > 1) {
        // Recorrer cada línea y verificar que no esté vacía y que cumpla con el formato del comando
        for (String linea : lineas) {
            // Quitar espacios en blanco al inicio y fin de cada línea
            String lineaSinEspacios = linea.trim();

            // Si alguna línea está vacía o no cumple con el formato, retornar error
            if (lineaSinEspacios.isEmpty()) {
                System.out.println("Error: Una de las líneas del comando está vacía.");
                return new ValidacionResultado(false, "Error: Una de las líneas del comando está vacía.");
            }
        }

        // Combinar las líneas en una sola cadena sin saltos de línea para validación final
        String comandoSinSaltos = String.join("", lineas);

        // Verificar el comando completo sin saltos de línea
        if (comandoSinSaltos.matches(estructuraGeneralComando)) {
            return new ValidacionResultado(true, null);
        } else {
            System.out.println("Error Saltos: La estructura del comando es incorrecta.");
            return new ValidacionResultado(false, "Error: La estructura del comando es incorrecta.");
        }
    } else {
        // No hay saltos de línea, verificar el comando completo
        if (comando.matches(estructuraGeneralComando)) {
            return new ValidacionResultado(true, null);
        } else {
            System.out.println("Error: La estructura del comando es incorrecta.");
            return new ValidacionResultado(false, "Error: La estructura del comando es incorrecta.");
        }
    }
}

    
    // Clase auxiliar para devolver el resultado de validación
    public static class ValidacionResultado {
        private final boolean isValid;
        private final String errorMessage;

        public ValidacionResultado(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return isValid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
