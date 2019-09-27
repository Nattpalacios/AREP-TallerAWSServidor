/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.escuelaing.arep;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author estudiante
 */
public class Cliente {
    public static Socket client(ServerSocket server){
        Socket client= null;
            try {
                System.out.println("Listo para recibir ...");
                client = server.accept();                
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
        return client;
    }
    
}
