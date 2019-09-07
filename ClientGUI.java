import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/*
 * The Client GUI for the Client/Server chat program
 */
public class ClientGUI extends JFrame implements ActionListener {

	private JLabel label;
	private JTextField tf;
	private JTextField tfServer, tfPort;
	private JButton login, logout, aGroupMember;
	private JTextArea ta;
	private boolean connected;
	private Client client;
	private int defaultPort;
	private String defaultHost;
   private String msg;
   private ChatMessage cm;

	// Constructor for receiving a socket number
	ClientGUI(String host, int port) {

		super("aGroup Client");
		defaultPort = port;
		defaultHost = host;
		
		// NorthPanel
		JPanel northPanel = new JPanel(new GridLayout(3,1));
		// server name anmd the port number
		JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1, 3));
		// two text fields with default value for server address and port number
		tfServer = new JTextField(host);
		tfPort = new JTextField("" + port);
		tfPort.setHorizontalAlignment(SwingConstants.RIGHT);

		serverAndPort.add(new JLabel("Server Address:  "));
		serverAndPort.add(tfServer);
		serverAndPort.add(new JLabel("Port Number:  "));
		serverAndPort.add(tfPort);
		serverAndPort.add(new JLabel(""));
		// adds the Server and port field to the GUI
		northPanel.add(serverAndPort);

		// the Label and the TextField
		label = new JLabel("Enter your username below:", SwingConstants.CENTER);
		northPanel.add(label);
		tf = new JTextField("aGroup Member");
		tf.setBackground(Color.WHITE);
		northPanel.add(tf);
		add(northPanel, BorderLayout.NORTH);

		// The chat room
		ta = new JTextArea("Welcome to aGroup's Chat room\n", 80, 80);
		JPanel centerPanel = new JPanel(new GridLayout(1,1));
		centerPanel.add(new JScrollPane(ta));
		ta.setEditable(false);
		add(centerPanel, BorderLayout.CENTER);

		// the 3 buttons
		login = new JButton("Login");
		login.addActionListener(this);
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false);		// you have to login before being able to logout
		aGroupMember = new JButton("Send");
		aGroupMember.addActionListener(this);
		aGroupMember.setEnabled(false);		// you have to login before being able to see the group members

		JPanel southPanel = new JPanel();
		southPanel.add(login);
		southPanel.add(logout);
		southPanel.add(aGroupMember);
		add(southPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		tf.requestFocus();

	}

	// called by the Client to append text in the TextArea 
	void append(String str) 
   {
		ta.append(str);
		ta.setCaretPosition(ta.getText().length() - 1);
	}
	// called by the GUI if the connection failed
	// If yes, we reset our buttons, label, textfield
	void connectionFailed() 
   {
		login.setEnabled(true);
		logout.setEnabled(false);
		aGroupMember.setEnabled(false);
		label.setText("Enter your username below");
		tf.setText("Anonymous");
		// reset port number and host name
		tfPort.setText("" + defaultPort);
		tfServer.setText(defaultHost);
		
		tfServer.setEditable(false);
		tfPort.setEditable(false);
		
		tf.removeActionListener(this);
		connected = false;
	}
		
	/*
	* Button or JTextField clicked
	*/
	public void actionPerformed(ActionEvent e) 
   {
		Object o = e.getSource();
		// if it is the Logout button		

		if(o == login) 
      {
			String username = tf.getText().trim();
			// empty username
			if(username.length() == 0)
				return;
			// empty serverAddress
			String server = tfServer.getText().trim();
			if(server.length() == 0)
				return;
			// empty or invalid port numer
			String portNumber = tfPort.getText().trim();
			if(portNumber.length() == 0)
				return;
			int port = 0;
			try {
				port = Integer.parseInt(portNumber);
			}
			catch(Exception en) {
				return;   
			}

			// create a new Client with GUI
			client = new Client(server, port, username, this);
			// start the Client?
			if(!client.start()) 
				return;
			tf.setText("");
			label.setText("Enter your message below");
			connected = true;
			
			// disable login button
			login.setEnabled(false);
			// enable the 2 buttons
			logout.setEnabled(true);
			aGroupMember.setEnabled(true);
			// disable the Server and Port JTextField
			tfServer.setEditable(false);
			tfPort.setEditable(false);
			// Action listener for when the user enter a message
			tf.addActionListener(this);
		}
      if(o == logout)
      {
         client.disconnect();
         login.setEnabled(true);
      }
      if(o == aGroupMember)
      {
         cm = new ChatMessage(1, tf.getText());
         client.sendMessage(cm);
      }
      

	}

	public static void main(String[] args) {
		new ClientGUI("localhost", 10000);
	}

}
