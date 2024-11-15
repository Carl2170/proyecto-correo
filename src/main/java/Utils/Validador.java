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
                                                           + "ELIMINARCLIENTE)\\[.*\\]$";  // indica si hay parametros
    
    public ValidacionResultado VerificarProceso(String comando) {
        // Valida si el comando cumple con la estructura general
        if (comando.matches(estructuraGeneralComando)) {
             return new ValidacionResultado(true, null);
        } else {
            System.out.println("Error: La estructura del comando es incorrecta.");
                 return new ValidacionResultado(false, "Error: La estructura del comando es incorrecta.");
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
