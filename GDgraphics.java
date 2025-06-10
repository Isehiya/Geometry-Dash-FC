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
    public static final int GRAVITY = 1;
    public static final int JUMP_VELOCITY = -15;
    public static final int GROUND_Y = 500;
    private static final double ROT_SPEED = 10.0;

    public static int playerSize = 40;
    public static int scrollSpeed = 7;

    public static Rectangle2D.Double player;
    public ArrayList<Rectangle2D.Double> blocks = new ArrayList<>();
    public ArrayList<Rectangle2D.Double> spikes = new ArrayList<>();
    public ArrayList<Rectangle2D.Double> hitboxes = new ArrayList<>();

    private int velocityY = 0;
    private boolean isOnGround = true;
    private double rotation = 0;

    public static int gameState = 0;

    Clip backgroundMusic1, backgroundMusic2;
    Clip buttonHover;

    private Image playerImg, blockImg, bgImg, spike;
    private Image halfSpeedPortal, speedPortal1, speedPortal2, speedPortal3, speedPortal4;
    private Image logo, playButton, iconMenuButton, creatorMenuButton;

    ArrayList<String> icons = new ArrayList<>();

    private int bgOffsetX = 0;
    private boolean running = false;

    private int imageOffets1 = 55;

    private Rectangle playButtonBounds;
    private Rectangle iconMenuButtonBounds;
    private Rectangle creatorMenuButtonBounds;
    private boolean hoveringPlay = false;
    private double playScale = 1.0;

    private boolean hoveringIcon = false;
    private double iconScale = 0.8;
    private boolean hoveringCreator = false;
    private final double scaleStep = 0.05;

    private double targetScale = 1.0;
    // Play button scale limits
    private static final double PLAY_SCALE_MAX = 1.2;
    private static final double PLAY_SCALE_MIN = 1.0;

    // Icon button scale limits
    private static final double ICON_SCALE_MAX = 1.0;
    private static final double ICON_SCALE_MIN = 0.80;

    private static final double CREATOR_SCALE_MAX = 1.0;
    private static final double CREATOR_SCALE_MIN = 0.80;
    private double creatorScale = 0.8;

    public GDgraphics() throws UnsupportedAudioFileException, IOException {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

            icons.add(0,"1");
            icons.add(1,"2");


        MediaTracker tracker = new MediaTracker(this);
        halfSpeedPortal = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDspeedportal0.5.gif");
        tracker.addImage(halfSpeedPortal, 0);
        speedPortal1 = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDspeedportal1.gif");
        tracker.addImage(speedPortal1, 1);
        speedPortal2 = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDspeedportal2.gif");
        tracker.addImage(speedPortal2, 2);
        speedPortal3 = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDspeedportal3.gif");
        tracker.addImage(speedPortal3, 3);
        speedPortal4 = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDspeedportal4.gif");
        tracker.addImage(speedPortal4, 4);
        playerImg = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDdefaulticon.png");
        tracker.addImage(playerImg, 5);
        blockImg = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDblock.png");
        tracker.addImage(blockImg, 6);
        bgImg = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDbackground.png");
        tracker.addImage(bgImg, 7);
        spike = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDspike.png");
        tracker.addImage(spike, 8);
        logo = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDlogo.png");
        tracker.addImage(logo, 9);
        playButton = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDplaybutton.png");
        tracker.addImage(playButton, 10);
        iconMenuButton = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDiconchoosingbutton2.png");
        tracker.addImage(iconMenuButton, 11);
        creatorMenuButton = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDcreatormenubutton2.png");
        tracker.addImage(creatorMenuButton, 12);




        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        player = new Rectangle2D.Double(50, GROUND_Y, playerSize, playerSize);

        try {
            AudioInputStream StereoMadness = AudioSystem.getAudioInputStream(new File("sfx/StereoMadness.wav"));
            AudioInputStream MenuMusic = AudioSystem.getAudioInputStream(new File("sfx/GDmenumusic.wav"));
            AudioInputStream HoveringButton = AudioSystem.getAudioInputStream(new File("sfx/GDbuttonhover.wav"));
            backgroundMusic1 = AudioSystem.getClip();
            backgroundMusic1.open(MenuMusic);
            backgroundMusic2 = AudioSystem.getClip();
            backgroundMusic2.open(StereoMadness);
            buttonHover = AudioSystem.getClip();
            buttonHover.open(HoveringButton);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (backgroundMusic1 != null) {
            backgroundMusic1.setFramePosition(0);
            backgroundMusic1.loop(Clip.LOOP_CONTINUOUSLY);
        }

        spikes.add(new Rectangle2D.Double(350, 500, 10, 20));
        spikes.add(new Rectangle2D.Double(800, 500, 10, 20));
        spikes.add(new Rectangle2D.Double(840, 500, 10, 20));
        spikes.add(new Rectangle2D.Double(1250, 500, 10, 20));
        spikes.add(new Rectangle2D.Double(1290, 500, 10, 20));
        spikes.add(new Rectangle2D.Double(2200, 500, 10, 20));
        spikes.add(new Rectangle2D.Double(2240, 500, 10, 20));
        spikes.add(new Rectangle2D.Double(2840, 500, 10, 20));
        spikes.add(new Rectangle2D.Double(2885, 500, 10, 20));
        spikes.add(new Rectangle2D.Double(2930, 500, 10, 20));
        spikes.add(new Rectangle2D.Double(3175, 460, 10, 20));
        spikes.add(new Rectangle2D.Double(3340, 500, 10, 20));
        spikes.add(new Rectangle2D.Double(3390, 500, 10, 20));
        spikes.add(new Rectangle2D.Double(3440, 500, 10, 20));
        spikes.add(new Rectangle2D.Double(3880, 420, 10, 20));



        hitboxes.add(new Rectangle2D.Double(355, 510 , 7, 18));
        hitboxes.add(new Rectangle2D.Double(805, 510 , 7, 18));
        hitboxes.add(new Rectangle2D.Double(845, 510 , 7, 18));
        hitboxes.add(new Rectangle2D.Double(1255, 510 , 7, 18));
        hitboxes.add(new Rectangle2D.Double(1295, 510 , 7, 18));
        hitboxes.add(new Rectangle2D.Double(2205, 510, 7, 18));
        hitboxes.add(new Rectangle2D.Double(2245, 510, 7, 18));
        hitboxes.add(new Rectangle2D.Double(2845, 510, 7, 18));
        hitboxes.add(new Rectangle2D.Double(2890, 510, 7, 18));
        hitboxes.add(new Rectangle2D.Double(2935, 510, 7, 18));
        hitboxes.add(new Rectangle2D.Double(3180, 470, 7, 18));
        hitboxes.add(new Rectangle2D.Double(3345, 510, 7, 18));
        hitboxes.add(new Rectangle2D.Double(3395, 510, 7, 18));
        hitboxes.add(new Rectangle2D.Double(3445, 510, 7, 18));
        hitboxes.add(new Rectangle2D.Double(3885, 430,7 , 18));


        blocks.add(new Rectangle2D.Double(1330, 500, 40, 40));
        blocks.add(new Rectangle2D.Double(1530, 500, 40, 40));
        blocks.add(new Rectangle2D.Double(1530, 460, 40, 40));
        blocks.add(new Rectangle2D.Double(1730, 500, 40, 40));
        blocks.add(new Rectangle2D.Double(1730, 460, 40, 40));
        blocks.add(new Rectangle2D.Double(1730, 420, 40, 40));
        for (int i = 2480; i <= 2800; i+=40) {
            blocks.add(new Rectangle2D.Double(i, 500, 40, 40));
        }
        for (int i = 2980; i <= 3300; i+=40) {
            blocks.add(new Rectangle2D.Double(i, 500, 40, 40));
        }
        for (int i = 3480; i <= 4200; i+=40) {
            blocks.add(new Rectangle2D.Double(i, 500, 40, 40));
            blocks.add(new Rectangle2D.Double(i, 460, 40, 40));
        }
        blocks.add(new Rectangle2D.Double(4400, 440, 40, 40));
        blocks.add(new Rectangle2D.Double(4585, 400, 40, 40));
        blocks.add(new Rectangle2D.Double(4770, 360, 40,40));
        blocks.add(new Rectangle2D.Double(4955, 320, 40, 40));
        blocks.add(new Rectangle2D.Double(5140, 280, 40, 40));
        blocks.add(new Rectangle2D.Double(5265, 320, 40, 40));
        blocks.add(new Rectangle2D.Double(5305, 320, 40, 40));

        for (int x = 5345; x < 6545; x+=40) {
            for (int y = 320; y <= 500 ; y+=40) {
                blocks.add(new Rectangle2D.Double(x, y, 40, 40));
            }
        }



        startGameLoop();
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

    private boolean noClip = false;

    private void updateGame() {
        if (bgImg != null) {
            bgOffsetX = (bgOffsetX - scrollSpeed) % bgImg.getWidth(this);
        }

        if (gameState == 0) {
            if (hoveringPlay && playScale < PLAY_SCALE_MAX) playScale += scaleStep;
            else if (!hoveringPlay && playScale > PLAY_SCALE_MIN) playScale -= scaleStep;

            if (hoveringIcon && iconScale < ICON_SCALE_MAX) iconScale += scaleStep;
            else if (!hoveringIcon && iconScale > ICON_SCALE_MIN) iconScale -= scaleStep;

            if (hoveringCreator && creatorScale < CREATOR_SCALE_MAX) creatorScale += scaleStep;
            else if (!hoveringCreator && creatorScale > CREATOR_SCALE_MIN) creatorScale -= scaleStep;

        }

        if(gameState == 1) {
            isOnGround = false;
            velocityY += GRAVITY;
            player.y += velocityY;

            rotation = (rotation + ROT_SPEED) % 360;
            for (int i = 0; i < spikes.size(); i++) {
                if (player.intersects(hitboxes.get(i)) && !noClip) {
                    System.exit(0);
                }
                spikes.get(i).x -= scrollSpeed;
                hitboxes.get(i).x -= scrollSpeed;
//                if(player.y < 300 && velocityY < 0){
//                    spikes.get(i).y -= velocityY;
//                    hitboxes.get(i).y -= velocityY;
//                }
            }
            for (int i = 0; i < blocks.size(); i++) {
                blocks.get(i).x -= scrollSpeed;
//                if(player.y < 300 && velocityY < 0){
//                    blocks.get(i).y -= velocityY;
//                }
            }
            for (Rectangle2D.Double block : blocks) {
                // Check if player is overlapping block
                if (player.intersects(block)) {
                    // Player’s bottom is below block’s top – adjust
                    if (velocityY > 0 && player.y + player.height >= block.y) {
                        isOnGround = true;
                        player.y = block.y - player.height; // snap to block top
                        velocityY = 0;
                        if (rotation <= 45) rotation = 0;
                        else if (rotation <= 135) rotation = 90;
                        else if (rotation <= 225) rotation = 180;
                        else if (rotation <= 315) rotation = 270;
                        else rotation = 0;
                    }
                }
            }


            if (player.y > GROUND_Y) {
                player.y = GROUND_Y;
                velocityY = 0;
                isOnGround = true;
                if (rotation <= 45) rotation = 0;
                else if (rotation <= 135) rotation = 90;
                else if (rotation <= 225) rotation = 180;
                else if (rotation <= 315) rotation = 270;
                else rotation = 0;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == 0) {
            //moves the background backwards
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
                    int scaledW = (int)(WIDTH * 0.2 * playScale);
                    int scaledH = (int)(originalH * (scaledW / (double)originalW));
                    int playX = (WIDTH - scaledW + imageOffets1) / 2;
                    int playY = (HEIGHT - scaledH + 150) / 2;
                    playButtonBounds = new Rectangle(playX, playY, scaledW, scaledH);
                    g2.drawImage(playButton, playX, playY, scaledW, scaledH, this);
                }
            }
            if (iconMenuButton != null){
                int originalW = iconMenuButton.getWidth(this);
                int originalH = iconMenuButton.getHeight(this);
                if (originalW > 0 && originalH > 0){
                    int scaledW = (int)(WIDTH * 0.2 * iconScale);
                    int scaledH = (int)(originalH * (scaledW / (double)originalW));
                    int iconX = (WIDTH - scaledW + imageOffets1 - 400) / 2;
                    int iconY = (HEIGHT - scaledH + 150) / 2;
                    iconMenuButtonBounds = new Rectangle(iconX, iconY, scaledW, scaledH);
                    g2.drawImage(iconMenuButton, iconX, iconY, scaledW, scaledH, this);
                }
            }
            if (creatorMenuButton != null){
                int originalW = creatorMenuButton.getWidth(this);
                int originalH = creatorMenuButton.getHeight(this);
                if (originalW > 0 && originalH > 0){
                    int scaledW = (int)(WIDTH * 0.2 * creatorScale);
                    int scaledH = (int)(originalH * (scaledW / (double)originalW));
                    int iconX = (WIDTH - scaledW + imageOffets1 + 400) / 2;
                    int iconY = (HEIGHT - scaledH + 150) / 2;
                    creatorMenuButtonBounds = new Rectangle(iconX, iconY, scaledW, scaledH);
                    g2.drawImage(creatorMenuButton, iconX, iconY, scaledW, scaledH, this);
                }
            }


        }
        else if (gameState == 1) {
            //moves the background backwards
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
                for(Rectangle2D.Double r: blocks){
                    g2.drawImage(blockImg, (int) r.x, (int) r.y, scaledBw, scaledBh, this);
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

    private boolean wasHoveringPlay    = false;
    private boolean wasHoveringIcon    = false;
    private boolean wasHoveringCreator = false;


    private void playClipFromStart(Clip c) {
        if (c == null) return;
        if (c.isRunning()) c.stop();
        c.setFramePosition(0);
        c.start();
    }

    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if ((code == KeyEvent.VK_SPACE || code == KeyEvent.VK_UP) && isOnGround) {
            velocityY = JUMP_VELOCITY;
            isOnGround = false;
        }
    }

    public void keyReleased(KeyEvent e) {
    }
    public void keyTyped(KeyEvent e) {}
    public void mouseMoved(MouseEvent e) {
        Point p = e.getPoint();

        // Determine current hover state
        boolean nowHoveringPlay    = playButtonBounds    != null && playButtonBounds.contains(p) && gameState == 0;
        boolean nowHoveringIcon    = iconMenuButtonBounds   != null && iconMenuButtonBounds.contains(p) && gameState == 0;
        boolean nowHoveringCreator = creatorMenuButtonBounds!= null && creatorMenuButtonBounds.contains(p) && gameState == 0;

        // Play SFX on the instant we enter each button
        if (nowHoveringPlay && !hoveringPlay) {
            playClipFromStart(buttonHover);
        }
        if (nowHoveringIcon && !hoveringIcon) {
            playClipFromStart(buttonHover);
        }
        if (nowHoveringCreator && !hoveringCreator) {
            playClipFromStart(buttonHover);
        }

        hoveringPlay    = nowHoveringPlay;
        hoveringIcon    = nowHoveringIcon;
        hoveringCreator = nowHoveringCreator;
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

        if (gameState == 0 && iconMenuButtonBounds != null && iconMenuButtonBounds.contains(e.getPoint())) {
            gameState = 1;
            if (backgroundMusic1 != null && backgroundMusic1.isRunning()) {
                backgroundMusic1.stop();
            }
            if (backgroundMusic2 != null && !backgroundMusic2.isRunning()) {
                backgroundMusic2.setFramePosition(0);
                backgroundMusic2.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }

        if (gameState == 0 && creatorMenuButtonBounds != null && creatorMenuButtonBounds.contains(e.getPoint())) {
            gameState = 1;
            noClip = true;
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