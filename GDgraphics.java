import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public class GDgraphics extends JComponent {
    private int frameWidth;
    private int frameHeight;
    private Image playerImg;
    private Image spikeImg;

    public GDgraphics(int w, int h) {
        frameWidth = w;
        frameHeight = h;

        // Load images from project folder
        playerImg = Toolkit.getDefaultToolkit().getImage("GDdefaulticon.png");
        spikeImg = Toolkit.getDefaultToolkit().getImage("GDspike.png");
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
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

        // Draw player image
        int cubeSize = 40;
        int cubeX = 100;
        int cubeY = frameHeight - groundHeight - cubeSize;
        g2d.drawImage(playerImg, cubeX, cubeY, cubeSize, cubeSize, this);

        // Draw spike image
        int spikeSize = 40;
        int spikeX = 300;
        int spikeY = frameHeight - groundHeight - spikeSize;
        g2d.drawImage(spikeImg, spikeX, spikeY, spikeSize, spikeSize, this);
    }
}
