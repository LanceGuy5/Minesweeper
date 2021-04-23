import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

/**
 * Class to run the game - when this is created, it creates a new Tic Tac Toe game
 */
public class GameDirector extends MouseAdapter{

    /**
     * Player information
     */
    int players;
    Player[] p = new Player[2];
    String whosTurn = "One's";
    int winner = -1;

    /**
     * Default board + position layout for rendering
     */
    SpaceType[][] board = new SpaceType[][]{
            {SpaceType.blank, SpaceType.blank, SpaceType.blank},
            {SpaceType.blank, SpaceType.blank, SpaceType.blank},
            {SpaceType.blank, SpaceType.blank, SpaceType.blank}
    };
    //[] == rows, [][] == columns, [][][] == {x1, y1}
    int[][][] boardLayout = new int[][][]{
            {{200, 200}, {350, 200}, {500, 200}},
            {{200, 355}, {355, 355}, {510, 355}},
            {{200, 510}, {355, 510}, {510, 510}}
    };

    /**
     * Click information
     */
    boolean playerOneClick = false;
    boolean playerTwoClick = false;
    int[] coords = new int[]{-1, -1};

    /**
     * Fonts below
     */
    Font f = new Font("Helvetica", Font.PLAIN, 32);
    Font space = new Font("Helvetica", Font.BOLD, 128);

    /**
     * Enums Below
     */
    enum PlayerType{
        human,
        robot
    }
    enum SpaceType{
        X,
        O,
        blank
    }

    Thread GameLoop = new Thread(this::gameLoop);

    public GameDirector(){

    }

    public void tick(){
        if(p[1].isTurn){
            whosTurn = "two's";
        }else{
            whosTurn = "one's";
        }
//        System.out.println(Arrays.toString(coords));
    }

    public void render(Graphics g){
        g.setColor(Color.black);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(15));
        g2.drawLine(325, 90, 325, 520);
        g2.drawLine(475, 90, 475, 520);
        g2.drawLine(180, 230, 610, 230);
        g2.drawLine(180, 385, 610, 385);
        g.setFont(f);
        if(winner == -1) g.drawString("Player " + whosTurn + " turn", 275, 50);
        else{
            if(winner == 1) g.drawString("Player one wins!", 275, 50);
            else if(winner == 2) g.drawString("Player two wins!", 275, 50);
        }
        g.setFont(space);
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                if(board[i][j].equals(SpaceType.X)){
                    g.drawString("X", boardLayout[i][j][0], boardLayout[i][j][1]);
                }else if(board[i][j].equals(SpaceType.O)){
                    g.drawString("O", boardLayout[i][j][0], boardLayout[i][j][1]);
                }
            }
        }
    }

    public void mouseReleased(MouseEvent e){
        int mX = e.getX();
        int mY = e.getY();
        if(TicTacToe.state.equals(TicTacToe.GAMESTATE.Game)){
            if(playerOneClick){
                try{
                    if(p[0].getIsTurn()){
                        coords = translateClickIntoPosition(mX, mY);
                    }
                }catch(NullPointerException ignored) {
                    System.out.println("P1 hasn't been created yet");
                }
            }else if(playerTwoClick){
                try{
                    if(players == 2) {
                        if (p[1].getIsTurn()) {
                            coords = translateClickIntoPosition(mX, mY);
                        }
                    }else{
                        System.out.println("AI Played");
                        p[1].playAI();
                    }
                }catch(NullPointerException ignored) {
                    System.out.println("P1 hasn't been created yet");
                }
            }
        }
    }

    public int[] translateClickIntoPosition(int x, int y){
        if(clickWithinBounds(x, y, 180, 90, 310, 215)){
            return new int[]{1, 1};
        }else if(clickWithinBounds(x, y, 335, 90, 460, 215)){
            return new int[]{1, 2};
        }else if(clickWithinBounds(x, y, 485, 90, 610, 215)){
            return new int[]{1, 3};
        }else if(clickWithinBounds(x, y, 180, 245, 310, 370)){
            return new int[]{2, 1};
        }else if(clickWithinBounds(x, y, 335, 245, 460, 370)){
            return new int[]{2, 2};
        }else if(clickWithinBounds(x, y, 485, 245, 610, 370)){
            return new int[]{2, 3};
        }else if(clickWithinBounds(x, y, 180, 400, 310, 520)){
            return new int[]{3, 1};
        }else if(clickWithinBounds(x, y, 335, 400, 460, 520)){
            return new int[]{3, 2};
        }else if(clickWithinBounds(x, y, 485, 400, 610, 520)){
            return new int[]{3, 3};
        }else{
            return new int[]{-1, -1};
        }
    }

    public boolean clickWithinBounds(int mouseX, int mouseY, int x1, int y1, int x2, int y2){
        if(mouseX > x1 && mouseX < x2){
            return mouseY > y1 && mouseY < y2;
        }else{
            return false;
        }
    }

    /**
     * Main loop for the actual game.
     */
    public void gameLoop(){
        //Beginning of the game
        Player player1 = p[0];
        Player player2 = p[1];
        player1.setIsTurn(true);
        clearBoard();
        winner = -1;
        do{
            try {
                Player temp = checkIfWinner(player1.st, player2.st, p);
                if(temp == null) System.out.println("NULL");
                if (temp != null) {
                    if(player1.equals(temp)){
                        winner = 1;
                        infoBox("Player one won the game!", "Winner!");
                    }else if(player2.equals(temp)){
                        winner = 2;
                        infoBox("Player two won the game!", "Winner!");
                    }else{
                        throw new Exception("Can't determine who won the game");
                    }
                    break;
                }
            }catch(Exception ignored){}
            if(checkIfFull(board)){
                infoBox("Cat got the game.", "Nobody Won");
                break;
            }
            if(player1.getIsTurn()){
                System.out.println("Player One's Turn");
                playerOneClick = true;
                while(true){
                    System.out.println();
                    if (coords[0] != -1 && coords[1] != -1) {
                        if(board[coords[0] - 1][coords[1] - 1].equals(SpaceType.blank)){
                            p[0].playHuman(board, coords[0] - 1, coords[1] - 1);
                            break;
                        }
                    }
                }
                playerOneClick = false;
                coords[0] = -1; coords[1] = -1;
                player1.setIsTurn(false);
                player2.setIsTurn(true);
            }else if(player2.getIsTurn()){
                System.out.println("Player Two's Turn");
                playerTwoClick = true;
                while(true){
                    System.out.println();
                    if (coords[0] != -1 && coords[1] != -1) {
                        if(board[coords[0] - 1][coords[1] - 1].equals(SpaceType.blank)){
                            p[1].playHuman(board, coords[0] - 1, coords[1] - 1);
                            break;
                        }
                    }
                }
                playerTwoClick = false;
                coords[0] = -1; coords[1] = -1;
                player2.setIsTurn(false);
                player1.setIsTurn(true);
            }
        }while(true);
        int dialogButton = JOptionPane.YES_NO_OPTION;
        dialogButton = JOptionPane.showConfirmDialog(null,
                "Would you like to play again?",
                "Play Again?", dialogButton);
        if(dialogButton == JOptionPane.NO_OPTION){
            System.exit(0);
        }else{
            player1.setIsTurn(false);
            player2.setIsTurn(false);
            gameLoop();
        }
        clearBoard();
    }

    public void clearBoard(){
        for(SpaceType[] spaceTypes : board) {
            Arrays.fill(spaceTypes, SpaceType.blank);
        }
    }

    private boolean checkIfFull(SpaceType[][] board){
        for(SpaceType[] row : board){
            for(SpaceType space : row){
                if(space.equals(SpaceType.blank)){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Gets the winner of the board (if there is one)
     * @param p1Type SpaceType of the first player
     * @param p2Type SpaceType of the second player
     * @param p Array of players - p[0] == Player1, p[1] == Player2
     * @return Winning Player
     */
    private Player checkIfWinner(SpaceType p1Type, SpaceType p2Type, Player[] p) throws Exception {
        //ROWS CHECK
        for(SpaceType[] row : board){
            if(!row[0].equals(SpaceType.blank)){
                if(row[0].equals(row[1]) && row[1].equals(row[2])){
                    if(row[0].equals(p1Type)){
                        return p[0];
                    }else if(row[0].equals(p2Type)){
                        return p[1];
                    }else{
                        throw new Exception("Rows Works, Player not Located");
                    }
                }
            }
        }
        //COLUMNS CHECK
        for(int i = 0; i < board.length; i++){
            boolean columnWorks = true;
            SpaceType temp = board[0][i];
            for(SpaceType[] row : board){
                if(temp != SpaceType.blank) {
                    if (!row[i].equals(temp)) {
                        columnWorks = false;
                        break;
                    }
                }else columnWorks = false;
            }
            if(columnWorks){
                if(board[0][i].equals(p1Type)){
                    return p[0];
                }else if(board[0][i].equals(p2Type)){
                    return p[1];
                }else throw new Exception("Column Works, Player not Located");
            }
        }
        //DIAGONALS CHECK
        if(!board[0][0].equals(SpaceType.blank)){
            if(board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2])){
                if(board[0][0].equals(p1Type)) return p[0];
                else if(board[0][0].equals(p2Type)) return p[1];
                else throw new Exception("Diagonal Works, Player not Located");
            }
        }
        if(!board[2][0].equals(SpaceType.blank)){
            if(board[2][0].equals(board[1][1]) && board[1][1].equals(board[0][2])){
                if(board[2][0].equals(p1Type)) return p[0];
                else if(board[2][0].equals(p2Type)) return p[1];
                else throw new Exception("Diagonal Works, Player not Located");
            }
        }
        return null;
    }

    public void setNumberOfPlayers(int players){
        try{
            Thread.sleep(50);
        }catch(InterruptedException ignored){}
        assert players == 1 || players == 2;
        this.players = players;
        if(players == 1){
            p[0] = new Player(0, this, SpaceType.X);
            p[1] = new Player(1, this, SpaceType.O);
        }else{
            p[0] = new Player(0, this, SpaceType.X);
            p[1] = new Player(0, this, SpaceType.O);
        }
        GameLoop.start();
    }

    public static void infoBox(String infoMessage, String titleBar){
        JOptionPane.showMessageDialog(null, infoMessage,
                "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }
}
