/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package config;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import resources.Constants;

/**
 *
 * @author C.Vargas
 */
public class POP {
        private String COMAND = "";
        private Socket Conexion;
        private BufferedReader Entrada;
        private DataOutputStream Salida;
        private static final String SALTO = "\n";

        
        public POP(){
            this.Conexion = null;
            this.Entrada = null;
            this.Salida = null;
        }
        
        public void conectar() throws IOException {
           this.Conexion = new Socket(Constants.MAIL_SERVER_HOST, Constants.MAIL_PORT_POP);
           this.Entrada = new BufferedReader(new InputStreamReader(Conexion.getInputStream()));
           this.Salida = new DataOutputStream(Conexion.getOutputStream());
       System.out.println("S:" + this.Entrada.readLine());

        }
        
        public void desconectar() throws IOException {
            this.Conexion.close();
            this.Entrada.close();
            this.Salida.close();
        }
        
        public void logIn() throws IOException {
            COMAND = "USER " + Constants.MAIL_USER + SALTO;
            this.Salida.writeBytes(COMAND);
            this.Entrada.readLine();
            COMAND = "PASS " + Constants.MAIL_PASSWORD + SALTO;
            this.Salida.writeBytes(COMAND);
           System.out.println("S:" + this.Entrada.readLine());
        }
        
        public String getList() throws IOException {
            COMAND = "LIST" + SALTO;
            this.Salida.writeBytes(COMAND);
            return getData(this.Entrada);
    }  
        
        static protected String getData(BufferedReader buffer) throws IOException {
            String Data = "";
            String Line = "";
            while ((Line = buffer.readLine()) != null) {

                if (Line.equals(".")) {
                    break;
                }

                if ((Line.length() > 0) && (Line.charAt(0) == '.')) {
                    Line = Line.substring(1);
                }
                Data = Data + Line + SALTO;
            }
            return Data;
        }
        
        public String getSubject(String Mail) throws IOException {
            String Line = "";
            String line2 = "";
            String paraf = "";
            COMAND = "RETR " + Mail + SALTO;
            this.Salida.writeBytes(COMAND);    
            while ((Line = this.Entrada.readLine()) != null) {

                if (Line.startsWith("Subject:") || Line.startsWith("SUBJECT:") || Line.startsWith("subject:")) {
                    break;
                }
            }        
            Line = Line.substring(8);

            while ((line2 = this.Entrada.readLine()) != null) {
                if (line2.equals(".") || line2.startsWith("To:") || line2.startsWith("Thread-Topic:") || line2.startsWith("MIME-Version:") || line2.startsWith("Thread-Topic:") || line2.startsWith("Message-ID:")) {
                    break;
                } else {
                    paraf = paraf + line2;
                }
            }
    //        System.out.println(paraf);
    //        if (paraf != "") {
    //            Line = Line + paraf;
    //            //line2 = line2 + paraf;
    //        }
            Line = Line.trim();
            if (!line2.equals(".")) {
                getData(Entrada);
            }
            return Line;
        }
        
        public String getStat() {
           /* String line = "";
            try {
                this.Salida.writeBytes("STAT" + SALTO);
                line = this.Entrada.readLine();

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            if (!"".equals(line)) {
                line = line.substring(4);
                int i = 1;
                while (line.charAt(i) != ' ') {
                    i++;
                }
                line = line.substring(0, i);
            }
            return line; */
           
            String line = "";
            try {
                this.Salida.writeBytes("STAT" + SALTO);
                line = this.Entrada.readLine();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            if (!"".equals(line) && line.startsWith("+OK")) {
                // Ignorar el prefijo "+OK " y tomar el resto de la línea
                line = line.substring(4).trim(); // quita "+OK " y cualquier espacio adicional

                int espacioIndex = line.indexOf(' ');

                if (espacioIndex != -1) {
                    // Extraer solo el primer número (cantidad de mensajes)
                    line = line.substring(0, espacioIndex);
                }
            } else {
                System.out.println("Respuesta inesperada: " + line);
                return "";
            }

            return line; // devuelve el número como String
             }
        
        public String getFrom(String Mail) throws IOException {
            String Line = "";
            COMAND = "RETR " + Mail + SALTO;
            this.Salida.writeBytes(COMAND);
            while ((Line = this.Entrada.readLine()) != null) {

                if (Line.indexOf("From:") == 0) {
                    break;
                }
            }
            Line = Line.substring(6);
            if (Line.contains("<")) {
                Line = Line.substring(Line.indexOf("<") + 1, Line.indexOf(">"));
            }
            getData(Entrada);//vaciar buffer
            return Line;
        }

        public String getMail(String mail) throws IOException {
            COMAND = "RETR " + mail + SALTO;
            this.Salida.writeBytes(COMAND);
            return getData(this.Entrada);
        }

        public void delete(String j) throws IOException {
            this.Salida.writeBytes("DELE " + j + SALTO);
            System.out.println("S:" + this.Entrada.readLine());
        }
        
        
        public void logOut() throws IOException {
            COMAND = "quit" + SALTO;
            this.Salida.writeBytes(COMAND);
            System.out.println("S:" + this.Entrada.readLine());
        }
}
