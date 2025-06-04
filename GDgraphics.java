import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import javax.sound.sampled.*;
import java.util.*;

public class GDgraphics extends JPanel implements KeyListener {

    // --- Constants
    public static final int WIDTH           = 800;
    public static final int HEIGHT          = 600;
    public static final int FPS_DELAY       = 16;      // ~60 FPS
    public static final int GRAVITY         = 1;
    public static final int JUMP_VELOCITY   = -15;
    public static final int GROUND_Y        = 500;     // where player lands
    private static final double ROT_SPEED    = 10.0;     // deg/frame

    public static final int SPIKE_HITBOX_WIDTH = 3;
    public static final int SPIKE_HITBOX_HEIGHT = 5;

    public static int playerSize = 40;
    public static int scrollSpeed = 5;

    // --- State
    public static Rectangle2D.Double player;
    public ArrayList<Rectangle2D.Double> blocks = new ArrayList<>();
    public ArrayList<Rectangle2D.Double> spikes = new ArrayList<>();
    public ArrayList<Rectangle2D.Double> hitboxes = new ArrayList<>();

    private int velocityY    = 0;
    private boolean isJump   = false;
    private double rotation  = 0;

    public static int gameState = 1;

    Clip backgroundMusic1, backgroundMusic2;

    // --- Images
    private Image playerImg;
    private Image blockImg;
    private Image bgImg;
    private Image spike;

    private Image halfSpeedPortal, speedPortal1, speedPortal2, speedPortal3, speedPortal4;
    private Image logo, playButton;

    // --- Scroll
    private int bgOffsetX = 0;
    private boolean running = false;

    private int imageOffets1 = 40;



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
        blockImg  = Toolkit.getDefaultToolkit().getImage("GDblock.png");
        tracker.addImage(blockImg, 6);
        bgImg = Toolkit.getDefaultToolkit().getImage("GDbackground.png");
        tracker.addImage(bgImg, 7);
        spike = Toolkit.getDefaultToolkit().getImage("GDspike.png");
        tracker.addImage(spike, 8);
        logo = Toolkit.getDefaultToolkit().getImage("GDlogo.png");
        tracker.addImage(logo, 9);
        playButton = Toolkit.getDefaultToolkit().getImage("GDplaybutton.png");
        tracker.addImage(playButton,10);

        try{
            tracker.waitForAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        player = new Rectangle2D.Double(50, GROUND_Y, playerSize, playerSize);

        try {
            AudioInputStream StereoMadness = AudioSystem.getAudioInputStream(new File("StereoMadness.wav"));
            AudioInputStream MenuMusic = AudioSystem.getAudioInputStream(new File("GDmenumusic.wav"));

            if (gameState == 0) {
                backgroundMusic1 = AudioSystem.getClip();
                backgroundMusic1.open(MenuMusic);
            } else if (gameState == 1) {
                backgroundMusic2 = AudioSystem.getClip();
                backgroundMusic2.open(StereoMadness);
            }

        } catch (Exception e) {
            e.printStackTrace(); // Better than silent failure
        }

// Only start music if successfully loaded
        if (backgroundMusic1 != null) {
            backgroundMusic1.setFramePosition(0);
            backgroundMusic1.loop(Clip.LOOP_CONTINUOUSLY);
        }
        if (backgroundMusic2 != null) {
            backgroundMusic2.setFramePosition(0);
            backgroundMusic2.loop(Clip.LOOP_CONTINUOUSLY);
        }


        spikes.add(new Rectangle2D.Double(1000, 500, 10, 20));

        hitboxes.add(new Rectangle2D.Double( 1015, 510 , 7, 18));

    } // Constructor



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
        // Scroll background
        if (bgImg != null) {
            bgOffsetX = (bgOffsetX - scrollSpeed) % bgImg.getWidth(this);
        }
        if(gameState == 1) {
            if (player.intersects(hitboxes.getFirst())) {
                System.exit(0);
            }

            spikes.getFirst().x -= scrollSpeed;
            hitboxes.getFirst().x -= scrollSpeed;

            // Apply gravity
            velocityY += GRAVITY;
            player.y += velocityY;

            // Rotate mid-air
            if (isJump) {
                rotation = (rotation + ROT_SPEED) % 360;
            }

            // Land on ground
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
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;


        //------------------------------- Menu (Gamestate 0) ---------------------------------
        if (gameState == 0){
            if (bgImg != null) {
                int bwBg = bgImg.getWidth(this);
                for (int i = -1; i <= getWidth() / bwBg + 1; i++) {
                    g2.drawImage(bgImg, bgOffsetX + i * bwBg, 0, bwBg, getHeight(), null);
                }
            }
            if (logo != null) {
                int originalW = logo.getWidth(this);
                int originalH = logo.getHeight(this);

                if (originalW > 0 && originalH > 0) {
                    // Scale to 70% of screen width
                    int scaledW = (int)(WIDTH * 0.9);
                    int scaledH = (int)(originalH * (scaledW / (double)originalW)); // keep aspect ratio

                    // Center horizontally
                    int logoX = (WIDTH - scaledW + imageOffets1) / 2;
                    int logoY = 50; // small margin from the top

                    g2.drawImage(logo, logoX, logoY, scaledW, scaledH, this);
                }
            }
            if (playButton != null) {
                int originalW = playButton.getWidth(this);
                int originalH = playButton.getHeight(this);

                if (originalW > 0 && originalH > 0) {
                    // Scale to 20% of screen width
                    int scaledW = (int)(WIDTH * 0.2);
                    int scaledH = (int)(originalH * (scaledW / (double)originalW)); // keep aspect ratio

                    // Center of screen
                    int playX = (WIDTH - scaledW + imageOffets1) / 2;
                    int playY = (HEIGHT - scaledH + 150) / 2;

                    g2.drawImage(playButton, playX, playY, scaledW, scaledH, this);
                }
            }




        }

        //--------------------------- Stereo Madness (Gamestate 1) ---------------------------
        else if (gameState == 1) {
            if (bgImg != null) {
                int bwBg = bgImg.getWidth(this);
                for (int i = -1; i <= getWidth() / bwBg + 1; i++) {
                    g2.drawImage(bgImg, bgOffsetX + i * bwBg, 0, bwBg, getHeight(), null);
                }
            }

            for (int i = 0; i < spikes.size(); i++) {
                int spikeHeight = spike.getHeight(this);
                int spikeWidth = spike.getWidth(this);
                spikeWidth /= 3;
                spikeHeight /= 3;
                if (spikes.get(i) != null) {
                    g2.drawImage(spike, (int) spikes.get(i).x, (int) spikes.get(i).y, spikeWidth, spikeHeight, this);
                    g2.setColor(Color.RED);
                    g2.drawRect((int) spikes.get(i).x + 15, (int) spikes.get(i).y + 10, 8, 18);
                }
            }

            // 2) Draw three stacked floor tiles exactly filling gap under player
            if (blockImg != null) {
                int nativeBw = blockImg.getWidth(this);
                int nativeBh = blockImg.getHeight(this);
                int gapHeight = getHeight() - (GROUND_Y + playerSize);
                int scaledBh = gapHeight / 3;
                int scaledBw = nativeBw * scaledBh / nativeBh;
                int startY = GROUND_Y + playerSize;  // top of top block
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
                double cx = player.x + playerSize / 2.0;
                double cy = player.y + playerSize / 2.0;
                g2.rotate(Math.toRadians(rotation), cx, cy);
                g2.drawImage(playerImg, (int) player.x, (int) player.y, playerSize, playerSize, null);
                g2.setTransform(old);
            } else {
                g2.setColor(Color.BLUE);
                g2.fill(player);
            }
        }

            // 4) Debug overlay
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRect(10, 10, 200, 200);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
            int line = 0;
            g2.drawString(String.format("player.x   = %.1f", player.x), 15, 30 + line * 15);
            line++;
            g2.drawString(String.format("player.y   = %.1f", player.y), 15, 30 + line * 15);
            line++;
            g2.drawString("velocityY  = " + velocityY, 15, 30 + line * 15);
            line++;
            g2.drawString("isJump     = " + isJump, 15, 30 + line * 15);
            line++;
            g2.drawString(String.format("rotation   = %.1f", rotation), 15, 30 + line * 15);
            line++;
            g2.drawString("bgOffsetX  = " + bgOffsetX, 15, 30 + line * 15);
            line++;
            g2.drawString("FPS_DELAY = " + FPS_DELAY, 15, 30 + line * 15);
            line++;
            g2.drawString("JUMP_VELOCITY = " + JUMP_VELOCITY, 15, 30 + line * 15);
            line++;
            g2.drawString("GROUND_Y = " + GROUND_Y, 15, 30 + line * 15);
            line++;
            g2.drawString("playerSize = " + playerSize, 15, 30 + line * 15);
            line++;
            g2.drawString("scrollSpeed = " + scrollSpeed, 15, 30 + line * 15);
            line++;
            g2.drawString("gameState = " + gameState, 15, 30 + line * 15);
            line++;


    }

    // --- KeyListener methods
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if ((code == KeyEvent.VK_SPACE || code == KeyEvent.VK_UP) && !isJump) {
            velocityY = JUMP_VELOCITY;
            isJump   = true;
        }
    }
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE)
            isJump = false;
    }
    public void keyTyped(KeyEvent e) {}
}