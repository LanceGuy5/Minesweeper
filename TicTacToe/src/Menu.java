import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Menu extends MouseAdapter{

    TicTacToe t;
    GameDirector gd;

    Font f = new Font("Helvetica", Font.BOLD, 64);
    Font f2 = new Font("Helvetica", Font.PLAIN, 24);

    public Menu(TicTacToe t, GameDirector gd){
        this.t = t;
        this.gd = gd;
    }

    public void mouseReleased(MouseEvent e){
        int mouseX = e.getX();
        int mouseY = e.getY();
        if(TicTacToe.state.equals(TicTacToe.GAMESTATE.Menu)) {
            if (clickWithinBounds(mouseX, mouseY, 150, 200, 650, 300)) {
                TicTacToe.state = TicTacToe.GAMESTATE.Game;
            } else if (clickWithinBounds(mouseX, mouseY, 150, 350, 650, 450)) {
                System.exit(0);
            }
        }
    }

    public void tick(){

    }

    public void render(Graphics g){
        g.setFont(f);
        g.setColor(Color.black);
        g.drawString("TicTacToe", 240, 80);
        g.setFont(f2);
        g.drawString("By: Lance Hartman", 295, 120);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(5));
        g.drawRect(150, 200, 500, 100);
        g.drawRect(150, 350, 500, 100);
        g2d.setStroke(new BasicStroke(1));
        g.setFont(new Font("Helvetica", Font.PLAIN, 56));
        g.drawString("Play", 340, 270);
        g.drawString("Quit", 340, 420);
    }

    public boolean clickWithinBounds(int mouseX, int mouseY, int x1, int y1, int x2, int y2){
        if(mouseX > x1 && mouseX < x2){
            return mouseY > y1 && mouseY < y2;
        }else{
            return false;
        }
    }

}
