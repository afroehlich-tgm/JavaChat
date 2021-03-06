package client_server;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import gui.MainGui;

import java.util.Collections;

public class Server {
    private ServerSocket serverSocket;
    private List<String> messages;
	ArrayList <PrintWriter> clients;
    
    public static void main(String[] args) throws Exception {
		Server s = new Server();
		s.startServer();
	}
   
    public Server() throws Exception {
        // Um Socketverbindung zu erstellen
        this.serverSocket = new ServerSocket(5050, 8);
        clients = new ArrayList<PrintWriter>();
        // Synchronized ArrayList wird hier erstellt
        // Synchronized bedeutet einfach Thread-Safe
        this.messages = Collections.synchronizedList(new ArrayList<String>());
    }

    public void startServer() {
        while (this.serverSocket.isBound()) {
            try {
                // Akzeptiert einen Client
            	Socket clientSocket = serverSocket.accept();
            	serverSocket.setReuseAddress(true);
                // Erstellt zu einem Client einen neuen zustšndigen Thread
                new ServerThread(clientSocket, this.messages, clients).start();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
    
    
}
