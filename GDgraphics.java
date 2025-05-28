import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public class GDgraphics extends JComponent {
    private int frameWidth;
    private int frameHeight;

    public GDgraphics(int w, int h) {
        frameWidth = w;
        frameHeight = h;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Clear background
        Graphics2D g2d = (Graphics2D) g;

        // Anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2d.setColor(new Color(30, 30, 30));
        g2d.fillRect(0, 0, frameWidth, frameHeight);

        // Ground
        g2d.setColor(new Color(60, 180, 75));
        int groundHeight = 100;
        g2d.fillRect(0, frameHeight - groundHeight, frameWidth, groundHeight);

        // Player cube
        g2d.setColor(Color.CYAN);
        int cubeSize = 40;
        int cubeX = 100;
        int cubeY = frameHeight - groundHeight - cubeSize;
        g2d.fillRect(cubeX, cubeY, cubeSize, cubeSize);

        // Spike (triangle)
        g2d.setColor(Color.RED);
        Polygon spike = new Polygon();
        spike.addPoint(300, frameHeight - groundHeight);        // bottom left
        spike.addPoint(330, frameHeight - groundHeight);        // bottom right
        spike.addPoint(315, frameHeight - groundHeight - 30);   // top
        g2d.fillPolygon(spike);
    }
}
