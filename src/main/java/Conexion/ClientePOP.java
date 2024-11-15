/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Conexion;
import Utils.Constants;
import Conexion.ClienteSMTP;
import Conexion.ClienteSMTP2;
import java.io.*;
import java.net.*;


/**
 *
 * @author C.Vargas
 */
public class ClientePOP {
       private static final int PORT = 110;
       String line;
       String comando="";
       ClienteSMTP2 c= new ClienteSMTP2();

    public ClientePOP() {
        try {
            Socket skCliente = new Socket(Constants.MAIL_SERVER_HOST, PORT);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(skCliente.getInputStream()));
            DataOutputStream salida = new DataOutputStream (skCliente.getOutputStream());
            System.out.println(" C : conectado a < "+ Constants.MAIL_SERVER_HOST + " >");
            System.out.println(" S : "+ entrada.readLine());
            
            comando = "USER " + Constants.MAIL_USER + "\r\n";
            System.out.print(" C : "+ comando);
            salida.writeBytes(comando);
            System.out.println(" S : "+ entrada.readLine());
            
            comando = "PASS " + Constants.MAIL_PASSWORD + "\r\n";
            System.out.print(" C : "+ comando);
            salida.writeBytes(comando);
            System.out.println(" S : "+ entrada.readLine());
            
            comando = "STAT \r\n";
            System.out.print(" C : "+ comando);
            salida.writeBytes(comando);
            System.out.println(" S : "+ entrada.readLine());

            comando = "LIST \r\n";
            System.out.print(" C : "+ comando);
            salida.writeBytes(comando);
            System.out.println(" S : "+ getMultiline2(entrada));     
            
            int numeroCorreo = 2; // Ejemplo: queremos obtener el correo con número 1
            String contenidoCorreo = obtenerCorreo(entrada, salida, numeroCorreo);
            
            // Si el correo fue obtenido exitosamente
            if (!contenidoCorreo.isEmpty()) {
                c.enviarCorreo(contenidoCorreo);
                // Enviar la respuesta
            } else {
                System.out.println("No se pudo obtener el correo.");
            }

            comando="QUIT \r\n";
            System.out.print("C : "+comando);
            salida.writeBytes( comando );               
            System.out.println("S : "+entrada.readLine());

            skCliente.close();
            System.out.println(" C : Desconectado del < " + Constants.MAIL_SERVER_HOST + " >"); 

        }catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());

        }
    }
    
   
    
    static protected String obtenerCorreo(BufferedReader in, DataOutputStream out, int numeroMensaje) throws IOException {
   // Enviar el comando RETR para obtener un correo específico
    String comando = "RETR " + numeroMensaje + "\r\n";
    System.out.print(" C : " + comando);
    out.writeBytes(comando); // Enviamos el comando RETR
    
    // Obtenemos la respuesta completa (encabezado y cuerpo del correo)
    String respuesta = getMultiline2(in);
    
    // Filtramos el contenido para obtener solo desde la línea que contiene "SUBJECT"
    String[] lineas = respuesta.split("\n");
    StringBuilder contenidoFiltrado = new StringBuilder();
    
    boolean encontradoSubject = false;
    for (String linea : lineas) {
        // Buscamos la línea que comienza con "SUBJECT" y agregamos desde allí en adelante
        if (linea.startsWith("SUBJECT")) {
            encontradoSubject = true;
        }
        if (encontradoSubject) {
            contenidoFiltrado.append(linea).append("\n");
        }
    }
    
    // Imprimimos el contenido filtrado
    String contenido = contenidoFiltrado.toString();
    
    return contenido;
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
    
    static protected String getMultiline2(BufferedReader in) throws IOException {
    StringBuilder lines = new StringBuilder();
    while (true) {
        String line = in.readLine();
        if (line == null) {
            // El servidor cerró la conexión inesperadamente
            throw new IOException(" S : Server unawares closed the connection.");
        }
        if (line.equals(".")) {
            // Final de la respuesta
            break;
        }
        lines.append(line).append("\n"); // Agrega la línea al resultado
    }
    return lines.toString();
}

    public static void main(String[] args) {
        ClientePOP cliente = new ClientePOP();
    }
    
}
