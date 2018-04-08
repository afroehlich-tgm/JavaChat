package client_server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ServerThread extends Thread {

	private Socket clientSocket;
	private List<String> messages;
	ArrayList<PrintWriter> clients;

	public ServerThread(Socket clientSocket, List<String> messages, ArrayList<PrintWriter> clients) {
		super();
		this.clientSocket = clientSocket;
		this.messages = messages;
		this.clients = clients;

	}

	public void run() {
		try {
			// Der printWriter schreibt und der reader liest Text aus
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			// addet clients zur liste
			this.clients.add(out);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			while (this.clientSocket.isConnected()) {
			
				// Zuerst liest es die Message vom Client aus
				String message = in.readLine();
				// Checkt ob eine Message ankam
				if (message != null) {
					// Die Message wird zum thread-safe ArrayList hinzugefuegt
					sendToAllClients(message);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void sendToAllClients(String message) {
		Iterator it = clients.iterator();

		while (it.hasNext()) {
			System.out.println("While");
			PrintWriter writer = (PrintWriter) it.next();
			writer.println(message);
			writer.flush();
		}
	}
}
