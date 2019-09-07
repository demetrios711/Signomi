public class Pawn
{
   final int ownerNumber; //Numbers the owners
   final int pawnNumber; //Numbers the pawns 1-4
   boolean inSafeZone; //Dictates if the pawn is within the safe zone
   boolean borderingSafeZone; //Dictates if the pawn can move into the safe zone
   boolean inStart; //Dictates if the pawn is in the "start" zone
   boolean home; //Dictates if the pawn has reached home
   String color; //Colors the pawn for eventual GUI
   int position; //Maintains where the pawn is 
   
   public Pawn(int ownerNumber, int pawnNumber, int position, String color)
   {
      this.ownerNumber = ownerNumber;
      this.pawnNumber = pawnNumber;
      this.color = color;  
      inSafeZone = false;
      inStart = true;
      borderingSafeZone = false;
      home = false;
      System.out.println("Pawn Created! Owner Number: " + ownerNumber + " Pawn Number: " + pawnNumber + " Color: " + color);         
   }
   
   public int getOwner()
   {
      return ownerNumber;
   }
   public int getPawn()
   {
      return pawnNumber;
   }
   public void isSafe()
   {
      inSafeZone = true;
   }
   public void leaveStart()
   {
      inStart = false;
      switch(ownerNumber){
         case 0: position = 5;
            break;
         case 1: position = 20;
            break;
         case 2: position = 50;
            break;
         case 3: position = 35;
            break;
         }
   }
   public int getPosition()
   {
      return position;
   }
   public int getState()
   {
      if(home = true)
      {
         return 4; //"Home";
      }
      if(inStart = true)
      {
         return 5; //"inStart";
      }
      if(borderingSafeZone = true)
      {
         return 6; //"borderingSafeZone";
      }
      if(inSafeZone = true)
      {
         return 7; //"inSafeZone";
      }
      return 420;
   }
}
