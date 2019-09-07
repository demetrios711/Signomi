import java.util.*;
public class Player
{
   final String name; //Player Name
   final int number; //Player Number
   final int gBoardPosition; //Starting from top left, to top right, to bottom left, to bottom right, 1,2,3,4.
   int startPosition = 5;
   int moves;
   String color;
   List<Pawn> PawnList = new ArrayList<Pawn>();   
   
   public Player(String name, int number, int gBoardPosition)
   {
      this.name = name;
      this.number = number;
      this.gBoardPosition = gBoardPosition;
      switch (gBoardPosition) {
         case 1: color = "red";
                 break;
         case 2: color = "blue";
                 break;
         case 3: color = "green";
                 break;
         case 4: color = "yellow";
                 break;
      }      
      System.out.println("Player Created! Name: " + name + " Your color is " + color + ".");
      
      switch(number){
         case 1: startPosition = 5;
            break;
         case 2: startPosition = 20;
            break;
         case 3: startPosition = 50;
            break;
         case 4: startPosition = 35;
            break;
         }      
      populatePawns();
   }
   
   public String getName() //Returns player name
   {
      return name;
   }
   public int getNumber() //Returns player number
   {
      return number;
   }
   public void populatePawns() //Creates the players pawns
   {
      for(int i = 1; 1 < 5; i++)
      {
         PawnList.add(new Pawn(gBoardPosition, i, gBoardPosition, color));
         if(i == 4)
         {
            break;
         }
      }   
   }
   public int getPawnPosition(int pawnNumber)
   {
      return PawnList.get(pawnNumber).getPosition(); 
   }
   public int getPawnState(int pawnNumber)//NOT DONE
   {
      PawnList.get(pawnNumber).getState();
      return 0;
   }
   public void setMoves(int moves)
   {
      this.moves = moves;  
   }
   public int getMoves()
   {
      return moves;
   }
   public String getColor()
   {
      return color;
   }
   public int getStartPosition()
   {
      return startPosition;
   }

}