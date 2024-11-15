/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.grupo13sa.tecnoweb;

import config.ObservadorCorreo;
import config.POP;
import java.io.IOException;

/**
 *
 * @author C.Vargas
 */
public class Grupo13saTecnoweb {
   public static String extraerContenidoCorchetes(String comando) {
    int startIndex = comando.indexOf("[");
    int endIndex = comando.lastIndexOf("]");

    // Verificar si ambos corchetes están presentes y en orden correcto
    if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
        return comando.substring(startIndex, endIndex + 1);  // Incluye el corchete de cierre
    } else {
        return "Error: Formato incorrecto. No se encontraron ambos corchetes correctamente.";
    }
}
   
   public static void main(String[] args) throws IOException {
  

        ObservadorCorreo observador = new ObservadorCorreo(); 
        Thread thread = new Thread(observador);
        thread.start();   
            
//
//    String comando = "ELIMINARUSUARIO[id=8]";
//    String resultado = extraerContenidoCorchetes(comando);
//    System.out.println("Contenido entre corchetes: " + resultado);



//ver mensajes 
//       POP clie = new POP();
//        clie.conectar();
//        clie.logIn();
//        System.out.println("Conexión y autenticación exitosa.");
//        System.out.println(clie.getList());  
//        System.out.println();
//        for (int i = 6; i <= 14; i++) {
//            clie.delete(Integer.toString(i));
//        }
//        System.out.println(clie.getList()); 
//        clie.logOut();
//        clie.desconectar();

//        clie.conectar();
//        clie.logIn();
//        System.out.println("Conexión y autenticación exitosa.");
//        System.out.println(clie.getList()); 
//        clie.delete("6");
//          clie.delete("7");
//            clie.delete("8");
//            clie.delete("9");
//        System.out.println(clie.getList()); 
//
//        clie.logOut();
//        clie.desconectar();

    }
}
