import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/*
 * The GUI for Signomi
 */
 
public class HostGUI extends JFrame implements ActionListener {

	private JLabel label;
	private JTextField tf;
	private JTextField tfServer, tfPort, tfGameName;
	private JButton connect, exit, hostPlayers;
	private JTextArea ta;
	private boolean connected;
	private SignomiGameGUI gameGui;
	private int defaultPort;
	private String defaultHost;
   private String msg;
   private String username;
   private String gamename;


	// Constructor for receiving a socket number
	HostGUI(String host, int port, String username, String gamename) 
   {

		super("Signomi Landing");
		defaultPort = port;
		defaultHost = host;
		
		// NorthPanel
		JPanel northPanel = new JPanel(new GridLayout(3,1));
      
		// server name anmd the port number
		JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1, 3));
      
		// three text fields with default value for server address and port number
		tfServer = new JTextField(host);
		tfPort = new JTextField("" + port);
		tfPort.setHorizontalAlignment(SwingConstants.RIGHT);
      
      tfGameName = new JTextField(gamename);

		serverAndPort.add(new JLabel("Server Address:  "));
		serverAndPort.add(tfServer);
		serverAndPort.add(new JLabel("Port Number:  "));
		serverAndPort.add(tfPort);
		serverAndPort.add(new JLabel("Game Name:  "));
      serverAndPort.add(tfGameName);
      
		// addsr the Server and port field to the GUI
		northPanel.add(serverAndPort);

		// the Label and the TextField
		label = new JLabel("Player Name:", SwingConstants.CENTER);
		northPanel.add(label);
		tf = new JTextField(username);
		tf.setBackground(Color.WHITE);
		northPanel.add(tf);
      
		add(northPanel, BorderLayout.NORTH);

		// the 3 buttons
      hostPlayers = new JButton("Host");
		hostPlayers.addActionListener(this);
		hostPlayers.setEnabled(true);	
		connect = new JButton("Connect");
		connect.addActionListener(this);
		exit = new JButton("Exit");
		exit.addActionListener(this);
		exit.setEnabled(false);

		JPanel southPanel = new JPanel();
      southPanel.add(hostPlayers);
		southPanel.add(connect);
		southPanel.add(exit);
		add(southPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 150);
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
		connect.setEnabled(true);
		exit.setEnabled(false);
		hostPlayers.setEnabled(true);
		label.setText("Enter the player name below");
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
		// if it is the exit button		

		if(o == connect) 
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
         
         String gameName = tfGameName.getText().trim();
			if(gameName.length() == 0)
				return;         

			// create a new Client with GUI
			gameGui = new SignomiGameGUI(username, gameName);
			connected = true;
		}
      if(o == exit)
      {
         connect.setEnabled(true);
      }
      if(o == hostPlayers)
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
         String gameName = tfGameName.getText().trim();
			if(gameName.length() == 0)
				return;             
				      
         ServerGUI theServer = new ServerGUI(10000, gameName);
			gameGui = new SignomiGameGUI(username, gameName);
			connected = true;         
      }
      

	}

	public static void main(String[] args) {
		new HostGUI("localhost", 10000, "Enter Username", "Enter Gamename");
	}

}
