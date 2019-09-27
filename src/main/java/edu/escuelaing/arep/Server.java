package edu.escuelaing.arep;

import java.io.IOException;
import java.net.ServerSocket;


public class Server {

	/*
	 * To change this license header, choose License Headers in Project Properties.
	 * To change this template file, choose Tools | Templates
	 * and open the template in the editor.
	 */
	
    static ServerSocket getServer() throws IOException {
        int port;
        if (System.getenv("PORT") != null) {
            port = Integer.parseInt(System.getenv("PORT"));
        }else{
            port = 4567; //returns default port if heroku-port isn't set (i.e.on localhost)
        }
        ServerSocket server = null;
        server = new ServerSocket(port);
        return server;
    }
}
