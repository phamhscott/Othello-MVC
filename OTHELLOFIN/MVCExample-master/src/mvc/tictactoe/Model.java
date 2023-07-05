package mvc.tictactoe;

import com.mrjaffesclass.apcs.messenger.*;
import java.awt.Color;
import java.awt.Point;

/**
 * The model represents the data that the app uses.
 * @author Roger Jaffe
 * @version 1.0
 */
public class Model implements MessageHandler {

  // Messaging system for the MVC
  private final Messenger mvcMessaging;

  // Model's data variables
  private boolean whoseMove;
  private boolean gameOver;
  private Color[][] board;
  private Color defaultColor = new Color(0,159,105);
  

  /**
   * Model constructor: Create the data representation of the program
   * @param messages Messaging class instantiated by the Controller for 
   *   local messages between Model, View, and controller
   */
  public Model(Messenger messages) {
    mvcMessaging = messages;   
    this.board = new Color[8][8];
    this.gameOver = false;
  }
  
  /**
   * Initialize the model here and subscribe to any required messages
   */
  public void init() {
    this.newGame();
    this.mvcMessaging.subscribe("playerMove", this);
    this.mvcMessaging.subscribe("newGame", this);

  }
  
  
   /**
   * Reset the state for a new game
   */
  private void newGame() {
    for(int row=0; row<this.board.length; row++) {
      for (int col=0; col<this.board[0].length; col++) {
        this.board[row][col] = defaultColor;
      }
    }
    
    this.board[3][3] = Color.WHITE;
    this.board[4][4] = Color.WHITE;
    this.board[3][4] = Color.BLACK;
    this.board[4][3] = Color.BLACK;
    
    this.whoseMove = false;
    this.gameOver = false;
  }

  
  @Override
  public void messageHandler(String messageName, Object messagePayload) {
     // Display the message to the console for debugging
     boolean onePlayerCantMove = false;
     boolean neitherPlayerCanMove = false;
     
    if (messagePayload != null) {
      System.out.println("MSG: received by model: "+messageName+" | "+messagePayload.toString());
    } else {
      System.out.println("MSG: received by model: "+messageName+" | No data sent");
    }
    
   
        
    // playerMove message handler
   
        if (messageName.equals("playerMove") && countSquares(defaultColor) >0){
          // Get the position string and convert to row and col
          String position = (String)messagePayload;
          Integer row = new Integer(position.substring(0,1));
          Integer col = new Integer(position.substring(1,2));
          // If square is blank...


               if (isLegalMove(this.whoseMove, row, col)) {
                 makeMove(this.whoseMove, row, col);
                 this.whoseMove = !whoseMove;
                 this.mvcMessaging.notify("whoseMove", this.whoseMove);


                }

               else if(!isLegalMove(this.whoseMove, row, col) && position!=null) {
                  //  this.gameOver = true;
                    this.mvcMessaging.notify("illegalMove");
               }

               this.mvcMessaging.notify("boardChange", this.board);

             int whiteSquares = countSquares(Color.WHITE);
             int blackSquares = countSquares(Color.BLACK);
             int[][] arr1 = new int[whiteSquares][blackSquares];
             this.mvcMessaging.notify("squaresNum", arr1 );


           
           
          
          
    
     
         
         
         
        // Send the boardChange message along with the new board 
       
      
    
       
    }
        if (messageName.equals("newGame")) {
      // Reset the app state
         this.newGame();
      // Send the boardChange message along with the new board 
      int whiteSquares = countSquares(Color.WHITE);
         int blackSquares = countSquares(Color.BLACK);
         int[][] arr1 = new int[whiteSquares][blackSquares];
      
      this.mvcMessaging.notify("boardChange", this.board);
     this.mvcMessaging.notify("whoseMove", whoseMove);
     this.mvcMessaging.notify("squaresNum", arr1);
    
  }
    
    
     if (noMovesAvailable(this.whoseMove)) {
               
                   //this.mvcMessaging.notify("noMoves", this.whoseMove);
                   //this.gameOver = true;
                  
                   this.mvcMessaging.notify("oneNoMoves", this.whoseMove);
                   this.whoseMove = !whoseMove;
                   
               
           
           }
   
        if(countSquares(defaultColor) ==0 ) {
                   this.gameOver = true;
                   
               int whiteSquares = countSquares(Color.WHITE);
               int blackSquares = countSquares(Color.BLACK);
               
               if(blackSquares > whiteSquares){
                   this.mvcMessaging.notify("winner", "BLACK");
               }
               else if(whiteSquares > blackSquares){
                   this.mvcMessaging.notify("winner", "WHITE");
               }
               else {
                    this.mvcMessaging.notify("tie");
               }
        }
    


    
     
    
    
          
  
  
    
  
    
     //newGame message handler
  
      
      
}
  
  
      
  
    public int countSquares (Color color){
        
        int count = 0;
        for (int row = 0; row <8; row++){
            for(int col = 0; col <8; col++) {
                if(this.board[row][col] == color ) {
                    count++;
            }
        }
            
        }
        return count;
    }
    
     public  Position getVector(String compassPoint) {
    switch (compassPoint) {
      case "N":  return new Position(-1,0);
      case "NE": return new Position(-1,1);
      case "E":  return new Position(0,1);
      case "SE": return new Position(1,1);
      case "S":  return new Position(1,0);
      case "SW": return new Position(1,-1);
      case "W":  return new Position(0,-1);
      case "NW": return new Position(-1,-1);
    }
    return null;
  }
  
    public static String[] getDirections() {
    String[] points = {"N","NE","E","SE","S","SW","W","NW"};
    return points;
  }
  
    public boolean isLegalMove(boolean player, int row, int col) {
        
        if (this.board[row][col] != defaultColor ) {
            return false;
        }
        
        for(String direction : getDirections()) {
            Position directionVector = getVector(direction);
            if(step(player, row, col, directionVector.x, directionVector.y, 0)) {
                return true;
            }
        }
    
        return false;
    }
    
    public boolean noMovesAvailable(boolean player){
        for (int row = 0; row < 8; row++) {
            for(int col = 0; col <8; col++){
                if (isLegalMove (player, row, col)){
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean boardFilled(){
        for (int row = 0; row < 8; row++) {
            for(int col = 0; row <8; col++){
                if (this.board[row][col]!= defaultColor){
                    return true;
                }
            }
        }
        return false;
    }
    
    
    protected boolean step(boolean player, int row, int col, int drow, int dcol, int count){
        int nrow = row + drow;
        int ncol = col + dcol;
        Color color;
        
        if(player == false){
            color = Color.BLACK;
        }
        else {
            color = Color.WHITE;
        }
        
        if( nrow < 0 || nrow >= 8 || ncol < 0 || ncol >= 8){
            return false;
        }
        else if (this.board[nrow][ncol] == defaultColor && count == 0) {
            return false;
        }
        else if(color != this.board[nrow][ncol] && this.board[nrow][ncol] != defaultColor) {
            return step (player, nrow, ncol, drow, dcol, count+1);
        }
        else if (color == this.board[nrow][ncol]){
            return count >0;
        }
        else {
            return false;
        }
    
   

    }
    
        
    
        private boolean makeMoveStep (boolean player, int row, int col, int drow, int dcol, int count) {
            int nrow = row + drow;
            int ncol = col + dcol;
            Color color;
        
            if(player == false){
                color = Color.BLACK;
            }
            else {
                color = Color.WHITE;
            }
            
            
            if( nrow < 0 || nrow >= 8 || ncol < 0 || ncol >= 8){
                return false;
            }
            else if(color != this.board[nrow][ncol] && this.board[nrow][ncol] != defaultColor) {
                boolean valid = makeMoveStep(player, nrow, ncol, drow, dcol, count+1);
                if (valid){
                    this.board[nrow][ncol] = color;
                }
                return valid;
            }
            else if (this.board[nrow][ncol] == color) {
                    return count > 0;
                }
            else {
                    return false;
                }
        
    
    

        }
        
        public void makeMove (boolean player, int row, int col) {
            Color color;
        
            if(player == false){
                color = Color.BLACK;
            }
            else {
                color = Color.WHITE;
            }
        
            for(String direction : getDirections()) {
                Position directionVector = getVector(direction);
                if(makeMoveStep(player, row, col, directionVector.x, directionVector.y,0 )){
                    this.board[row][col] = color;
            }
        }
    }
        
        
        
        public class Position extends Point {
  
  public Position(int row, int col) {
    super (row, col);
  }
  
  public int getRow() {
    return this.x;
  }
  
  public int getCol() {
    return this.y;
  }
  
  public Position translate(Position vector) {
    return new Position(this.x+vector.x, this.y+vector.y);
  }
  
 // public boolean isOffBoard() {
 //   return (this.x < 0 || this.x >= Constants.SIZE ||
 //     this.y < 0 || this.y >= Constants.SIZE);
//  }
  
 // @Override
 /// public String toString() {
 //   return "["+this.getRow()+","+this.getCol()+"]";
 // }

}
}

