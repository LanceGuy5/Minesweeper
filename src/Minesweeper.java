import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;

public class Minesweeper extends MouseAdapter{

    public static final int BORDER = 100;
//    public static final int DISTANCE_BETWEEN = 20; // 30 x 30 grid
    public static final int BOMB_AMOUNT = 150;

    public boolean[][] boardClicked;
    public spaceType[][] boardSpaces;
    public Space[][] board;

    //COLORS
    Color customRed = new Color(211,50,49);
    Color customOrange = new Color(188,110,0);
//    Color customYellow = new Color(228,186,0);
    Color customGreen = new Color(59,143,61);
    Color customTurquoise = new Color(113,193,199);
    Color customBlue = new Color(25,118,210);
    Color customPurple = new Color(123,31,162);
    Color customViolet = new Color(223,118,255);
    Color board1 = new Color(215, 184, 153);
    Color board2 = new Color(229, 194, 159);
    Color grass1 = new Color(170, 215, 81);
    Color grass2 = new Color(162, 209, 73);
    Color betweenClickedAndNot = new Color(31, 71, 32);

    //Images
    BufferedImageLoader b = new BufferedImageLoader();
    BufferedImage bomb = null;
    BufferedImage flag = null;
    Thread loadBomb = new Thread(() -> bomb = b.loadImage("/bomb.png"));
    Thread loadFlag = new Thread(() -> flag = b.loadImage("/flag.png"));

    public ArrayList<int[]> locationsAndNumbers;

    Font f1 = new Font("Helvetica", Font.BOLD, 18);

    Thread t = new Thread(() -> {
        populateBoard();
        populateBombs(BOMB_AMOUNT);
        establishFinalBoard();
    });

    public enum spaceType{
        BOMB,
        CLEAR
    }

    public Minesweeper(){
        locationsAndNumbers = new ArrayList<>();
        boardClicked = new boolean[30][30];
        boardSpaces = new spaceType[30][30];
        board = new Space[30][30];
        t.start();
        loadBomb.start();
        loadFlag.start();
    }

    public void tick(){

    }

    //TODO THOUGHTS:
    //If one clicks on a bomb, it fills the space with red -> inserts bomb picture
    //All of the other bombs begin to show throughout the board
    //Any misplaced flags go away and a big red X shows up on the spot

    //TODO Make a method to determine if the player wins or not
    public void render(Graphics g){
        fillBoard(g);
        drawBoard(g);
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                if(board[i][j] == null) return;
                if(board[i][j].getClicked()){
                    if(!board[i][j].getSpaceType().equals(spaceType.BOMB)) {
                        int tCoord = board[i][j].getNum();
                        g.setFont(f1);
                        if(tCoord == 0) g.setColor(customRed); //TODO Remove when bomb images work -> don't render 0's
                        else if(tCoord == 1) g.setColor(customBlue);
                        else if(tCoord == 2) g.setColor(customGreen);
                        else if(tCoord == 3) g.setColor(customRed);
                        else if(tCoord == 4) g.setColor(customPurple);
                        else if(tCoord == 5) g.setColor(customOrange);
                        else if(tCoord == 6) g.setColor(customTurquoise);
                        else if(tCoord == 7) g.setColor(customViolet);
                        else if(tCoord == 8) g.setColor(Color.LIGHT_GRAY);
                        g.drawString("" + board[i][j].getNum(), i * 20 + 105, j * 20 + 116);
                    }
                }else
                    if(board[i][j].getFlagged()) g.drawImage(flag, i * 20 + 100, j * 20 + 100, null);
            }
        }
        g.setColor(betweenClickedAndNot);
        drawBorders(g);
    }

    public void mouseReleased(MouseEvent e){
        int x = e.getX(), y = e.getY();
        int[] coords = translateClickToCoordinate(x, y);
        if(SwingUtilities.isLeftMouseButton(e)) {
            try {
                if (!board[coords[0]][coords[1]].getClicked()) {
                    if(!board[coords[0]][coords[1]].getFlagged()) {
                        switchSpot(coords);
                        if (board[coords[0]][coords[1]].getSpaceType().equals(spaceType.BOMB)) {
                            //TODO end the game - below is a temporary fix
                            System.out.println("HIT A BOMB");
                        } else {
                            int temp = board[coords[0]][coords[1]].getNum();
                            locationsAndNumbers.add(new int[]{coords[0], coords[1], temp});
                            if (temp == 0) clearZeroValues(coords);
                        }
                    }
                }
            } catch (IndexOutOfBoundsException ignored) {}
        }else if(SwingUtilities.isRightMouseButton(e)){
            try{
                if(!board[coords[0]][coords[1]].getClicked())
                    board[coords[0]][coords[1]].setFlagged(!board[coords[0]][coords[1]].getFlagged());
            }catch(IndexOutOfBoundsException ignored){}
        }
    }

    public void drawBorders(Graphics g){
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++) {
                Space s = board[i][j];
                if (!s.getClicked()) continue;
                try {
                    if (!board[i - 1][j - 1].getClicked())
                        g.fillRect(i * 20 + 100 - 1, j * 20 + 100 - 1, 2, 2);
                } catch (IndexOutOfBoundsException ignored) {}
                try {
                    if (!board[i - 1][j].getClicked())
                        g.fillRect(i * 20 + 100 - 1, j * 20 + 100 - 1, 2, 20);
                } catch (IndexOutOfBoundsException ignored) {}
                try {
                    if (!board[i - 1][j + 1].getClicked())
                        g.fillRect(i * 20 + 100 - 1, j * 20 + 100 - 1 + 20, 2, 2);
                } catch (IndexOutOfBoundsException ignored) {}
                try {
                    if (!board[i][j + 1].getClicked())
                        g.fillRect(i * 20 + 100 - 1, j * 20 + 100 - 1 + 20, 20, 2);
                } catch (IndexOutOfBoundsException ignored) {}
                try {
                    if (!board[i + 1][j + 1].getClicked())
                        g.fillRect(i * 20 + 100 - 1 + 20, j * 20 + 100 - 1 + 20, 2, 2);
                } catch (IndexOutOfBoundsException ignored) {}
                try {
                    if (!board[i + 1][j].getClicked())
                        g.fillRect(i * 20 + 100 - 1 + 20, j * 20 + 100 - 1, 2, 20);
                } catch (IndexOutOfBoundsException ignored) {}
                try {
                    if (!board[i + 1][j - 1].getClicked())
                        g.fillRect(i * 20 + 100 - 1 + 20, j * 20 + 100 - 1, 2, 2);
                } catch (IndexOutOfBoundsException ignored) {}
                try {
                    if (!board[i][j - 1].getClicked())
                        g.fillRect(i * 20 + 100 - 1, j * 20 + 100 - 1, 20, 2);
                } catch (IndexOutOfBoundsException ignored) {}
            }
        }
    }

    public void clearZeroValues(int[] coords){
        Thread clearZeros = new Thread(() -> {
        ArrayList<int[]> coordsList = new ArrayList<>();
        try {
            if (!board[coords[0] - 1][coords[1] - 1].getClicked()
                    && !board[coords[0] - 1][coords[1] - 1].getFlagged())
                coordsList.add(new int[]{coords[0] - 1, coords[1] - 1});
        } catch (IndexOutOfBoundsException ignored) {}
        try {
            if (!board[coords[0] - 1][coords[1]].getClicked()
                    && !board[coords[0] - 1][coords[1]].getFlagged())
                coordsList.add(new int[]{coords[0] - 1, coords[1]});
        } catch (IndexOutOfBoundsException ignored) {}
        try {
            if (!board[coords[0] - 1][coords[1] + 1].getClicked()
                    && !board[coords[0] - 1][coords[1] + 1].getFlagged())
                coordsList.add(new int[]{coords[0] - 1, coords[1] + 1});
        } catch (IndexOutOfBoundsException ignored) {}
        try {
            if (!board[coords[0]][coords[1] + 1].getClicked()
                    && !board[coords[0]][coords[1] + 1].getFlagged())
                coordsList.add(new int[]{coords[0], coords[1] + 1});
        } catch (IndexOutOfBoundsException ignored) {}
        try {
            if (!board[coords[0] + 1][coords[1] + 1].getClicked()
                    && !board[coords[0] + 1][coords[1] + 1].getFlagged())
                coordsList.add(new int[]{coords[0] + 1, coords[1] + 1});
        } catch (IndexOutOfBoundsException ignored) {}
        try {
            if (!board[coords[0] + 1][coords[1]].getClicked()
                    && !board[coords[0] + 1][coords[1]].getFlagged())
                coordsList.add(new int[]{coords[0] + 1, coords[1]});
        } catch (IndexOutOfBoundsException ignored) {}
        try {
            if (!board[coords[0] + 1][coords[1] - 1].getClicked()
                    && !board[coords[0] + 1][coords[1] - 1].getFlagged())
                coordsList.add(new int[]{coords[0] + 1, coords[1] - 1});
        } catch (IndexOutOfBoundsException ignored) {}
        try {
            if (!board[coords[0]][coords[1] - 1].getClicked()
                    && !board[coords[0]][coords[1] - 1].getFlagged())
                coordsList.add(new int[]{coords[0], coords[1] - 1});
        } catch (IndexOutOfBoundsException ignored) {}
        for(int[] tCoord : coordsList){
            if(board[tCoord[0]][tCoord[1]].getNum() == 0){
                board[tCoord[0]][tCoord[1]].setClicked(true);
                locationsAndNumbers.add(new int[]{tCoord[0], tCoord[1], 0});
                try {
                    Thread.sleep(20);
                }catch(InterruptedException ignored){}
                clearZeroValues(tCoord);
            }else{
                board[tCoord[0]][tCoord[1]].setClicked(true);
                locationsAndNumbers.add(new int[]{tCoord[0], tCoord[1], 0});
            }
        }
        });
        clearZeros.start();
    }

    public int calculateNumberInsideSquare(int[] coords){
        int ret = 0;
        try{
            if(board[coords[0] - 1][coords[1] - 1].getSpaceType().equals(spaceType.BOMB)) ret++;
        }catch(IndexOutOfBoundsException ignored){}
        try{
            if(board[coords[0] - 1][coords[1]].getSpaceType().equals(spaceType.BOMB)) ret++;
        }catch(IndexOutOfBoundsException ignored){}
        try{
            if(board[coords[0] - 1][coords[1] + 1].getSpaceType().equals(spaceType.BOMB)) ret++;
        }catch(IndexOutOfBoundsException ignored){}
        try{
            if(board[coords[0]][coords[1] + 1].getSpaceType().equals(spaceType.BOMB)) ret++;
        }catch(IndexOutOfBoundsException ignored){}
        try{
            if(board[coords[0] + 1][coords[1] + 1].getSpaceType().equals(spaceType.BOMB)) ret++;
        }catch(IndexOutOfBoundsException ignored){}
        try{
            if(board[coords[0] + 1][coords[1]].getSpaceType().equals(spaceType.BOMB)) ret++;
        }catch(IndexOutOfBoundsException ignored){}
        try{
            if(board[coords[0] + 1][coords[1] - 1].getSpaceType().equals(spaceType.BOMB)) ret++;
        }catch(IndexOutOfBoundsException ignored){}
        try{
            if(board[coords[0]][coords[1] - 1].getSpaceType().equals(spaceType.BOMB)) ret++;
        }catch(IndexOutOfBoundsException ignored){}
        return ret;
    }

    public void establishFinalBoard(){
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++) {
                board[i][j] = new Space(boardSpaces[i][j], boardClicked[i][j], -1, false);
            }
        }
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                board[i][j].setNum(calculateNumberInsideSquare(new int[]{i, j}));
            }
        }
    }

    //TODO MAKE WEIGHTED BOMBS POSSIBLY
    //TODO Make it so that the place a player clicks cannot be a bomb
    public void populateBombs(int num){
        for (spaceType[] boardSpace : boardSpaces) Arrays.fill(boardSpace, spaceType.CLEAR);
        LinkedList<Integer> list = new LinkedList<>();
        for(int i = 0; i < num; i++){
            while(true) {
                int n = (int)(Math.random() * 900);
                if(!list.contains(n)){
                    list.add(n);
                    break;
                }
            }
        }
        for (Integer integer : list) {
            int[] coords = turnSpaceTypeLocationIntoCoordinate(integer);
            boardSpaces[coords[0]][coords[1]] = spaceType.BOMB;
        }
    }

    public int[] turnSpaceTypeLocationIntoCoordinate(int location){
        int x = location % 30;
        int y = location / 30;
        return new int[]{x, y};
    }

    public void drawBoard(Graphics g){
        g.setColor(Color.black);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3));
        g.drawLine(BORDER, BORDER, BORDER, Game.HEIGHT - BORDER);
        g.drawLine(BORDER, BORDER, Game.WIDTH - BORDER, BORDER);
        g.drawLine(BORDER, Game.HEIGHT - BORDER, Game.WIDTH - BORDER, Game.HEIGHT - BORDER);
        g.drawLine(Game.WIDTH - BORDER, BORDER, Game.WIDTH - BORDER, Game.HEIGHT - BORDER);
    }

    public void fillBoard(Graphics g){
        for(int i = 0; i < boardClicked.length; i++){
            for(int j = 0; j < boardClicked[i].length; j++){
                fillSpot(g, new int[]{i, j});
            }
        }
    }

    public void switchSpot(int[] coords){
        board[coords[0]][coords[1]].setClicked(!board[coords[0]][coords[1]].getClicked());
    }

    public void fillSpot(Graphics g, int[] coords){
        if(coords == null) return;
        if(board[coords[0]][coords[1]] == null) return;
        if(board[coords[0]][coords[1]].getClicked()){
            if(coords[1] % 2 == 0){
                if(coords[0] % 2 == 1) g.setColor(board1);
                else g.setColor(board2);
            }
            else{
                if(coords[0] % 2 == 1) g.setColor(board2);
                else g.setColor(board1);
            }
        }else{
            if(coords[1] % 2 == 0){
                if(coords[0] % 2 == 1) g.setColor(grass1);
                else g.setColor(grass2);
            }
            else{
                if(coords[0] % 2 == 1) g.setColor(grass2);
                else g.setColor(grass1);
            }
        }
        g.fillRect(coords[0] * 20 + 100, coords[1] * 20 + 100, 20, 20);
    }

    public void populateBoard(){
        for(boolean[] b : boardClicked){
            Arrays.fill(b, false);
        }
    }

    public int[] translateClickToCoordinate(int x, int y){
        int realX = x - 100, realY = y - 100;
        return new int[]{realX / 20, realY / 20};
    }
}

class Space{

    private Minesweeper.spaceType s;
    private boolean c;
    private int n;
    private boolean f;

    public Space(Minesweeper.spaceType s, boolean c, int n, boolean f){
        this.s = s;
        this.c = c;
        this.n = n;
        this.f = f;
    }

    public Minesweeper.spaceType getSpaceType(){
        return s;
    }

    public void setSpaceType(Minesweeper.spaceType s){
        this.s = s;
    }

    public boolean getClicked(){
        return c;
    }

    public void setClicked(boolean c){
        this.c = c;
    }

    public int getNum(){
        return n;
    }

    public void setNum(int n){
        this.n = n;
    }

    public boolean getFlagged(){
        return f;
    }

    public void setFlagged(boolean f){
        this.f = f;
    }

}
