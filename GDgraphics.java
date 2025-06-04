import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class GDgraphics extends JPanel implements KeyListener {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final int FPS_DELAY = 16;
    public static final int GRAVITY = 1;
    public static final int JUMP_VELOCITY = -15;
    public static final int GROUND_Y = 500;
    private static final double ROT_SPEED = 7.0;

    public static final int SPIKE_HITBOX_WIDTH = 3;
    public static final int SPIKE_HITBOX_HEIGHT = 5;

    public static int playerSize = 40;
    public static int scrollSpeed = 5;

    public static Rectangle player;
    public Rectangle[] blocks = new Rectangle[10000];
    public Rectangle[] spikes = new Rectangle[10000];

    private int velocityY = 0;
    private boolean isJump = false;
    private double rotation = 0;

    Clip backgroundMusic1;

    private Image playerImg;
    private Image blockImg;
    private Image bgImg;
    private Image spike;

    private Image halfSpeedPortal, speedPortal1, speedPortal2, speedPortal3, speedPortal4;

    private int bgOffsetX = 0;
    private boolean running = false;

    public GDgraphics() throws UnsupportedAudioFileException, IOException {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);

        MediaTracker tracker = new MediaTracker(this);
        halfSpeedPortal = Toolkit.getDefaultToolkit().getImage("GDspeedportal0.5.gif");
        tracker.addImage(halfSpeedPortal, 0);
        speedPortal1 = Toolkit.getDefaultToolkit().getImage("GDspeedportal1.gif");
        tracker.addImage(speedPortal1, 1);
        speedPortal2 = Toolkit.getDefaultToolkit().getImage("GDspeedportal2.gif");
        tracker.addImage(speedPortal2, 2);
        speedPortal3 = Toolkit.getDefaultToolkit().getImage("GDspeedportal3.gif");
        tracker.addImage(speedPortal3, 3);
        speedPortal4 = Toolkit.getDefaultToolkit().getImage("GDspeedportal4.gif");
        tracker.addImage(speedPortal4, 4);
        playerImg = Toolkit.getDefaultToolkit().getImage("GDdefaulticon.png");
        tracker.addImage(playerImg, 5);
        blockImg = Toolkit.getDefaultToolkit().getImage("GDblock.png");
        tracker.addImage(blockImg, 6);
        bgImg = Toolkit.getDefaultToolkit().getImage("GDbackground.png");
        tracker.addImage(bgImg, 7);
        spike = Toolkit.getDefaultToolkit().getImage("GDspike.png");
        tracker.addImage(spike, 8);
        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        player = new Rectangle(50, GROUND_Y, playerSize, playerSize);

        try {
            AudioInputStream sound = AudioSystem.getAudioInputStream(new File("StereoMadness.wav"));
            backgroundMusic1 = AudioSystem.getClip();
            backgroundMusic1.open(sound);
        } catch (Exception e) {}

        backgroundMusic1.setFramePosition(0);
        backgroundMusic1.loop(Clip.LOOP_CONTINUOUSLY);

        spikes[3] = new Rectangle(1000, 500, 10, 20);
    }

    public void startGameLoop() {
        running = true;
        Thread loop = new Thread(() -> {
            while (running) {
                updateGame();
                repaint();
                try {
                    Thread.sleep(FPS_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        loop.start();
    }

    private void updateGame() {
        if (bgImg != null) {
            bgOffsetX = (bgOffsetX - scrollSpeed) % bgImg.getWidth(this);
        }

        if (spikes[3] != null && player.intersects(spikes[3])) {
            System.exit(0);
        }

        if (spikes[3] != null) {
            spikes[3].x -= scrollSpeed;
        }

        velocityY += GRAVITY;
        player.y += velocityY;

        if (isJump) {
            rotation = (rotation + ROT_SPEED) % 360;
        }

        if (player.y > GROUND_Y) {
            player.y = GROUND_Y;
            velocityY = 0;
            isJump = false;
            if (rotation <= 45)
                rotation = 0;
            else if (rotation > 45 && rotation <= 135)
                rotation = 90;
            else if (rotation > 135 && rotation <= 225)
                rotation = 180;
            else if (rotation > 225 && rotation <= 315)
                rotation = 270;
            else
                rotation = 0;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Draw scrolling background
        if (bgImg != null) {
            int bwBg = bgImg.getWidth(this);
            for (int i = -1; i <= getWidth() / bwBg + 1; i++) {
                g2.drawImage(bgImg, bgOffsetX + i * bwBg, 0, bwBg, getHeight(), null);
            }
        }

        // Draw spikes
        for (int i = 0; i < spikes.length; i++) {
            if (spikes[i] != null) {
                int spikeHeight = spike.getHeight(this) / 3;
                int spikeWidth = spike.getWidth(this) / 3;
                g2.drawImage(spike, spikes[i].x, spikes[i].y, spikeWidth, spikeHeight, this);
                g2.setColor(Color.RED);
                g2.drawRect(spikes[i].x, spikes[i].y, spikes[i].width, spikes[i].height);
            }
        }

        // Draw floor tiles
        if (blockImg != null) {
            int nativeBw = blockImg.getWidth(this);
            int nativeBh = blockImg.getHeight(this);
            int gapHeight = getHeight() - (GROUND_Y + playerSize);
            int scaledBh = gapHeight / 3;
            int scaledBw = nativeBw * scaledBh / nativeBh;
            int startY = GROUND_Y + playerSize;
            int offsetX = bgOffsetX % scaledBw;
            for (int row = 0; row < 3; row++) {
                int y = startY + row * scaledBh;
                for (int i = -1; i <= getWidth() / scaledBw + 1; i++) {
                    g2.drawImage(blockImg, offsetX + i * scaledBw, y, scaledBw, scaledBh, null);
                }
            }
        }

        // Draw and rotate player icon
        if (playerImg != null) {
            double cx = player.x + player.width / 2.0;
            double cy = player.y + player.height / 2.0;
            g2.rotate(Math.toRadians(rotation), cx, cy);
            g2.drawImage(playerImg, player.x, player.y, player.width, player.height, null);
            g2.rotate(-Math.toRadians(rotation), cx, cy); // reset rotation
        } else {
            g2.setColor(Color.BLUE);
            g2.fillRect(player.x, player.y, player.width, player.height);
        }

        // Debug info
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(10, 10, 200, 125);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
        int line = 0;
        g2.drawString(String.format("player.x   = %d", player.x), 15, 30 + line * 15); line++;
        g2.drawString(String.format("player.y   = %d", player.y), 15, 30 + line * 15); line++;
        g2.drawString("velocityY  = " + velocityY, 15, 30 + line * 15); line++;
        g2.drawString("isJump     = " + isJump, 15, 30 + line * 15); line++;
        g2.drawString(String.format("rotation   = %.1f", rotation), 15, 30 + line * 15); line++;
        g2.drawString("bgOffsetX  = " + bgOffsetX, 15, 30 + line * 15);
    }

    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if ((code == KeyEvent.VK_SPACE || code == KeyEvent.VK_UP) && !isJump) {
            velocityY = JUMP_VELOCITY;
            isJump = true;
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}
