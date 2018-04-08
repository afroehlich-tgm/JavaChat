package client_server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ServerThread extends Thread {

	private Socket clientSocket;
	private List<String> messages;
	Server s;

	public ServerThread(Socket clientSocket, List<String> messages) {
		super();
		this.clientSocket = clientSocket;
		this.messages = messages;
		try {
			s = new Server();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			// Der printWriter schreibt und der reader liest Text aus
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			while (this.clientSocket.isConnected()) {
				//addet clients zur liste
				s.clients.add(out);
				// Zuerst liest es die Message vom Client aus
				String message = in.readLine();
				// Checkt ob eine Message ankam
				if (message != null) {
					// Die Message wird zum thread-safe ArrayList hinzugefuegt
					this.messages.add(message);
				}
				out.println(message);
				sendToAllClients(message);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void sendToAllClients(String message) {
		Iterator it = s.clients.iterator();

		while (it.hasNext()) {
			System.out.println("While");
			PrintWriter writer = (PrintWriter) it.next();
			writer.println(message);
			writer.flush();
		}
	}
}
