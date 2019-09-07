public class Card 
{
   private Suits suit;
   public Card(Suits suit) 
   {
      this.suit = suit;
   }
   public int readCard()
   {
         switch(suit)
         {
            case SIGNOMI:
               System.out.println("SIGNOMI");
               return 300; //"SIGNOMI";
            case ONE:
               System.out.println("ONE");
               return 1; //"ONE";
            case TWO:
               System.out.println("TWO");
               return 2; //"TWO";
            case THREE:
               System.out.println("THREE");
               return 3; //"THREE";
            case FOUR:
               System.out.println("FOUR");
               return 4; //"FOUR";
            case FIVE:
               System.out.println("FIVE");
               return 5; //"FIVE";
            case SEVEN:
               System.out.println("SEVEN");
               return 7; //"SEVEN";
            case EIGHT:
               System.out.println("EIGHT");
               return 8; //"EIGHT";
            case TEN:
               System.out.println("TEN");
               return 10; //"TEN";
            case ELEVEN:
               System.out.println("ELEVEN");
               return 11; //"ELEVEN";
            case TWELVE:
               System.out.println("TWELVE");
               return 12; //"TWELVE";
         } 
      return 420;   
   } 
}