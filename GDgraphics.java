import java.awt.*;
import javax.swing.*;

public class GDgraphics extends JComponent {
    private int frameWidth;
    private int frameHeight;

    private Image playerImg;
    private Image spikeImg;
    private Image bgImg;

    private int bgX = 0;
    private int scrollSpeed = 2;

    public GDgraphics(int w, int h) {
        frameWidth = w;
        frameHeight = h;

        playerImg = Toolkit.getDefaultToolkit().getImage("GDdefaulticon.png");
        spikeImg = Toolkit.getDefaultToolkit().getImage("GDspike.png");
        bgImg = Toolkit.getDefaultToolkit().getImage("GDbackground.png");
    }

    public void updateGame() {
        // Move background
        bgX -= scrollSpeed;
        if (bgX <= -frameWidth) {
            bgX = 0;
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2d.drawImage(bgImg, bgX, 0, frameWidth, frameHeight, this);
        g2d.drawImage(bgImg, bgX + frameWidth, 0, frameWidth, frameHeight, this);

        // Ground
        int groundHeight = 100;
        g2d.setColor(new Color(60, 180, 75));
        g2d.fillRect(0, frameHeight - groundHeight, frameWidth, groundHeight);

        // Player cube
        int cubeSize = 40;
        int cubeX = 100;
        int cubeY = frameHeight - groundHeight - cubeSize;
        g2d.drawImage(playerImg, cubeX, cubeY, cubeSize, cubeSize, this);

        // Spike
        int spikeSize = 40;
        int spikeX = 300;
        int spikeY = frameHeight - groundHeight - spikeSize;
        g2d.drawImage(spikeImg, spikeX, spikeY, spikeSize, spikeSize, this);
    }
}
