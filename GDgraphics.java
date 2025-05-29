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
    private static final int WIDTH           = 800;
    private static final int HEIGHT          = 600;
    private static final int FPS_DELAY       = 16;      // ~60 FPS
    private static final int GRAVITY         = 1;
    private static final int JUMP_VELOCITY   = -15;
    private static final int PLAYER_SIZE     = 50;
    private static final int GROUND_Y        = 500;     // where player lands
    private static final int BG_SCROLL_SPEED = 2;
    private static final double ROT_SPEED    = 5.0;     // deg/frame

    // --- State
    private Rectangle2D.Double player;
    private int velocityY    = 0;
    private boolean isJump   = false;
    private double rotation  = 0;

    // --- Images
    private BufferedImage playerImg;
    private BufferedImage blockImg;
    private BufferedImage bgImg;

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
            System.err.println(
                    "Error loading images—make sure GDdefaulticon.png, GDblock.png, GDbackground.png are in your working dir."
            );
        }
    }

    public void startGameLoop() {
        running = true;
        new Thread(() -> {
            while (running) {
                updateGame();
                repaint();
                try { Thread.sleep(FPS_DELAY); }
                catch (InterruptedException ignored) {}
            }
        }).start();
    }

    private void updateGame() {
        // background scroll
        if (bgImg != null) {
            bgOffsetX = (bgOffsetX - BG_SCROLL_SPEED) % bgImg.getWidth();
        }

        // gravity
        velocityY += GRAVITY;
        player.y   += velocityY;

        // rotate in air
        if (isJump) {
            rotation = (rotation + ROT_SPEED) % 360;
        }

        // land
        if (player.y > GROUND_Y) {
            player.y    = GROUND_Y;
            velocityY   = 0;
            isJump      = false;
            rotation    = 0;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // 1) draw scrolling background
        if (bgImg != null) {
            int bw = bgImg.getWidth();
            for (int i = -1; i <= getWidth() / bw + 1; i++) {
                g2.drawImage(bgImg, bgOffsetX + i * bw, 0, bw, getHeight(), null);
            }
        }

        // 2) tile GDblock.png for the floor
        if (blockImg != null) {
            int bw = blockImg.getWidth();
            int bh = blockImg.getHeight();
            int floorY = GROUND_Y + PLAYER_SIZE;
            for (int y = floorY; y < getHeight(); y += bh) {
                for (int x = 0; x <= getWidth() / bw; x++) {
                    g2.drawImage(blockImg, x * bw, y, null);
                }
            }
        }

        // 3) draw & rotate player icon
        if (playerImg != null) {
            AffineTransform old = g2.getTransform();
            double cx = player.x + PLAYER_SIZE/2.0;
            double cy = player.y + PLAYER_SIZE/2.0;
            g2.rotate(Math.toRadians(rotation), cx, cy);
            g2.drawImage(playerImg,
                    (int)player.x, (int)player.y,
                    PLAYER_SIZE, PLAYER_SIZE,
                    null);
            g2.setTransform(old);
        } else {
            g2.setColor(Color.BLUE);
            g2.fill(player);
        }

        // 4) debug overlay
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(10, 10, 180, 110);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
        int line = 0;
        g2.drawString(String.format("player.x   = %.1f", player.x), 15, 30 + line*15); line++;
        g2.drawString(String.format("player.y   = %.1f", player.y), 15, 30 + line*15); line++;
        g2.drawString("velocityY  = " + velocityY,          15, 30 + line*15); line++;
        g2.drawString("isJump     = " + isJump,             15, 30 + line*15); line++;
        g2.drawString(String.format("rotation   = %.1f", rotation), 15, 30 + line*15); line++;
        g2.drawString("bgOffsetX  = " + bgOffsetX,          15, 30 + line*15);
    }

    // --- KeyListener
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !isJump) {
            velocityY = JUMP_VELOCITY;
            isJump    = true;
        }
    }
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
