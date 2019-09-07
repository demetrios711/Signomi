import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class Server 
{
	private static int uniqueId; //Unique ID for clients
	private ArrayList<ClientThread> clientList; //Keeps a list of all clients
	private ServerGUI sg; //Server GUI object
	private SimpleDateFormat sdf; //Displays the current time
   private String gameName;
	private int port; //Port to be connected through
	private boolean keepGoing; //Dictates whether or not the client continues to run	
   public static int ps = 1; //Counts the number of connected players
   private int whoTurn = 1;
   int gameBoard[] = new int[64]; //Creates the board. 
   int safeZone[][] = new int[5][5]; //2D Array of Safe Zones. 
   int startZone[][] = new int[5][1]; //2D Array for Start Zones
     
   SignomiDeck deck;
   

   //Creates server + calls main constructor saying that there is no GUI.
	public Server(int port) 
   {
		this(port, null, "BanthaPoodoo");
	}
	
	public Server(int port, ServerGUI sg, String gameName) 
   {
	   this.sg = sg; //GUI
		this.port = port; //Port
      this.gameName = gameName; //Game Name
      deck = new SignomiDeck();
		sdf = new SimpleDateFormat("HH:mm:ss"); //SDF
		clientList = new ArrayList<ClientThread>(); //Creates the list of all clients
	}
   public boolean canIplay(int playerNum) //Returns true or false depending on if the supplied player number matches the current term.
   {
      if(whoTurn == playerNum)
         return true;
         else
            return false;
   }	
	public void start() 
   {
		keepGoing = true;
      
      //Shuffles the deck      
      deck.shuffle();           
      //Creates the server
		try 
		{
			ServerSocket serverSocket = new ServerSocket(port); //Socket used by server

			// Waits for clients to connect
			while(keepGoing) 
			{
				display("Server waiting for Clients on port " + port + ".");
				/*Checks amount of players within the game, if 4 players have connected, then ps will equal 5.
              If ps equals five, then ps is set to six, preventing any further connections.
             */
            if(ps == 6) 
            {
               System.out.println("Game Full");
               ps = 6;
            }            
            if(ps < 6)
            { 
				   Socket socket = serverSocket.accept();  	// Accepts a connection if someone tries to connect
				   if(!keepGoing) //Stops the server from waiting for clients if a shutdown request has been sent.
               {
					   break;
               }
				   ClientThread t = new ClientThread(socket);  // creates a new thread for the client
				   clientList.add(t); // Saves the client to the arrayList
				   t.start();
               ps++;           
            }            
			}
         
         //Server closes all connections + itself 
			try 
         {
				serverSocket.close();
				for(int i = 0; i < clientList.size(); ++i) 
            {
					ClientThread tc = clientList.get(i);
					try 
               {               
					   tc.sInput.close();
					   tc.sOutput.close();
					   tc.socket.close();
					}
					catch(IOException ioE) 
               {
                  System.out.println("*Throws up hands*"); //Nothing to do
					}
				}
			}
			catch(Exception e) 
         {
				display("Exception closing the server and clients: " + e);
			}
		}
		// Some error happened.
		catch (IOException e) 
      {
         String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}		


	public void stop() 
   {
		keepGoing = false;
		try 
      {
			new Socket("localhost", port);
		}
		catch(Exception e) 
      {
         System.out.println("*Throws hands in air*"); //This shouldn't happen.
		}
	}

   //Displays the message with the time before it.
	private void display(String msg) 
   {
		String time = sdf.format(new Date()) + " " + msg;
		if(sg == null)
      {
			System.out.println(time);
      }
		else
      {
			sg.appendEvent(time + "\n");
      }
	}	

   //If the port number is not specified 10000 is used	 
	public static void main(String[] args) 
   {
		// start server on port 10000 unless a PortNumber is specified 
		int portNumber = 10000;
		switch(args.length) 
      {
			case 1:
				try 
            {
					portNumber = Integer.parseInt(args[0]);
				}
				catch(Exception e) 
            {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Server [portNumber]");
					return;
				}
			case 0:
				break;
			default:
				System.out.println("Usage is: > java Server [portNumber]");
				return;
				
		}
		// Creates a server object and starts it.
		Server server = new Server(portNumber);
		server.start();
	}
   
	private synchronized void broadcast(String message) 
   {
		String time = sdf.format(new Date()); 
		String messageLf = time + " " + message + "\n"; //Appends time to beginning of message

      ChatMessage sendMessage = new ChatMessage(1, messageLf);

		if(sg == null) //Checks to see if using GUI
      {
			System.out.print(messageLf);
      }
		else
      {
			sg.appendRoom(messageLf); // append in the room window
      }
		
		// we loop in reverse order in case we would have to remove a Client
		// because it has disconnected
		for(int i = clientList.size(); --i >= 0;) 
      {
			ClientThread ct = clientList.get(i);
         
			if(!ct.writeMsg(sendMessage)) //Remove client from list if client fails to respond.
         {
				clientList.remove(i);
				display("Disconnected Client " + ct.username + " removed from list.");
			}
		}
	}
   
	private synchronized void updateBoard(int playerNum, int positionOnBoard) 
   {
      ChatMessage updatePos = new ChatMessage(6, playerNum, positionOnBoard);
		// we loop in reverse order in case we would have to remove a Client
		// because it has disconnected
		for(int i = clientList.size(); --i >= 0;) 
      {
			ClientThread ct = clientList.get(i);
         
			if(!ct.writeBoard(updatePos)) //Remove client from list if client fails to respond.
         {
				clientList.remove(i);
				display("Disconnected Client " + ct.username + " removed from list.");
			}
		}
	}
   
	private synchronized void updateCard(int moves) 
   {
      ChatMessage drawnCard = new ChatMessage(2, moves);
   
		// we loop in reverse order in case we would have to remove a Client
		// because it has disconnected
		for(int i = clientList.size(); --i >= 0;) 
      {
			ClientThread ct = clientList.get(i);
         
			if(!ct.writeCard(drawnCard)) //Remove client from list if client fails to respond.
         {
				clientList.remove(i);
				display("Disconnected Client " + ct.username + " removed from list.");
			}
		}
	}      
    
   //Creates an instance of this thread for every client connected.
	class ClientThread extends Thread 
   {
		Socket socket; 
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		int id; //Unique ID
		String username; //Players username
      String nameOfGame; //The name of the game.
		ChatMessage cm; //Chat message objects
		String date; //Date of connection
      Player player; //Player Object - Handles Pawns
      int numPawnsHome = 0;
      
      boolean canMovePawnFromStart;
      
      
      int selected; //Dictates what tile has been selected
      boolean somethingWasSelected = false; //Records if something was selected, in order for targeted to be used. 
      int targeted; //Dictates the tile that has been targeted after one was selected
      
      int pawnOwner; //Saves the value of the space selected, that is, if it's 1, it's player 1, 2, player 2, etc..

		// Constructore
		ClientThread(Socket socket) 
      {
			// a unique id
			id = ++uniqueId;
			this.socket = socket;

         
			System.out.println("Thread trying to create Object Input/Output Streams");
			try
			{
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				username = (String) sInput.readObject();          
            player = new Player(username, ps, ps);            
				display(username + " just connected.");
            display(username + " had 4 pawns placed in his start");
            startZone[player.getNumber()][0] = 4; //Places 4 pawns in the players start.
			}
			catch (IOException e) 
         {
				display("IO Exception" + e);
				return;
			}
        
			catch (ClassNotFoundException e) 
         {
				display("Game Not Found" + e);
				return;         
			}
            date = new Date().toString() + "\n";
		}
      
      //Runs forever for client
		public void run() 
      {
			boolean keepGoing = true;
         boolean please; //Dictates if the player is allowed to play.
         boolean canDrawCard; //Dictates if the player can draw a card
         
			while(keepGoing) 
         {
            please = canIplay(player.getNumber()); //Sends the players number to the method "canIplay" to check if it is their turn.
            //Reads an object.
				try 
            {
					cm = (ChatMessage) sInput.readObject();
				}
				catch (IOException e) 
            {
					display(username + " Exception reading Streams: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) 
            {
					break;
				}
				// Switch on the type of message receive
				switch(cm.getType()) 
            {   
				   case ChatMessage.MESSAGE: //1
                  //Pulls the message from the chat object.
   	   			String message = cm.getMessage();            
					   broadcast(username + ": " + message);
					   break;
            }   
            if(please)
            {
               switch(cm.getType())
               {      
                  case ChatMessage.DRAWCARD: //2
                     //Pops a card from the deck stack
                     if(canDrawCard = true)
                     {
                        int moves = deck.drawCard().readCard();
                        player.setMoves(moves);
                        sg.appendDrawCard(moves);
                        canDrawCard = false;
      					   updateCard(moves);
                        if(moves == 1 || moves == 2)
                        {
                           canMovePawnFromStart = true;
                           System.out.println("You can move a pawn from start!");
                        }
                     }
                     else
                     {
                        System.out.println(player.getName() + " has already drawn a card.");
                     }
                     break;
                     
                  case ChatMessage.SELECT: //3
                     int tempHolder = cm.getPos();
                     if(somethingWasSelected) //Check if the player has previously clicked on a space with a pawn in it.
                     {
                        targeted = tempHolder;
                        int allowedTarget = 3000;
                        if((selected + player.getMoves()) > 63)
                        {
                           allowedTarget = ((selected + player.getMoves()) - 63);
                           System.out.println("Move Size: - " + player.getMoves());
                           System.out.println("Targeted Tile: - " + targeted);
                           System.out.println("Potential Target: - " + allowedTarget);
                           
                        } 
                        else
                        {
                           allowedTarget = selected + player.getMoves();
                           System.out.println("Move Size: - " + player.getMoves());  
                           System.out.println("Targeted Tile: - " + targeted);         
                           System.out.println("Potential Target: - " + allowedTarget);                                                    
                        }
                        
                        if(targeted == selected)
                        {
                           selected = targeted; //This allows the player to change their mind and select a different pawn
                           System.out.println("Tile " + selected + " now selected.");
                        }
                        else if(player.getMoves() == 300) //This is the check to see if the player drew a Signomi card.
                        {
                           startZone[player.getNumber()][0] = startZone[player.getNumber()][0] - 1;                         
                           int rektPlayer = gameBoard[targeted];
                           startZone[rektPlayer][0] = startZone[rektPlayer][0] + 1; 
                           gameBoard[targeted] = player.getNumber();  
                           somethingWasSelected = false;
                           canDrawCard = true;
                           canMovePawnFromStart = false;
                           player.setMoves(0);      
                           whoTurn();                                                                     
                        }
                        else if(targeted == allowedTarget)
                        {
                           if(gameBoard[targeted] == 0) //Check if the targeted space on the board is empty.
                           {
                              System.out.println("Pawn of " + player.getName() + " occupying Space - " + targeted);   
                              gameBoard[targeted] = player.getNumber(); //Moves the players pawn to the targeted space
                              gameBoard[selected] = 0; //Erases the players pawn from the originally selected space
                              sg.appendMove(targeted, username);    
                              somethingWasSelected = false;
                              canDrawCard = true;
                              canMovePawnFromStart = false;
            					   updateBoard(player.getNumber(), targeted);   
                              player.setMoves(0);   
                              whoTurn();                                                                                                                                                                                                                    
                           }
                           else if(gameBoard[targeted] != player.getNumber())
                           {
                              int rektPlayer = gameBoard[targeted];
                              startZone[rektPlayer][0] = startZone[rektPlayer][0] + 1; 
                              System.out.println("Space Occupied by Player number: " + gameBoard[targeted] + " your pawn knocks his to start.");
                              System.out.println("Pawn of " + player.getName() + " occupying Space - " + targeted);   
                              gameBoard[targeted] = player.getNumber(); //Moves the players pawn to the targeted space
                              gameBoard[selected] = 0; //Erases the players pawn from the originally selected space
                              sg.appendMove(targeted, username);   
                              somethingWasSelected = false;
                              canDrawCard = true;
                              canMovePawnFromStart = false;
            					   updateBoard(player.getNumber(), targeted); 
                              player.setMoves(0);
                              whoTurn();                                 
                           }
                           else
                           {
                              System.out.println("You can not bump you own pawns back to start, please target a different space.");
                           }
                        }
                        else
                        {
                           System.out.println("You can't move there, select a different tile.");
                        }                        
                           
                     }    
                     else if(gameBoard[tempHolder] < 5) //Saves the ownership of the selected tile and checks if the player actually owns it.
                     {
                        pawnOwner = gameBoard[tempHolder]; 
                        if(pawnOwner != player.getNumber()) //Checks to see if the player owns the selected space
                        {
                           if(pawnOwner == 0) //Checks to see if the space is empty
                           {
                              System.out.println("Space Unoccupied - Please Select Your Own Pawns");   
                           }
                           else
                           {
                              System.out.println("Space Occupied - Please Select Your Own Pawns");               
                           }                  
                        }
                        else
                        {
                           selected = tempHolder;     
                           somethingWasSelected = true;   
                           System.out.println("Space Selected - By Player " + player.getName() + " with ownership number " + player.getNumber());                                                                      
                        }
                     }              
                     else
                     {
                        System.out.println("Space Unoccupied - Please Select An Occupied Space");
                     }
                     break;
                     
                  case ChatMessage.MOVEPAWNTOSAFEZONE: //4
                     int z = cm.getPos();       
                     if(safeZone[player.getNumber()][z] == 0)
                     {
                        System.out.println("Pawn of " + player.getName() + " occupying Space - " + z);   
                        safeZone[player.getNumber()][z] = player.getNumber();  
                        sg.appendMove(z, username);    
                        whoTurn();   
                     }
                     else
                     {
               		   display("Space Occupied - Please Select Another");                    
                        System.out.println("Space Occupied - Please Select Another");
                     }
                     break;                 
                                       
                  case ChatMessage.LEAVESTART: //5
                     //Moves a pawn from start to the space right outside of start
                     int sentNum = cm.getPos();
                     if(sentNum == player.getNumber())
                     {
                        if(canMovePawnFromStart && startZone[player.getNumber()][0] > 0)
                        {
                           if(gameBoard[player.getStartPosition()] == 0)
                           {
                              startZone[player.getNumber()][0] = startZone[player.getNumber()][0] - 1; 
                              gameBoard[player.getStartPosition()] = player.getNumber();
            					   updateBoard(player.getNumber(), player.getStartPosition());                                                               
                              System.out.println(player.getName() + " moved pawn to position bordering start "); 
                              player.setMoves(0);
                              whoTurn();                              
                           }
                           else if(gameBoard[player.getStartPosition()] == player.getNumber())
                           {
                              System.out.println("One of your pawns is already occupying your start position, you must move it before you can bring out more pawns.");
                           }
                           else
                           {
                              int rektPlayer = gameBoard[player.getStartPosition()];
                              startZone[rektPlayer][0] = startZone[rektPlayer][0] + 1; 
                              System.out.println("Space Occupied by Player number: " + gameBoard[targeted] + " your pawn knocks his to start.");
                              System.out.println("Pawn of " + player.getName() + " occupying Space - " + targeted);   
                              gameBoard[player.getStartPosition()] = player.getNumber(); //Moves the current players pawn to the targeted space
                              player.setMoves(0);
                              whoTurn();                                 
                           }                        
                        }
                        else
                        {
                           System.out.println("You must draw a 1 or a 2 in order to move a pawn from start");
                        }
                     }
                     else
                     {
                        System.out.println("You can only move your pawns out.");
                     }
                     break;                  
   				}
		   	}
		   }
         //Removes ID from clientlist
	      clientList.remove(id);
         ps--;
		   close();         
		}
		private void close() 
      {
			try 
         {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try 
         {
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {};
			try 
         {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}
 
      //Sends message to client
		private boolean writeMsg(ChatMessage msg) 
      {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) 
         {
				close();
				return false;
			}
			// write the message to the stream
			try 
         {
				sOutput.writeObject(msg);
			}
			// if an error occurs, do not abort just inform the user
			catch(IOException e) 
         {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}
		private boolean writeBoard(ChatMessage msg) 
      {
			// if Client is still connected send the command to it
			if(!socket.isConnected()) 
         {
				close();
				return false;
			}
			// write the message to the stream
			try 
         {
				sOutput.writeObject(msg);
			}
			// if an error occurs, do not abort just inform the user
			catch(IOException e) 
         {
				display("Error updating " + username + " on board movements.");
				display(e.toString());
			}
			return true;
		}
		private boolean writeCard(ChatMessage msg) 
      {
			// if Client is still connected send the command to it
			if(!socket.isConnected()) 
         {
				close();
				return false;
			}
			// write the message to the stream
			try 
         {
				sOutput.writeObject(msg);
			}
			// if an error occurs, do not abort just inform the user
			catch(IOException e) 
         {
				display("Error updating " + username + " on card drawn.");
				display(e.toString());
			}
			return true;
		}
      public void whoTurn()
      {
         if(whoTurn == clientList.size())
            whoTurn = 1;
         else
            whoTurn++;    
      }
	}
}

