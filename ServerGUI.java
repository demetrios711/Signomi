import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class ServerGUI extends JFrame implements ActionListener, WindowListener {
	
	private JButton stopStart, sendAnnouncement; //Stop and start button
	private JTextArea chat, event; //Text Area for Chat Room + Events
	private JTextField tPortNumber, makeAnnouncement; //Port Number + Announcement to Make
	private Server server; //Server Object
   private String gameName;
	
   //Starts server on port 10000
	public static void main(String[] arg) 
	{
		new ServerGUI(10000, "BanthaPoodoo");
	} 
   
	ServerGUI(int port, String gameName) //Constructor that recieves port
	{
		super("Signomi Game Server"); //States that the name of the server will be "aGroup's Chat Server
         
		this.gameName = gameName;
		server = null; //Sets server object to null
      
		//JPanel
		JPanel north = new JPanel();
      
		//Port Label
		north.add(new JLabel("Port number: "));
		tPortNumber = new JTextField("  " + port);
		north.add(tPortNumber);

		//Stop Start Buttons
		stopStart = new JButton("Start");
		stopStart.addActionListener(this);
		north.add(stopStart);
      
		//Announcement Button (Currently lacks action listener)
		north.add(new JLabel("Announce:"));
		makeAnnouncement = new JTextField(" ");
		north.add(makeAnnouncement);
		sendAnnouncement = new JButton("Announce");
		north.add(sendAnnouncement);
		add(north, BorderLayout.NORTH);
		
		// the event and chat room
		JPanel center = new JPanel(new GridLayout(2,1));
		chat = new JTextArea(80,80);
		chat.setEditable(false);
		appendRoom("Chat room.\n");
		center.add(new JScrollPane(chat));
		event = new JTextArea(80,80);
		event.setEditable(false);
		appendEvent("Events log.\n");
		center.add(new JScrollPane(event));	
		add(center);
		
		// need to be informed when the user click the close button on the frame
		addWindowListener(this);
		setSize(900, 1200);
		setVisible(true);
      
		//Creates the server object with this GUI
		server = new Server(port, this, gameName);

		//Starts the server on a new thread
		new ServerRunning().start();
		stopStart.setText("Stop");      
	}		

   //Appends messages to the end of the text areas.
	void appendRoom(String str) 
	{
		chat.append(str);
		chat.setCaretPosition(chat.getText().length() - 1);
	}
	void appendEvent(String str) 
	{
		event.append(str + "\n");
		event.setCaretPosition(chat.getText().length() - 1);		
	}
	void appendDrawCard(int x)
	{
		event.append("Card Number "+ x + " drawn." + "\n");
		event.setCaretPosition(chat.getText().length() - 1);		   
	}
	void appendMove(int position, String player)
	{
		event.append("Pawn of " + player + " occupying space " + position + "." + "\n");
		event.setCaretPosition(chat.getText().length() - 1);		   
	}   
   //Starts / Stops the server
	public void actionPerformed(ActionEvent e) 
	{
      
		// Turns off server if server is running
		if(server != null) 
		{
			server.stop();
			server = null;
			tPortNumber.setEditable(true);
			stopStart.setText("Start");
			return;
		}
      
      //Starts the server if server is not running.	
		int port;
		try 
		{
			port = Integer.parseInt(tPortNumber.getText().trim());
		}
		catch(Exception er) 
		{
			appendEvent("Invalid port number");
			return;
		}
      //Creates the server object with this GUI
		server = new Server(port, this, gameName);

      //Starts the server on a new thread
		new ServerRunning().start();
		stopStart.setText("Stop");
		tPortNumber.setEditable(false);
      
      
	}
	
   //Tells the server to close if user presses the X button
	public void windowClosing(WindowEvent e) 
	{
		// if my Server exist
		if(server != null) 
		{
			try 
			{
				server.stop();	// ask the server to close the conection
			}
			catch(Exception eClose) 
			{
				//Nothing to be done
			}
			server = null;
		}
		// dispose the frame
		dispose();
		System.exit(0);
	}

	//Thread that runs the server
	class ServerRunning extends Thread 
	{
		public void run() 
		{
			server.start();         // should execute until if fails
			// the server failed
			stopStart.setText("Start");
			tPortNumber.setEditable(true);
			appendEvent("Server crashed\n");
			server = null;
		}
   }
   
	// Without this I get an error, I assume because if I use windowListener, these have to exist.
	public void windowClosed(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}   

}
