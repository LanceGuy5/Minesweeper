import javax.swing.*;
import java.awt.*;

public class Window {

    public Window(int width, int height, String title, Component c){
        Dimension d = new Dimension(width, height);

        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(d);
        frame.setMaximumSize(d);
        frame.setMinimumSize(d);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.add(c);
        frame.pack();
        frame.requestFocus();
//        frame.setIconImage();
        frame.setVisible(true);
    }
}
