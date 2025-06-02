import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class GDgraphics extends JPanel implements KeyListener {
    // --- Constants
    public static final int WIDTH           = 800;
    public static final int HEIGHT          = 600;
    public static final int FPS_DELAY       = 16;      // ~60 FPS
    public static final int GRAVITY         = 1;
    public  static final int JUMP_VELOCITY   = -15;
    public static final int PLAYER_SIZE     = 50;
    public static final int GROUND_Y        = 500;     // where player lands
    public static final int BG_SCROLL_SPEED = 2;
    private static final double ROT_SPEED    = 5.0;     // deg/frame

    // --- State
    private Rectangle2D.Double player;
    private int velocityY    = 0;
    private boolean isJump   = false;
    private double rotation  = 0;

    // --- Images
    private Image playerImg;
    private Image blockImg;
    private Image bgImg;

    // --- Scroll
    private int bgOffsetX = 0;
    private boolean running = false;

    public GDgraphics() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);

        player = new Rectangle2D.Double(50, GROUND_Y, PLAYER_SIZE, PLAYER_SIZE);

        try {
            playerImg = ImageIO.read(new File("GDdefaulticon.png"));
            blockImg  = ImageIO.read(new File("GDblock.png"));
            bgImg     = ImageIO.read(new File("GDbackground.png"));
        } catch (IOException e) {
            System.err.println("Error loading images—ensure GDdefaulticon.png, GDblock.png, and GDbackground.png are in your working dir.");
        }
    }

    public void startGameLoop() {
        running = true;
        Thread loop = new Thread(new Runnable() {
            public void run() {
                while (running) {
                    updateGame();
                    repaint();
                    try {
                        Thread.sleep(FPS_DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        loop.start();
    }

    private void updateGame() {
        // Scroll background
        if (bgImg != null) {
            bgOffsetX = (bgOffsetX - BG_SCROLL_SPEED) % bgImg.getWidth(this);
        }

        // Apply gravity
        velocityY += GRAVITY;
        player.y   += velocityY;

        // Rotate mid-air
        if (isJump) {
            rotation = (rotation + ROT_SPEED) % 360;
        }

        // Land on ground
        if (player.y > GROUND_Y) {
            player.y   = GROUND_Y;
            velocityY  = 0;
            isJump     = false;
            rotation   = 0;
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // 1) Draw scrolling background
        if (bgImg != null) {
            int bwBg = bgImg.getWidth(this);
            for (int i = -1; i <= getWidth() / bwBg + 1; i++) {
                g2.drawImage(bgImg, bgOffsetX + i * bwBg, 0, bwBg, getHeight(), null);
            }
        }

        // 2) Draw three stacked floor tiles exactly filling gap under player
        if (blockImg != null) {
            int nativeBw = blockImg.getWidth(this);
            int nativeBh = blockImg.getHeight(this);
            int gapHeight = getHeight() - (GROUND_Y + PLAYER_SIZE);
            int scaledBh = gapHeight / 3;
            int scaledBw = nativeBw * scaledBh / nativeBh;
            int startY = GROUND_Y + PLAYER_SIZE;  // top of top block
            int offsetX = bgOffsetX % scaledBw;
            for (int row = 0; row < 3; row++) {
                int y = startY + row * scaledBh;
                for (int i = -1; i <= getWidth() / scaledBw + 1; i++) {
                    g2.drawImage(blockImg, offsetX + i * scaledBw, y, scaledBw, scaledBh, null);
                }
            }
        }

        // 3) Draw and rotate player icon
        if (playerImg != null) {
            AffineTransform old = g2.getTransform();
            double cx = player.x + PLAYER_SIZE / 2.0;
            double cy = player.y + PLAYER_SIZE / 2.0;
            g2.rotate(Math.toRadians(rotation), cx, cy);
            g2.drawImage(playerImg, (int) player.x, (int) player.y, PLAYER_SIZE, PLAYER_SIZE, null);
            g2.setTransform(old);
        } else {
            g2.setColor(Color.BLUE);
            g2.fill(player);
        }

        // 4) Debug overlay
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(10, 10, 200, 125);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
        int line = 0;
        g2.drawString(String.format("player.x   = %.1f", player.x), 15, 30 + line * 15); line++;
        g2.drawString(String.format("player.y   = %.1f", player.y), 15, 30 + line * 15); line++;
        g2.drawString("velocityY  = " + velocityY, 15, 30 + line * 15); line++;
        g2.drawString("isJump     = " + isJump, 15, 30 + line * 15); line++;
        g2.drawString(String.format("rotation   = %.1f", rotation), 15, 30 + line * 15); line++;
        g2.drawString("bgOffsetX  = " + bgOffsetX, 15, 30 + line * 15);
    }

    // --- KeyListener methods
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if ((code == KeyEvent.VK_SPACE || code == KeyEvent.VK_UP) && !isJump) {
            velocityY = JUMP_VELOCITY;
            isJump    = true;
        }
    }
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}