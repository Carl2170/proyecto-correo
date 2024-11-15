/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package config;

import Utils.Validador;
import email.Email;
import email.EmailEnvio;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author C.Vargas
 */
public class ObservadorCorreo implements Runnable{
    private POP popClient;
    private int previousMailCount = 12;  // Conteo previo de correos  //ver si se puede obtener dinamicamente
    private Thread procesadorHilo;
    private BlockingQueue<Email> colaCorreos;
  
    public ObservadorCorreo() {
        this.popClient = new POP();
        this.colaCorreos = new LinkedBlockingQueue<>(); // Inicializar la cola
        inicioHiloEnvioCorreo(); 
    }
    
      public void inicioHiloEnvioCorreo() {
        procesadorHilo = new Thread(() -> {
            while (true) {
                try {
                    Email email = colaCorreos.take();
                    System.out.println(email);
                    new EmailEnvio(email).procesoEnvioEmail();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        procesadorHilo.start();
    }

    @Override
    public void run() {
        int contadorObservaciones = 0;

        while (true) {
            try {
                // Conectar al servidor e iniciar sesión
                popClient.conectar();
                popClient.logIn();
                System.out.println("Conexión y autenticación exitosa.");

                // Obtener la cantidad actual de correos
                String stat = popClient.getStat();
                int currentMailCount = Integer.parseInt(stat);
                 
                // Si hay más correos que antes, significa que llegó uno nuevo
                if (currentMailCount > previousMailCount) {
                    int newMailIndex = currentMailCount;  // Índice del nuevo correo
                    
                    
                    // Obtener el remitente y asunto del nuevo correo
                    String from = popClient.getFrom(String.valueOf(newMailIndex));
                    String subject = popClient.getSubject(String.valueOf(newMailIndex));
                    
             
                    // Crear el objeto Email y agregarlo a la cola para procesamiento
                    Email email = new Email(newMailIndex, from, subject, "en proceso");
                    
                    // Encolar el nuevo correo
                    colaCorreos.add(email); 
                   
                    // Actualizar el conteo de correos
                    previousMailCount = currentMailCount;
                }

                // Espera unos segundos antes de la siguiente verificación
                contadorObservaciones++;
                System.err.println("*************Servidor Monitoreado Nro: " + contadorObservaciones + "***********************");

            } catch (IOException e) {
                System.err.println("Error al monitorear correos: " + e.getMessage());
            } finally {
                try {
                    popClient.desconectar();
                    System.out.println("Desconectado del servidor POP.");
                } catch (IOException e) {
                    System.err.println("Error al desconectar: " + e.getMessage());
                }
            }

            try {
                // Espera unos segundos antes de volver a conectarse
                Thread.sleep(10000);  // Espera 10 segundos entre chequeos
            } catch (InterruptedException e) {
                System.err.println("Error en la espera entre conexiones: " + e.getMessage());
                Thread.currentThread().interrupt();  // Restablece el estado de interrupción del hilo
                break;
            }
        }
    }
}