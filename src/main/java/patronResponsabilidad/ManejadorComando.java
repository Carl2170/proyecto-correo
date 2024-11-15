/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package patronResponsabilidad;

import java.util.Map;

/**
 *
 * @author C.Vargas
 */
public interface ManejadorComando {
    
    boolean VerificarProceso(String comando);
    
//    Map<String, String>  manejador(String comando);
      Map<String, Object> procesar(String comando);

    
}
