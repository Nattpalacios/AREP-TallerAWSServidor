package edu.escuelaing.arep;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controlador {
	
	public static void main(String[] args) throws IOException {
            try {
                AppServer.inicializar();
            } catch (IOException ex) {
                Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
            }
            escuchar();
	}

    private static void escuchar() throws IOException {
        ServerSocket server = Server.getServer();
        ExecutorService executor = Executors.newCachedThreadPool();
        while(true){
            Socket client = Cliente.client(server);
            executor.execute(new AppServer(client));
        }
    }

}
