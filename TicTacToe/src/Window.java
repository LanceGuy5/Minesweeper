import javax.swing.*;
import java.awt.*;

/**
 * Window class
 * @see javax.swing.JFrame
 */
public class Window {

    public Window(int width, int height, String name, Component c){
        JFrame frame = new JFrame(name);
        Dimension d = new Dimension(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(d);
        frame.setMaximumSize(d);
        frame.setMinimumSize(d);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.add(c);
        frame.pack();
        frame.setVisible(true);
    }

}
