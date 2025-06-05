import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import javax.sound.sampled.*;
import java.util.*;

public class GDgraphics extends JPanel implements KeyListener, MouseListener, MouseMotionListener {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final int FPS_DELAY = 16;
    public static final double GRAVITY = 0.5;
    public static final double JUMP_VELOCITY = -10;
    public static final int GROUND_Y = 500;
    private static final double ROT_SPEED = 5.0;

    public static final int CUBE = 1;
    public static final int SHIP = 2;

    public static int playerSize = 40;
    public static int scrollSpeed = 5;

    public static Rectangle2D.Double player;
    public ArrayList<Rectangle2D.Double> blocks = new ArrayList<>();
    public ArrayList<Rectangle2D.Double> spikes = new ArrayList<>();
    public ArrayList<Rectangle2D.Double> hitboxes = new ArrayList<>();

    private double velocityY = 0;
    private boolean isJump = false;
    private double rotation = 0;

    public static int gameState = 0;

    Clip backgroundMusic1, backgroundMusic2;

    private Image playerImg, blockImg, bgImg, spike;
    private Image halfSpeedPortal, speedPortal1, speedPortal2, speedPortal3, speedPortal4;
    private Image logo, playButton, shipPortal;

    private int bgOffsetX = 0;
    private boolean running = false;

    private int imageOffets1 = 40;

    private Rectangle playButtonBounds;
    private boolean hoveringPlay = false;

    public GDgraphics() throws UnsupportedAudioFileException, IOException {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

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
        logo = Toolkit.getDefaultToolkit().getImage("GDlogo.png");
        tracker.addImage(logo, 9);
        playButton = Toolkit.getDefaultToolkit().getImage("GDplaybutton.png");
        tracker.addImage(playButton, 10);
        shipPortal = Toolkit.getDefaultToolkit().getImage("GDshipportal.gif");
        tracker.addImage(shipPortal, 11);

        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        player = new Rectangle2D.Double(50, GROUND_Y, playerSize, playerSize);

        try {
            AudioInputStream StereoMadness = AudioSystem.getAudioInputStream(new File("StereoMadness.wav"));
            AudioInputStream MenuMusic = AudioSystem.getAudioInputStream(new File("GDmenumusic.wav"));
            backgroundMusic1 = AudioSystem.getClip();
            backgroundMusic1.open(MenuMusic);
            backgroundMusic2 = AudioSystem.getClip();
            backgroundMusic2.open(StereoMadness);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (backgroundMusic1 != null) {
            backgroundMusic1.setFramePosition(0);
            backgroundMusic1.loop(Clip.LOOP_CONTINUOUSLY);
        }

        spikes.add(new Rectangle2D.Double(350, 500, 10, 20));
        spikes.add(new Rectangle2D.Double(600, 500, 10, 20));
        spikes.add(new Rectangle2D.Double(650, 500, 10, 20));

        hitboxes.add(new Rectangle2D.Double(355, 510 , 7, 18));
        hitboxes.add(new Rectangle2D.Double(605, 510 , 7, 18));
        hitboxes.add(new Rectangle2D.Double(655, 510 , 7, 18));
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
        if(gameState == 1) {

            for (int i = 0; i < hitboxes.size(); i++) {
                if (player.intersects(hitboxes.get(i))) {
                    System.exit(0);
                }
                hitboxes.get(i).x -= scrollSpeed;
            }
            for (int i = 0; i < spikes.size(); i++) {
                spikes.get(i).x -= scrollSpeed;
            }
            for (int i = 0; i < blocks.size(); i++) {
                if(player.intersects(blocks.get(i))){
                    if (player.y > blocks.get(i).y){
                        System.exit(0);
                    }
                    else{
                        isJump = false;
                    }
                }
                blocks.get(i).x -= scrollSpeed;
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
                if (rotation <= 45) rotation = 0;
                else if (rotation <= 135) rotation = 90;
                else if (rotation <= 225) rotation = 180;
                else if (rotation <= 315) rotation = 270;
                else rotation = 0;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == 0) {
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
                    int scaledW = (int)(WIDTH * 0.9);
                    int scaledH = (int)(originalH * (scaledW / (double)originalW));
                    int logoX = (WIDTH - scaledW + imageOffets1) / 2;
                    int logoY = 50;
                    g2.drawImage(logo, logoX, logoY, scaledW, scaledH, this);
                }
            }
            if (playButton != null) {
                int originalW = playButton.getWidth(this);
                int originalH = playButton.getHeight(this);
                if (originalW > 0 && originalH > 0) {
                    double scale = hoveringPlay ? 1.2 : 1.0;
                    int scaledW = (int)(WIDTH * 0.2 * scale);
                    int scaledH = (int)(originalH * (scaledW / (double)originalW));
                    int playX = (WIDTH - scaledW + imageOffets1) / 2;
                    int playY = (HEIGHT - scaledH + 150) / 2;
                    playButtonBounds = new Rectangle(playX, playY, scaledW, scaledH);
                    g2.drawImage(playButton, playX, playY, scaledW, scaledH, this);
                }
            }
        } else if (gameState == 1) {
            if (bgImg != null) {
                int bwBg = bgImg.getWidth(this);
                for (int i = -1; i <= getWidth() / bwBg + 1; i++) {
                    g2.drawImage(bgImg, bgOffsetX + i * bwBg, 0, bwBg, getHeight(), null);
                }
            }
            for (Rectangle2D.Double spikeRect : spikes) {
                int spikeHeight = spike.getHeight(this);
                int spikeWidth = spike.getWidth(this);
                spikeWidth /= 3;
                spikeHeight /= 3;
                g2.drawImage(spike, (int) spikeRect.x, (int) spikeRect.y, spikeWidth, spikeHeight, this);
            }
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
    }

    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if ((code == KeyEvent.VK_SPACE || code == KeyEvent.VK_UP) && !isJump) {
            velocityY = JUMP_VELOCITY;
            isJump = true;
        }
    }
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE)
            isJump = false;
    }
    public void keyTyped(KeyEvent e) {}

    public void mouseMoved(MouseEvent e) {
        if (playButtonBounds != null && playButtonBounds.contains(e.getPoint())) hoveringPlay = true;
        else hoveringPlay = false;
    }
    public void mouseDragged(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {
        if (gameState == 0 && playButtonBounds != null && playButtonBounds.contains(e.getPoint())) {
            gameState = 1;
            if (backgroundMusic1 != null && backgroundMusic1.isRunning()) {
                backgroundMusic1.stop();
            }
            if (backgroundMusic2 != null && !backgroundMusic2.isRunning()) {
                backgroundMusic2.setFramePosition(0);
                backgroundMusic2.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
    }
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
