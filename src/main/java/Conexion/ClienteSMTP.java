/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Conexion;
import static Conexion.ClientePOP.getMultiline2;
import static Conexion.ClientePOP.obtenerCorreo;
import Utils.Constants;
import java.io.*;
import java.net.*;

/**
 *
 * @author C.Vargas
 */
public class ClienteSMTP {
    private static final int PORT = 25;
    String line;
    String comando="";
      
    public ClienteSMTP() {

        try {
            Socket skCliente = new Socket(Constants.MAIL_SERVER_HOST, PORT);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(skCliente.getInputStream()));
            DataOutputStream salida = new DataOutputStream (skCliente.getOutputStream());
            System.out.println(" C : conectado a < "+ Constants.MAIL_SERVER_HOST + " >");
            System.out.println(" S : "+ entrada.readLine());

            comando="HELO "+Constants.MAIL_SERVER_HOST+" \r\n";
            System.out.print("C : "+comando);
            salida.writeBytes( comando );   
            System.out.println("S : "+getMultiline(entrada));

            comando = "MAIL FROM: "+ Constants.MAIL_CLIENT + "\r\n";
            System.out.print(" C : " + comando);
            salida.writeBytes(comando);
            System.out.println(" S : "+ entrada.readLine());

            comando = "RCPT TO: " + Constants.MAIL_USERMAIL + "\r\n";
            System.out.print(" C : " + comando);
            salida.writeBytes(comando);
            System.out.println(" S : "+ entrada.readLine());
    //        
            comando = "DATA\r\n";
            System.out.print(" C : "+ comando);
            salida.writeBytes(comando);
    //        System.out.println(" S : "+ entrada.readLine());
            System.out.println("S : "+getMultiline(entrada));

            comando="SUBJECT: ENVIO DE MENSAJE2 \r\n"+
                    "Probando\r\n"+
                    "mensaje\r\n"
                    +".\r\n";

            System.out.print("C : "+comando);
            salida.writeBytes( comando );               
           //System.out.println("S : "+entrada.readLine());       
            System.out.println("S : "+ getMultiline(entrada));       


            comando="QUIT\r\n";
            System.out.print("C : "+comando);
            salida.writeBytes( comando );               
            System.out.println("S : "+entrada.readLine());

            skCliente.close();
            System.out.println(" C : Desconectado del < " + Constants.MAIL_SERVER_HOST + " >");
        }catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());

        }
    }
    
    public void respuestaCorreo(String mensaje) {
          try {
            Socket skCliente = new Socket(Constants.MAIL_SERVER_HOST, PORT);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(skCliente.getInputStream()));
            DataOutputStream salida = new DataOutputStream (skCliente.getOutputStream());
           // System.out.println(" C : conectado a < "+ Constants.MAIL_SERVER_HOST + " >");
           // System.out.println(" S : "+ entrada.readLine());
            
             comando="HELO "+Constants.MAIL_SERVER_HOST+" \r\n";
            //System.out.print("C : "+comando);
            salida.writeBytes( comando );   
            //System.out.println("S : "+getMultiline(entrada));

            comando = "MAIL FROM: "+ Constants.MAIL_USERMAIL+ "\r\n";
           // System.out.print(" C : " + comando);
            salida.writeBytes(comando);
           // System.out.println(" S : "+ entrada.readLine());

            comando = "RCPT TO: " + Constants.MAIL_CLIENT + "\r\n";
            //System.out.print(" C : " + comando);
            salida.writeBytes(comando);
          //  System.out.println(" S : "+ entrada.readLine());
    //        
            comando = "DATA\r\n";
          //  System.out.print(" C : "+ comando);
            salida.writeBytes(comando);
    //        System.out.println(" S : "+ entrada.readLine());
            System.out.println("S : "+getMultiline(entrada));

            comando="SUBJECT: ERESPUESTA DE SERVIDOR\r\n"+
                     "\r\n" +  // Línea en blanco entre encabezado y cuerpo
                     " Respuesta.\r\n"+
                     ".\r\n";
            



            System.out.print("C : "+comando);
            salida.writeBytes( comando );               
           //System.out.println("S : "+entrada.readLine());       
            System.out.println("S : "+ getMultiline(entrada));       


            comando="QUIT\r\n";
            System.out.print("C : "+comando);
            salida.writeBytes( comando );               
            System.out.println("S : "+entrada.readLine());

            skCliente.close();
            System.out.println(" C : Desconectado del < " + Constants.MAIL_SERVER_HOST + " >"); 

        }catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());

        }
    }
    
    static protected String getMultiline(BufferedReader in) throws IOException{
        String lines = "";
        while (true){
            String line = in.readLine();
            if (line == null){
               // Server closed connection
               throw new IOException(" S : Server unawares closed the connection.");
            }
            if (line.charAt(3)==' '){
                lines=lines+"\n"+line;
                // No more lines in the server response
                break;
            }           
            // Add read line to the list of lines
            lines=lines+"\n"+line;
        }       
        return lines;
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
          ClienteSMTP cliente = new ClienteSMTP();
    }
    
}
