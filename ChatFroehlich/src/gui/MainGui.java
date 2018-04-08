package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MainGui extends Thread {

	public static void main(String[] args) {
		MainGui client = new MainGui();
		client.previewWindow();
	}

	private JFrame newFrame = new JFrame("Java Chat Froehlich");
	private JButton sendMessage;
	private JTextField messageBox;
	private JTextArea chatBox, clientList;
	private JFrame preFrame;
	private JTextField usernameChooser;
	private PrintWriter _writer;
	private BufferedReader _reader;
	private Socket mySocket;
	private ArrayList<String> messages;
	private ArrayList<String> names;


	public MainGui() {
		this.messages = new ArrayList<String>();
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		JPanel southPanel = new JPanel();
		JPanel eastPanel = new JPanel();
		southPanel.setBackground(Color.BLUE);
		southPanel.setLayout(new GridBagLayout());
		eastPanel.setBackground(Color.BLUE);
		eastPanel.setLayout(new BorderLayout());
		
		//TEXTAREA FÜR ALLE CLIENTS
		clientList = new JTextArea();
		clientList.setEditable(false);
		clientList.setFont(new Font("Serif", Font.PLAIN, 15));
		clientList.setLineWrap(true);

		eastPanel.add(clientList, BorderLayout.LINE_END);
		messageBox = new JTextField(30);
		messageBox.requestFocusInWindow();
		sendMessage = new JButton("Send Message");
		sendMessage.addActionListener(new sendMessageButtonListener());


		chatBox = new JTextArea();
		chatBox.setEditable(false);
		chatBox.setFont(new Font("Serif", Font.PLAIN, 15));
		chatBox.setLineWrap(true);

		


		mainPanel.add(new JScrollPane(chatBox), BorderLayout.CENTER);

		GridBagConstraints left = new GridBagConstraints();
		left.anchor = GridBagConstraints.LINE_START;
		left.fill = GridBagConstraints.HORIZONTAL;
		left.weightx = 512.0D;
		left.weighty = 1.0D;

		GridBagConstraints right = new GridBagConstraints();
		right.insets = new Insets(0, 10, 0, 0);
		right.anchor = GridBagConstraints.LINE_END;
		right.fill = GridBagConstraints.NONE;
		right.weightx = 1.0D;
		right.weighty = 1.0D;

		southPanel.add(messageBox, left);
		southPanel.add(sendMessage, right);

		mainPanel.add(BorderLayout.SOUTH, southPanel);
		mainPanel.add(BorderLayout.EAST, eastPanel);
		newFrame.add(mainPanel);
		newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		newFrame.setSize(500, 300);
		newFrame.setVisible(true);
	}

	public void previewWindow() {
		this.preFrame = new JFrame("Java Chat Froehlich");
		this.usernameChooser = new JTextField(255);
		JLabel chooseUsernameLabel = new JLabel("Pick a username:");
		JButton enterServer = new JButton("Enter Chat Server");
		JPanel prePanel = new JPanel(new GridBagLayout());

		GridBagConstraints preRight = new GridBagConstraints();
		preRight.weightx = 1;
		preRight.insets = new Insets(0, 0, 0, 10);
		preRight.anchor = GridBagConstraints.EAST;
		GridBagConstraints preLeft = new GridBagConstraints();
		preLeft.anchor = GridBagConstraints.WEST;
		preLeft.insets = new Insets(0, 10, 0, 10);
		enterServer.addActionListener(new enterServerButtonListener());
		preRight.fill = GridBagConstraints.HORIZONTAL;
		preRight.gridwidth = GridBagConstraints.REMAINDER;

		prePanel.add(chooseUsernameLabel, preLeft);
		prePanel.add(usernameChooser, preRight);
		preFrame.add(BorderLayout.CENTER, prePanel);
		preFrame.add(BorderLayout.SOUTH, enterServer);
		preFrame.setSize(300, 300);
		preFrame.setVisible(true);

		// Damit alles freigegeben wird beim beenden
		preFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				try {
					if (mySocket != null)
						mySocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		});
	}

	enterServerButtonListener user = new enterServerButtonListener();

	class sendMessageButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if (messageBox.getText().length() < 1) {
				System.out.println("Invalid message");
			} else if (messageBox.getText().equals(".clear")) {
				chatBox.setText("Cleared all messages\n");
				messageBox.setText("");
			}
			if (messageBox.getText().length() >= 1) {
				_writer.println("<" + user.userName() + ">:  " + messageBox.getText());
				messageBox.setText("");
			}
			messageBox.requestFocusInWindow();
		}
	}


	class enterServerButtonListener implements ActionListener {
		public String userName() {
			String username = usernameChooser.getText();
			return username;
		}
		

		public void actionPerformed(ActionEvent event) {
			try {
				System.out.println(userName());
				if (userName().length() < 1) {
					System.out.println("No!");
				} else {
					Socket socket = new Socket(InetAddress.getByName(null), 5050);
					preFrame.setVisible(false);
					mySocket = socket;
					new ConnectionHandlerThread().start();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class ConnectionHandlerThread extends Thread {
		public ConnectionHandlerThread() {
			super();
		}

		@SuppressWarnings("unchecked")
		public void run() {
			try {
				System.out.println("Verbunden: " + mySocket.isConnected());
				_writer = new PrintWriter(mySocket.getOutputStream(), true);
				_reader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
				_writer.println(usernameChooser.getText() + " Connected!");
				while (true) {
					String message = _reader.readLine();

					if (message == null) {
						System.out.println("got no message");

					} else {
						messages.add(message);
						chatBox.append(message + "\n");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}