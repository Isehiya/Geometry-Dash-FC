import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.io.IOException;

public class GDmain {
    public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
        JFrame frame = new JFrame("Geometry Dash FC");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(   900, 700);
        frame.setResizable(false);
        GDgraphics panel = new GDgraphics();
        frame.add(panel);
        frame.setVisible(true);
        panel.startGameLoop();
    }
}