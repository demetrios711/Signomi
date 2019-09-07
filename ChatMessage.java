import java.io.*;
public class ChatMessage implements Serializable
{
	static final int WHOISIN = 0; // Is the signal to return a list of users 
   static final int MESSAGE = 1; // Is the signal to display a message
   static final int DRAWCARD = 2; //Is the signal to draw a card
   static final int SELECT = 3; //Selects a tile for either pawn movement or swapping.
   static final int MOVEPAWNTOSAFEZONE = 4; //Is the signal that a pawn wants to be moved to a safe zone.
   static final int LEAVESTART = 5; //Is the signal that the player wishes to move a pawn from start.
   static final int BOARDMOVEMENT = 6; //Is the signal that a position has been occupied by a spawn
   
	private String message;   
	private int type;
   private int pos;
   private int player;
   
   private static final long serialVersionUID = 7526472295622777L;

	// constructor
	ChatMessage(int type, String message) 
   {
		this.type = type;
		this.message = message;
	}
   ChatMessage(int type, int pos)
   {
      this.type = type;
      this.pos = pos;
   }
   ChatMessage(int type)
   {
      this.type = type;
   }   
   ChatMessage(int type, int player, int pos)
   {
      this.type = type;
      this.player = player;
      this.pos = pos;
   }
	
	// getters
	int getType() 
   {
		return type;
	}
	String getMessage() 
   {
		return message;
	} 
   int getPos()
   {
      return pos;
   }
   int getPlayer()
   {
      return player;
   }
}
