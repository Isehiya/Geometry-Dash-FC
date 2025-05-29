import javax.swing.*;

public class GDmain {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Geometry Dash FC");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setResizable(false);

        GDgraphics panel = new GDgraphics();
        frame.add(panel);
        frame.setVisible(true);

        panel.startGameLoop();
    }
}
