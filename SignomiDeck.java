import java.util.*;

public class SignomiDeck 
{
   Stack<Card> deck = new Stack<Card>();
   
   public SignomiDeck() 
   {
      makeDeck();
   }       
   public void shuffle() 
   {
      Collections.shuffle(deck); 
   }
   public Card drawCard()
   {
      return deck.pop();
   }
   public void makeDeck()
   {
      for (int i = 0; i <= 45; i++) 
      {
         if(i<4)
         {
            Card card = new Card(Suits.SIGNOMI); //Instantiate a Card
            deck.push(card);
         }      
         else if(i<9)
         {
            Card card = new Card(Suits.ONE); //Instantiate a Card
            deck.push(card); //Adding card to the Deck
         }
         else if(i<13)
         {
            Card card = new Card(Suits.TWO); //Instantiate a Card
            deck.push(card);
         }    
         else if(i<17)
         {
            Card card = new Card(Suits.THREE); //Instantiate a Card
            deck.push(card);
         }
         else if(i<21)
         {
            Card card = new Card(Suits.FOUR); //Instantiate a Card
            deck.push(card);
         }   
         else if(i<25)
         {
            Card card = new Card(Suits.FIVE); //Instantiate a Card
            deck.push(card);
         }                            
         else if(i<29)
         {
            Card card = new Card(Suits.SEVEN); //Instantiate a Card
            deck.push(card);
         }
         else if(i<33)
         {
            Card card = new Card(Suits.EIGHT); //Instantiate a Card
            deck.push(card);
         }
         else if(i<37)
         {
            Card card = new Card(Suits.TEN); //Instantiate a Card
            deck.push(card);
         }
         else if(i<41)
         {
            Card card = new Card(Suits.ELEVEN); //Instantiate a Card
            deck.push(card);
         }
         else if(i<45)
         {
            Card card = new Card(Suits.TWELVE); //Instantiate a Card
            deck.push(card);
         }
      }
   }
}