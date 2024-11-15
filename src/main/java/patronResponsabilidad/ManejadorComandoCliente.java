/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package patronResponsabilidad;

import java.util.Map;

/**
 *
 * @author C.Vargas
 */
public class ManejadorComandoCliente extends ManejadorComandoAbs{
    
    private String comando ;
    
    public ManejadorComandoCliente(){
        this.comando = "^(LISTARCLIENTE\\[\"\\*\"\\]"
                      + "|INSERTARCLIENTE\\[\".*\"\\])$";
    }
    @Override
    public boolean VerificarProceso(String comando) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
//    @Override
//    public String procesar(String command) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
//    }

    @Override
    public Map<String, Object> procesar(String command) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
