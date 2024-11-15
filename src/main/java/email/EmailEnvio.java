/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package email;

import Utils.Constants;
import Utils.Validador;
import config.SMTP;
import java.util.List;
import java.util.Map;
import patronResponsabilidad.ManejadorComandoAbs;
import patronResponsabilidad.ManejadorComandoCliente;
import patronResponsabilidad.ManejadorComandoProducto;
import patronResponsabilidad.ManejadorComandoReseña;
import patronResponsabilidad.ManejadorComandoUsuario;

/**
 *
 * @author C.Vargas
 */
public class EmailEnvio {
    private Email email;
    private Validador validador;
    private SMTP smtp;
    private ManejadorComandoAbs cadena;
 
    
    public EmailEnvio(Email email) {
        this.email = email;
        this.validador= new Validador(); 
        this.smtp = new SMTP(Constants.MAIL_SERVER_HOST, Constants.MAIL_USERMAIL);
        this.cadena = crearCadenaManejadores();

    }
    
    //enlazar todos los manejadores
    private ManejadorComandoAbs crearCadenaManejadores() {
        ManejadorComandoAbs usuario = new ManejadorComandoUsuario();
        ManejadorComandoAbs cliente = new ManejadorComandoCliente();
        ManejadorComandoAbs producto = new ManejadorComandoProducto();
        ManejadorComandoAbs reseña = new ManejadorComandoReseña();

        // Configura la cadena: principal -> usuario
        usuario.setManejadorComando(cliente);
        cliente.setManejadorComando(producto);
        producto.setManejadorComando(reseña);
        
        return usuario;  // Retorna el manejador inicial de la cadena
    }

    
    public void procesoEnvioEmail() {
                    // Mostrar en consola la llegada del nuevo correo
                    System.out.println("Nuevo correo recibido:");
                    System.out.println("De: " + email.getReceptor());
                    System.out.println("Asunto: " + email.getAsunto());
                    
                    // Validador.ValidacionResultado validationResult = validador.validateSubject(email.getAsunto());
                    Validador.ValidacionResultado validationResult = validador.VerificarProceso(email.getAsunto());
                    
                     if (validationResult.isValid()) {
                         
                        // Comando válido: ejecutar acción asociada
                        System.out.println("Comando válido: " + email.getAsunto());
                       
                     //   String resultado = cadena.procesar(email.getAsunto());
                        

                        //Transferencia del comando a la cadena de responsabilidad
                        Map<String, Object> resultado = cadena.procesar(email.getAsunto());
                         
                        String asunto = (String) resultado.get("subject");
                        boolean esListado = (boolean) resultado.getOrDefault("esListado", false); // Verificar si es listado 
                        
                        if(esListado){
                        List<String> columnas = (List<String>)resultado.get("columnas");
                        List<Map<String, String>> usuariosFormateados = (List<Map<String, String>>) resultado.get("body");
                            
                       // smtp.enviarCorreo( asunto,"",email.getReceptor(),esListado,columnas, usuariosFormateados);
                            
                        }else{    
                         String cuerpo = (String) resultado.get("body");   
                        // smtp.enviarCorreo( asunto,cuerpo, email.getReceptor(),esListado,null,null);   
                            System.out.println(asunto);
                            System.out.println(cuerpo.toString());
                        }
                    } else {
                        // Comando inválido: enviar mensaje de error al remitente
                        System.out.println("Enviando correo de error a " + email.getReceptor());
                         smtp.enviarCorreo("Error en el comando", 
                                           "Has enviado un comando incorrecto. Revisa el comando, por favor.",
                                            email.getReceptor(),
                                            false,
                                            null,
                                            null);
                    }
 
    }
    
    
/*CASOS DE USO:(APROBADOS)
CU1. Gestión de usuarios(Administrativos, Cliente).
CU2. Gestión de Productos.
CU3. Gestión de Locaciones.
CU4. Gestión de Menus.
CU5. Gestión de Promociones.
CU6. Gestión de Reclamos.
CU7. Gestión de Pagos.
CU8. Reportes y Estadísticas.*/
}
