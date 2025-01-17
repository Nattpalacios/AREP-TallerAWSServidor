package edu.escuelaing.arep;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.sound.midi.SysexMessage;

public class AppServer extends Thread{
	
	private static ConcurrentHashMap<String,Handler> hash;
        private Socket client;
	
        public AppServer(Socket client){
            this.client = client;
        }
	public void run(){
		
		
            
            
            BufferedReader in = null;
            try {
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                 in = new BufferedReader(
                         new InputStreamReader(client.getInputStream()));
                 String inputLine, outputLine = "";
                 String op = "";
                 try {
                     while ((inputLine = in.readLine()) != null) {
                         System.out.println("Received: " + inputLine);
                         if(inputLine.contains("GET")) {
                             if(inputLine.contains("/apps/")) {
                                 String path = inputLine.split(" ")[1];
                                 System.err.println(path);
                                 String[] a = path.split("/apps/");
                                 op = "/"+a[1];
                                 String[] elementos = op.split("\\?value=");
                                 System.err.println("op:" + op);
                                 String[] parametros = elementos[1].split("&value=");
                                 for(int i = 0; i < parametros.length; i++) {
                                     System.err.println(parametros[i]);
                                 }
                                 System.err.println(hash.get(elementos[0]));
                                 outputLine = "HTTP/1.1 200 OK\r\n"
                                         + "Content-Type: text/html\r\n"
                                         + "\r\n"
                                         + "<!DOCTYPE html>\n"
                                         + "<html>\n"
                                         + "<head>\n"
                                         + "<meta charset=\"UTF-8\">\n"
                                         + "<title>Title of the document</title>\n"
                                         + "</head>\n"
                                         + "<body>\n"
                                         + "<h1>"
                                         + hash.get(elementos[0]).ejecutar(parametros)
                                         +"</h1>\n"
                                         + "</body>\n"
                                         + "</html>\n";
                             }else if(inputLine.contains("/imagenes/")) {
                                 String path = inputLine.split(" ")[1].split("/")[2];
                                 String formatoFile = path.substring(path.indexOf(".") + 1);
                                 String direccion = System.getProperty("user.dir") + "/imagenes/" + path;
                                 BufferedImage bI = ImageIO.read(new File(direccion));
                                 ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
                                 ImageIO.write(bI, formatoFile, byteArrayOutput);
                                 byte [] listaB = byteArrayOutput.toByteArray();
                                 DataOutputStream salida = new DataOutputStream(client.getOutputStream());
                                 salida.writeBytes("HTTP/1.1 200 OK \r\n");
                                 salida.writeBytes("Content-Type: image/" + formatoFile + "\r\n");
                                 salida.writeBytes("Content-Length: " + listaB.length);
                                 salida.writeBytes("\r\n\r\n");
                                 salida.write(listaB);
                                 salida.close();
                                 out.println(salida.toString());
                             }else if(inputLine.contains("/recursosWeb/")) {
                                 String path = inputLine.split(" ")[1].split("/")[2];
                                 String direccion = System.getProperty("user.dir") + "/RecursosWeb/" + path;
                                 out.println("HTTP/1.1 200 OK\r");
                                 out.println("Content-Type: text/html\r");
                                 out.println("\r\n");
                                 BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(direccion),"UTF8"));
                                 while (br.ready()) {
                                     outputLine += br.readLine();
                                 }
                                 br.close();
                             }
                         }
                         if (!in.ready()) {
                             break;
                         }
                     }
                     
                     out.write(outputLine);
                     
                     out.close();
                     
                     in.close();
                 }catch(Exception e) {
                     e.printStackTrace();
                     outputLine = "HTTP/1.1 200 OK\r\n"
                             + "Content-Type: text/html\r\n"
                             + "\r\n"
                             + "<!DOCTYPE html>\n"
                             + "<html>\n"
                             + "<head>\n"
                             + "<meta charset=\"UTF-8\">\n"
                             + "<title>Title of the document</title>\n"
                             + "</head>\n"
                             + "<body>\n"
                             + "<h1>"
                             + "404 Not Found :("
                             +"</h1>\n"
                             + "</body>\n"
                             + "</html>\n";
                     out.write(outputLine);
                     
                     out.close();
                     
                     in.close();
                 }
                 client.close();
            }catch(IOException ex) {
            	Logger.getLogger(AppServer.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(AppServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }   
		
	}
	
	public static void inicializar() throws IOException {
		String sCarpAct = System.getProperty("user.dir")+"/src/main/java/apps";
		File carpeta = new File(sCarpAct);
		String[] clases = carpeta.list();
		hash = new ConcurrentHashMap<String,Handler>();
		for (int i = 0; i < clases.length; i++) {
			String ruta = "apps." + clases[i].substring(0,clases[i].indexOf("."));
			cargar(ruta);
		}
	}
	
	public static void cargar(String classpath) {
		try {
			Class c = Class.forName(classpath);
			Method[] metodos = c.getDeclaredMethods();
			for(Method m : metodos) {
				if (m.getAnnotation(Web.class) != null) {
					System.err.println(m.getAnnotation(Web.class).value());
					hash.put(m.getAnnotation(Web.class).value(), new URLHandler(m));
				}		
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
