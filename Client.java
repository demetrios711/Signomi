import java.net.*;
import java.io.*;
import java.util.*;

/*
 * The Client for the Client/Server chat program
 */
public class Client  {

	// declaring the Input and output streams
	private ObjectInputStream inputStream;		// to read from the socket
	private ObjectOutputStream outputStream;		// to write on the socket
	private Socket socket;

	// To use the GUI
   private SignomiGameGUI gGui;
	
	// declaring the server, the port, and the username
	private String server, username, gameName;
	private int port;

	/*
	 *  @param server: the server address
	 *  @param port: the port number
	 *  @param username: the username
	 */
	Client(String server, int port, String username) {
		this(server, port, username, null, null); // calls the common constructor with the GUI set to null
	}

	/*
	 * Constructor call
	 */
	Client(String server, int port, String username, SignomiGameGUI gGui, String gameName) {
		this.server = server;
		this.port = port;
		this.username = username;
      this.gGui = gGui;
      this.gameName = gameName;
	}
	
	/*
	 * To start the dialog
	 */
	public boolean start() {
   
      String confirmation = "dog";
      
		// try to connect to the server
		try {
			socket = new Socket(server, port);
		} 
      //Error connecting to the server
		catch(Exception ec) {
			display("Error connecting to server:" + ec);
			return false;
		}
		
	
		/* Creating both Data Stream */
		try
		{
			inputStream  = new ObjectInputStream(socket.getInputStream());
			outputStream = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		// creates the Thread to listen from the server 
		new ListenFromServer().start();
		// Send username to the server
		try
		{
			outputStream.writeObject(username);
		}
		catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);
            
		// success 
		return true;
	}

	/*
	 * To send a message to the GUI
	 */
	private void display(String msg) {
			gGui.append(msg + "\n");		// append to the ClientGUI JTextArea (or whatever)
	}
	
	/*
	 * To send a message to the server
	 */
	void sendMessage(ChatMessage msg) {
		try {
			outputStream.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}

	/*
	 * Error condition
	 */
	void disconnect() {
		try { 
			if(inputStream != null) inputStream.close();
		}
		catch(Exception e) {} 
		try {
			if(outputStream != null) outputStream.close();
		}
		catch(Exception e) {} 
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {} 
		
		// inform the GUI
		if(gGui != null)
			gGui.connectionFailed();
			
	}

	/*
	 * This class waits for the message from the server and append them to the JTextArea
	 */
	class ListenFromServer extends Thread 
   {
		ChatMessage cm; //Chat message objects
      int cardValue = 420;

		public void run() 
      {
			while(true) 
         {
				try 
            {
					cm = (ChatMessage) inputStream.readObject();            
				}
				catch(IOException e) 
            {
               System.out.println("Error Reading From Server");
            }
				catch(ClassNotFoundException e2) {}
					
            switch(cm.getType()) 
            {      
				   case ChatMessage.MESSAGE: //1
                  //Pulls the message from the chat object.
   	   			String message = cm.getMessage();
      			   gGui.append(message);        
					   break;
               case ChatMessage.DRAWCARD: //2
                  cardValue = cm.getPos();
                  gGui.DrawCard(cardValue);
                  break;
               case ChatMessage.BOARDMOVEMENT: //6
                  System.out.println("Great"); 
            }   
         
			}
		}
	}
}
