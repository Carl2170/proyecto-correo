/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package config;

import Utils.Constants;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 *
 * @author C.Vargas
 */
public class SMTP {
    
     private static final int PORT = 25; // SMTP 25
     private String mailServerHost;
     private String mailUserMail;
        
        public SMTP(String mailServerHost, String mailUserMail){
            this.mailServerHost = mailServerHost;
            this.mailUserMail = mailUserMail;
        }
        
        
    public String prepararPlantillaHTML(String title, String message, boolean esListado, List<String> columnas, List<Map<String, String>> usuariosFormateados) throws IOException {
        String htmlTemplate;

           // Cargar la plantilla HTML según el tipo (Listado o normal)
           if (esListado) {
               htmlTemplate = new String(Files.readAllBytes(Paths.get("C:\\\\Users\\\\C.Vargas\\\\Documents\\\\NetBeansProjects\\\\grupo13sa-tecnoweb\\\\src\\\\main\\\\java\\\\resources\\\\plantilla_listado.html")), "UTF-8");
           } else {
               htmlTemplate = new String(Files.readAllBytes(Paths.get("C:\\Users\\C.Vargas\\Documents\\NetBeansProjects\\grupo13sa-tecnoweb\\src\\main\\java\\resources\\plantilla.html")), "UTF-8");

           }

            // Reemplazar el título y el mensaje en la plantilla
             htmlTemplate = htmlTemplate.replace("{{title}}", title);
             htmlTemplate = htmlTemplate.replace("{{message}}", message);

             if (esListado && columnas != null && usuariosFormateados != null) {
                 // Generar los encabezados de la tabla (columnas)
                 StringBuilder columnasHTML = new StringBuilder();
                 for (String columna : columnas) {
                     columnasHTML.append("<th>").append(columna).append("</th>");
                 }

                 // Generar las filas de la tabla (usuarios)
                 StringBuilder filasHTML = new StringBuilder();
                 for (Map<String, String> registro : usuariosFormateados) {
                     filasHTML.append("<tr>");
                     for (String columna : columnas) {
                         filasHTML.append("<td>").append(registro.get(columna)).append("</td>");
                     }
                     filasHTML.append("</tr>");
                 }

                 // Reemplazar las columnas y filas en la plantilla HTML
                 htmlTemplate = htmlTemplate.replace("{{columnas}}", columnasHTML.toString());
                 htmlTemplate = htmlTemplate.replace("{{datos}}", filasHTML.toString());
             }
             
             System.out.println("************************");
             System.out.println(htmlTemplate);
             System.out.println("************************");
             return htmlTemplate;
             
             
    }
        
        
        public void enviarCorreo(String asunto, String contenido, String correoReceptor, boolean esListado,List<String> columnas,  List<Map<String, String>> registros) {
        // Estableciendo variables
        BufferedReader entrada;
        DataOutputStream salida;
        String comando;
        String contenidoHTML ="";

        try {
            if(esListado){
             contenidoHTML = prepararPlantillaHTML(asunto, contenido, esListado, columnas, registros);

            }else{
            contenidoHTML = prepararPlantillaHTML(asunto, contenido, esListado, null, null);
            }
                  

            Socket skCliente = new Socket(this.mailServerHost, PORT);
            entrada = new BufferedReader(new InputStreamReader(skCliente.getInputStream()));
            salida = new DataOutputStream(skCliente.getOutputStream());

        // Conectar al servidor SMTP
        System.out.println("C: Conectando a " + this.mailServerHost);
        System.out.println("S: " + entrada.readLine()); // Respuesta de bienvenida del servidor

        // Comando HELO
        comando = "HELO " + this.mailServerHost + "\r\n";
        salida.writeBytes(comando);
        System.out.println("C: " + comando);
        System.out.println("S: " + getMultiline(entrada));

        // Comando MAIL FROM
        comando = "MAIL FROM: <" + this.mailUserMail + ">\r\n";
        salida.writeBytes(comando);
        System.out.println("C: " + comando);
        System.out.println("S: " + entrada.readLine());

        // Comando RCPT TO
        comando = "RCPT TO: <" + correoReceptor + ">\r\n";
        salida.writeBytes(comando);
        System.out.println("C: " + comando);
        System.out.println("S: " + entrada.readLine());

        // Comando DATA
        comando = "DATA\r\n";
        salida.writeBytes(comando);
        System.out.println("C: " + comando);
        System.out.println("S: " + entrada.readLine());

        // Encabezados y cuerpo del mensaje
//        comando = "From: <" + this.mailUserMail + ">\r\n" +
//                  "To: <" + correoReceptor + ">\r\n" +
//                  "Subject: " + asunto + "\r\n" +
//                  "\r\n" +  // Línea en blanco para separar encabezados y cuerpo
//                   contenido  + "\r\n" +
//                  ".\r\n"; // Línea de punto final para indicar el fin del mensaje
         comando = "From: <" + this.mailUserMail + ">\r\n" +
                      "To: <" + correoReceptor + ">\r\n" +
                      "Subject: " + asunto + "\r\n" +
                      "Content-Type: text/html; charset=UTF-8\r\n" +
                      "\r\n" + contenidoHTML + "\r\n" +
                      ".\r\n";  // Fin del mensaje
        
        salida.writeBytes(comando);
        System.out.println("C: " + comando);
        System.out.println("S: " + entrada.readLine());

        // Comando QUIT
        comando = "QUIT\r\n";
        salida.writeBytes(comando);
        System.out.println("C: " + comando);
        System.out.println("S: " + entrada.readLine());

        skCliente.close();
        System.out.println("C: Desconectado del servidor SMTP");
        } catch (IOException ex) {
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
}
