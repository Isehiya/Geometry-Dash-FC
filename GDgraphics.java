/*
Names: Andy Yang, Jacob Lin
Teacher: Ms. Wong
Due date: Jun 14, 2025
Course: ICS3U1

Geometry Dash FC is inspired by the classic game Geometry Dash. Dodge the spikes, and
jump on top of blocks. Press up or space to jump, esc once to pause, and esc twice to exit.
Reach the pink rectangle to win!

 */
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import javax.sound.sampled.*;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class GDgraphics extends JPanel implements KeyListener, MouseListener, MouseMotionListener {


    //Game constants
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final int FPS_DELAY = 16;
    public static final int GRAVITY = 1;
    public static final int JUMP_VELOCITY = -15;
    public static final int GROUND_Y = 500;
    private static final double ROT_SPEED = 10.0;

    public int playerX = 0; // tracks player movement for game resets

    // Player info and scroll speed (game speed)
    public static int playerSize = 40;
    public static int scrollSpeed = 7;

    public double cameraY = 0; // tracks camera movement (functionality not yet added)

    // Player, blocks, spikes, and hitboxes
    public static Rectangle2D.Double player;
    public ArrayList<Rectangle2D.Double> blocks = new ArrayList<>();
    public ArrayList<Rectangle2D.Double> spikes = new ArrayList<>();
    public ArrayList<Rectangle2D.Double> hitboxes = new ArrayList<>();

    // Jump mechanics
    private int velocityY = 0;
    private boolean isOnGround = true;
    private double rotation = 0;

    public static int gameState = 0; // Used for game screen toggling

    // Game music and SFX
    Clip backgroundMusic1, backgroundMusic2, endMusic;
    Clip buttonHover, dead;
    Clip icon, credits, instructionsMusic;
    Clip iconselectormusic;

    // Game images
    private Image playerImg, blockImg, bgImg, spike;
    private Image halfSpeedPortal, speedPortal1, speedPortal2, speedPortal3, speedPortal4;

    // Green button = instructions; blue button = credits
    private Image logo, playButton, iconMenuButton, creatorMenuButton, greenButton, blueButton;
    private Image shipPortal;
    private Image levelComplete, instructions, creditsImage;


    ArrayList<String> icons = new ArrayList<>();

    // Background variables
    private int bgOffsetX = 0;
    private boolean running = false;

    private int imageOffets1 = 55;

    // Menu options
    private Rectangle playButtonBounds;
    private Rectangle iconMenuButtonBounds;
    private Rectangle creatorMenuButtonBounds;
    private Rectangle instructionsBounds;
    private Rectangle creditsBounds;
    private boolean hoveringPlay = false;
    private double playScale = 1.0;
    private boolean hoveringIcon = false;
    private double iconScale = 0.8;
    private boolean hoveringCreator = false;
    private boolean hoveringInstructions = false;
    private boolean hoveringCredits = false;
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

    private Rectangle2D.Double endPort = new Rectangle2D.Double(11000, 420, 40, 200);
    private int selectedIconNumber = 1;
    private int selectedColor1Index = 2;
    private int selectedColor2Index = 7;
    private String selectedIconPath;
    private final String[] colorNames = {
            "red","orange","yellow","green","blue","indigo","violet","white","black"
    };
    private int   selectedIcon = 1;
    private boolean red1, orange1, yellow1=true, green1, blue1, indigo1, violet1, white1, black1;
    private boolean red2, orange2, yellow2, green2, blue2, indigo2, violet2, white2=true, black2;

    private int iconBtnX       = 100, iconBtnY       = 100;
    private int iconBtnSize    = 64,  iconBtnSpacing = 100;
    private Rectangle icon1Rect, icon2Rect;

    private int previewX       = 300, previewY       = 150;
    private int previewSize    = 128;
    private int highlightStroke=   4;

    private final String[] COLORS = {
            "red","orange","yellow","green","blue","indigo","violet","white","black"
    };
    private final int ICON_BTN_SIZE    = 64;
    private final int ICON_BTN_SPACING = 100;
    private final int ICON1_X = 100, ICON1_Y = 100;
    private final int PREVIEW_X = 650, PREVIEW_Y = 150, PREVIEW_SIZE = 128;

    private final int COLOR_BTN_SIZE    = 48;
    private final int COLOR_BTN_SPACING = 8;
    private final int OUTER_ROW_X = ICON1_X, OUTER_ROW_Y = ICON1_Y + ICON_BTN_SIZE + 40;
    private final int INNER_ROW_X = OUTER_ROW_X, INNER_ROW_Y = OUTER_ROW_Y + COLOR_BTN_SIZE + 20;

    private final int HIGHLIGHT_STROKE = 3;

    private Rectangle[] outerColorRects = new Rectangle[COLORS.length];
    private Rectangle[] innerColorRects = new Rectangle[COLORS.length];
    private Image defaultPlayerImg;
    private Image currentPlayerImg;




    public GDgraphics() throws UnsupportedAudioFileException, IOException {

        // Setting game dimensions and initializing keyboard and mouse
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        // Player icon selection (may or may not be used)
            icons.add(0,"1");
            icons.add(1,"2");

        icon1Rect = new Rectangle(ICON1_X, ICON1_Y, ICON_BTN_SIZE, ICON_BTN_SIZE);
        icon2Rect = new Rectangle(ICON1_X + ICON_BTN_SPACING, ICON1_Y,
                ICON_BTN_SIZE, ICON_BTN_SIZE);

        // Color-combo rows
        for (int i = 0; i < COLORS.length; i++) {
            int ox = OUTER_ROW_X + i * (COLOR_BTN_SIZE + COLOR_BTN_SPACING);
            int oy = OUTER_ROW_Y;
            outerColorRects[i] = new Rectangle(ox, oy, COLOR_BTN_SIZE, COLOR_BTN_SIZE);

            int ix = INNER_ROW_X + i * (COLOR_BTN_SIZE + COLOR_BTN_SPACING);
            int iy = INNER_ROW_Y;
            innerColorRects[i] = new Rectangle(ix, iy, COLOR_BTN_SIZE, COLOR_BTN_SIZE);
        }

        // Build initial default path:
        selectedIconPath = GDiconlocater.GDiconLocater(
                selectedIcon,
                false,false,true, false,false,false,false,false,false,  // yellow1
                false,false,false,false,false,false,false,true,false   // white2
        );

        // Loading game images
        MediaTracker tracker = new MediaTracker(this);
        halfSpeedPortal = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDspeedportal0.5.gif");
        tracker.addImage(halfSpeedPortal, 0);
        speedPortal1    = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDspeedportal1.gif");
        tracker.addImage(speedPortal1,    1);
        speedPortal2    = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDspeedportal2.gif");
        tracker.addImage(speedPortal2,    2);
        speedPortal3    = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDspeedportal3.gif");
        tracker.addImage(speedPortal3,    3);
        speedPortal4    = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDspeedportal4.gif");
        tracker.addImage(speedPortal4,    4);

// block, background, spike
        blockImg        = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDblock.png");
        tracker.addImage(blockImg,        5);
        bgImg           = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDbackground.png");
        tracker.addImage(bgImg,           6);
        spike           = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDspike.png");
        tracker.addImage(spike,           7);

// logo & menu buttons
        logo            = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDlogo.png");
        tracker.addImage(logo,            8);
        playButton      = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDplaybutton.png");
        tracker.addImage(playButton,      9);
        iconMenuButton  = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDiconchoosingbutton2.png");
        tracker.addImage(iconMenuButton, 10);
        creatorMenuButton = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDcreatormenubutton2.png");
        tracker.addImage(creatorMenuButton,11);

// portals & end‐screen art
        shipPortal      = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDshipportal.gif");
        tracker.addImage(shipPortal,     12);
        levelComplete   = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDlevelcomplete.png");
        tracker.addImage(levelComplete,  13);

// instructions & credits
        instructions    = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDinstructions.png");
        tracker.addImage(instructions,   14);
        creditsImage    = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDcredits.png");
        tracker.addImage(creditsImage,   15);

// info/credits icons
        greenButton     = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDinfobutton.png");
        tracker.addImage(greenButton,    16);
        blueButton      = Toolkit.getDefaultToolkit().getImage("GDprojectImages/GDcreditsbutton.png");
        tracker.addImage(blueButton,     17);

        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        currentPlayerImg = Toolkit.getDefaultToolkit().getImage(selectedIconPath);

        // Initializing player icon
        player = new Rectangle2D.Double(50, GROUND_Y, playerSize, playerSize);

        try { // Loading music and SFX
            AudioInputStream StereoMadness = AudioSystem.getAudioInputStream(new File("sfx/StereoMadness.wav"));
            AudioInputStream MenuMusic = AudioSystem.getAudioInputStream(new File("sfx/Rrhar'il.wav"));
            AudioInputStream HoveringButton = AudioSystem.getAudioInputStream(new File("sfx/GDbuttonhover.wav"));
            AudioInputStream EndMusic = AudioSystem.getAudioInputStream(new File("sfx/Igallta.wav"));
            AudioInputStream Dead = AudioSystem.getAudioInputStream(new File("sfx/dead.wav"));
            AudioInputStream Icon = AudioSystem.getAudioInputStream(new File("sfx/Isolation.wav"));
            AudioInputStream Credits = AudioSystem.getAudioInputStream(new File("sfx/Solar Wind.wav"));
            AudioInputStream Instructions = AudioSystem.getAudioInputStream(new File("sfx/Quaoar.wav"));
            AudioInputStream IconMenu = AudioSystem.getAudioInputStream(new File("sfx/Bison Charge - Umeboshi Chazuke.wav"));
            backgroundMusic1 = AudioSystem.getClip();
            backgroundMusic1.open(MenuMusic);
            backgroundMusic2 = AudioSystem.getClip();
            backgroundMusic2.open(StereoMadness);
            buttonHover = AudioSystem.getClip();
            buttonHover.open(HoveringButton);
            endMusic = AudioSystem.getClip();
            endMusic.open(EndMusic);
            dead = AudioSystem.getClip();
            dead.open(Dead);
            icon = AudioSystem.getClip();
            icon.open(Icon);
            credits = AudioSystem.getClip();
            credits.open(Credits);
            instructionsMusic = AudioSystem.getClip();
            instructionsMusic.open(Instructions);
            iconselectormusic = AudioSystem.getClip();
            iconselectormusic.open(IconMenu);


            icon1Rect = new Rectangle(iconBtnX,iconBtnY, iconBtnSize, iconBtnSize);
            icon2Rect = new Rectangle(iconBtnX + iconBtnSpacing,iconBtnY, iconBtnSize, iconBtnSize);

            selectedIconPath = GDiconlocater.GDiconLocater(
                    selectedIcon,
                    red1, orange1, yellow1, green1, blue1, indigo1, violet1, white1, black1,
                    red2, orange2, yellow2, green2, blue2, indigo2, violet2, white2, black2
            );



        } catch (Exception e) {
            e.printStackTrace();
        }

        // Menu music
        if (backgroundMusic1 != null) {
            backgroundMusic1.setFramePosition(0);
            backgroundMusic1.loop(Clip.LOOP_CONTINUOUSLY);
        }

        // Adding spikes
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
        for (int i = 5685; i < 5825; i+=40) {
            spikes.add(new Rectangle2D.Double(i, 280, 10, 20));
        }
        for (int i = 6025; i < 6165; i+=40) {
            spikes.add(new Rectangle2D.Double(i, 280, 10, 20));
        }
        for (int i = 6365; i < 6525; i+=40) {
            spikes.add(new Rectangle2D.Double(i, 240, 10, 20));
        }
        spikes.add(new Rectangle2D.Double(6785, 280, 10, 20));
        spikes.add(new Rectangle2D.Double(6825, 360, 10, 20));
        spikes.add(new Rectangle2D.Double(7620, 360, 10, 20));
        spikes.add(new Rectangle2D.Double(7940, 400, 10, 20));
        spikes.add(new Rectangle2D.Double(9290, 200, 10, 20));

        for (int i = 5435; i < 9260; i+=40) {
            spikes.add(new Rectangle2D.Double(i, 500, 10, 20));
        }

        // Adding hitboxes
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
        for (int i = 5690; i < 5830; i+=40) {
            hitboxes.add(new Rectangle2D.Double(i, 290, 7, 18));
        }
        for (int i = 6030; i < 6170; i+=40) {
            hitboxes.add(new Rectangle2D.Double(i, 290, 7, 18));
        }
        for (int i = 6370; i < 6530; i+=40) {
            hitboxes.add(new Rectangle2D.Double(i, 250, 7, 18));
        }
        hitboxes.add(new Rectangle2D.Double(6790, 290, 7, 18));
        hitboxes.add(new Rectangle2D.Double(6830, 370, 7, 18));
        hitboxes.add(new Rectangle2D.Double(7625, 450, 7, 18));
        hitboxes.add(new Rectangle2D.Double(7945, 410, 7, 18));
        hitboxes.add(new Rectangle2D.Double(9295, 210, 7, 18));

        for (int i = 5440; i < 9265; i+=40) {
            hitboxes.add(new Rectangle2D.Double(i, 510, 7, 18));
        }

        // Adding blocks
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
        blocks.add(new Rectangle2D.Double(4400, 440, 40, 20));
        blocks.add(new Rectangle2D.Double(4585, 400, 40, 20));
        blocks.add(new Rectangle2D.Double(4770, 360, 40,20));
        blocks.add(new Rectangle2D.Double(4955, 320, 40, 20));
        blocks.add(new Rectangle2D.Double(5140, 280, 40, 20));
        blocks.add(new Rectangle2D.Double(5265, 320, 40, 40));
        blocks.add(new Rectangle2D.Double(5305, 320, 40, 40));

        for (int x = 5345; x < 6825; x+=40) {
            for (int y = 320; y <= 500 ; y+=40) {
                blocks.add(new Rectangle2D.Double(x, y, 40, 40));
            }
        }
        blocks.add(new Rectangle2D.Double(5725, 260, 40, 20));
        blocks.add(new Rectangle2D.Double(5765, 260, 40, 20));
        blocks.add(new Rectangle2D.Double(6065, 260, 40, 20));
        blocks.add(new Rectangle2D.Double(6105, 260, 40, 20));
        for (int i = 6265; i < 6625; i++) {
            blocks.remove(new Rectangle2D.Double(i, 320, 40, 40));
        }

        for (int i = 6365; i < 6525; i+=40) {
            blocks.add(new Rectangle2D.Double(i, 280, 40, 20));
        }
        for (int x = 6825; x < 7265; x+=40) {
            for (int y = 400; y <= 500; y+=40) {
                blocks.add(new Rectangle2D.Double(x, y, 40, 40));
            }
        }
        for (int i = 7265; i < 7420; i+=40) {
            blocks.add(new Rectangle2D.Double(i, 400, 40, 40));
        }

        for (int i = 7500; i < 7660; i+=40) {
            blocks.add(new Rectangle2D.Double(i, 400, 40, 20));
        }

        for (int i = 7700; i < 7980; i+=40) {
            blocks.add(new Rectangle2D.Double(i, 440, 40, 20));
        }

        for (int i = 8020; i < 8220; i+=40) {
            blocks.add(new Rectangle2D.Double(i, 480, 40, 20));
        }

        blocks.add(new Rectangle2D.Double(8365, 440, 40, 20));
        blocks.add(new Rectangle2D.Double(8550, 400, 40, 20));
        blocks.add(new Rectangle2D.Double(8735, 360, 40, 20));
        blocks.add(new Rectangle2D.Double(8920, 320, 40, 20));
        blocks.add(new Rectangle2D.Double(9105, 280, 40, 20));
        blocks.add(new Rectangle2D.Double(9290, 240, 40, 20));

        startGameLoop(); // Game starts once initialized
    }

    /**
     * Starts the game for the first time. Updates the game at a constant speed (60 FPS).
     */
    public void startGameLoop() {
        running = true;
        Thread loop = new Thread(() -> {
            while (running) {
                try {
                    updateGame(); // Updating game every frame
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Thread.sleep(FPS_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        loop.start();
    }

    private boolean noClip = false; // implemented cheat

    private int frames = 0; // Music frame position - used when pausing the game

    /**
     * Updates the game. It is called in the game loop method, and it keeps track of all player, block, spike,
     * and hitbox positions. It also keeps track of the game state.
     * @throws InterruptedException
     */
    private void updateGame() throws InterruptedException {
        if (bgImg != null) { // Setting up the background
            bgOffsetX = (bgOffsetX - scrollSpeed) % bgImg.getWidth(this);
        }

        // Menu screen (game state 0)
        if (gameState == 0) { // Icon gets bigger when mouse hovers over it
            if (hoveringPlay && playScale < PLAY_SCALE_MAX) playScale += scaleStep;
            else if (!hoveringPlay && playScale > PLAY_SCALE_MIN) playScale -= scaleStep;

            if (hoveringIcon && iconScale < ICON_SCALE_MAX) iconScale += scaleStep;
            else if (!hoveringIcon && iconScale > ICON_SCALE_MIN) iconScale -= scaleStep;

            if (hoveringCreator && creatorScale < CREATOR_SCALE_MAX) creatorScale += scaleStep;
            else if (!hoveringCreator && creatorScale > CREATOR_SCALE_MIN) creatorScale -= scaleStep;

        }

        // Level running (state 1) and level paused (state -2)
        if(gameState == 1 || gameState == -2) {
            if (gameState == -2) scrollSpeed = 0; // Stopping the level when paused
            else { // Moving level
                scrollSpeed = 7;
                playerX += scrollSpeed; // Used for game resets
                isOnGround = false;

                // Jump mechanic
                velocityY += GRAVITY;
                player.y += velocityY;
                rotation = (rotation + ROT_SPEED) % 360;
                for (int i = 0; i < spikes.size(); i++) {
                    // Noclip allows you to clip through spikes and blocks without dying.
                    if (player.intersects(hitboxes.get(i)) && !noClip) { // Checks to see if player is hitting spike
                        backgroundMusic2.stop();
                        dead.setFramePosition(0);
                        dead.start();
                        TimeUnit.SECONDS.sleep(1);
                        resetGame();
                    }
                    // Moving obstacles forward
                    spikes.get(i).x -= scrollSpeed;
                    hitboxes.get(i).x -= scrollSpeed;
                }
                endPort.x -= scrollSpeed;
                for (int i = 0; i < blocks.size(); i++) {
                    blocks.get(i).x -= scrollSpeed;
                }
                for (Rectangle2D.Double block : blocks) {
                    // Check if player is overlapping block
                    if (player.intersects(block)) {
                        // Player’s bottom is above block’s top
                        if (velocityY > 0 && player.y <= block.y) {
                            isOnGround = true;
                            player.y = block.y - player.height; // snap to block top
                            velocityY = 0;
                            if (rotation <= 45) rotation = 0;
                            else if (rotation <= 135) rotation = 90;
                            else if (rotation <= 225) rotation = 180;
                            else if (rotation <= 315) rotation = 270;
                            else rotation = 0;
                        }
                        else if (!noClip){ // Player is below the block's top - player will die
                            backgroundMusic2.stop();
                            dead.setFramePosition(0);
                            dead.start();
                            TimeUnit.SECONDS.sleep(1);
                            resetGame();
                        }
                    }
                }
                if (player.y > GROUND_Y) { // Checks to see if player is on the ground
                    player.y = GROUND_Y;
                    velocityY = 0;
                    isOnGround = true;
                    if (rotation <= 45) rotation = 0;
                    else if (rotation <= 135) rotation = 90;
                    else if (rotation <= 225) rotation = 180;
                    else if (rotation <= 315) rotation = 270;
                    else rotation = 0;
                }
                if (player.intersects(endPort)) { // Player wins
                    gameState = 2;
                }

                // Win screen (state 2)
                if (gameState == 2) {
                    player.x = 0;
                    player.y = 500;
                    scrollSpeed = 0; // Stopping the map
                    backgroundMusic2.stop();
                    if (endMusic != null) { // Playing the win music
                        endMusic.setFramePosition(0);
                        endMusic.loop(Clip.LOOP_CONTINUOUSLY);
                    }
                }
            }
        }
        // Game instructions (state -1)
        else if (gameState == -1){
            if(!instructionsMusic.isRunning()){ // Plays a different song
                backgroundMusic1.stop();
                instructionsMusic.setFramePosition(0);
                instructionsMusic.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
        // Credits (state 4)
        else if (gameState == 4){
            if(!credits.isRunning()){
                backgroundMusic1.stop();
                credits.setFramePosition(0);
                credits.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }

        // Icon selection (state 3 - may or may not be used)
        else if (gameState == 3){
            if(!icon.isRunning()){
                backgroundMusic1.stop();
                icon.setFramePosition(0);
                icon.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
        repaint(); // Redraws all objects
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
                    g2.drawImage(bgImg, bgOffsetX + i * bwBg, 0, bwBg, getHeight(), this);
                }
            }

            if (logo != null) { // Game logo
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
            if (playButton != null) { // Play button
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
            if (iconMenuButton != null){ // Icon selection
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
            if (creatorMenuButton != null){ // Play level with cheat on
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

            if(greenButton != null){ // Instructions button
                g2.drawImage(greenButton, 350, 600, 60, 60, this);
                instructionsBounds = new Rectangle( 350, 600, 60, 60);
            }

            if (blueButton != null){ // Credits button (originally blue but changed to green as well)
                g2.drawImage(blueButton, 450, 600, 60, 60, this);
                creditsBounds = new Rectangle( 450, 600, 60, 60);
            }

        }
        else if (gameState == 5) {
            if (bgImg != null) {
                int bwBg = bgImg.getWidth(this);
                for (int i = -1; i <= getWidth() / bwBg + 1; i++) {
                    g2.drawImage(bgImg, bgOffsetX + i * bwBg, 0, bwBg, getHeight(), this);
                }
            }
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.setColor(Color.WHITE);
            String msg = "Click anywhere to return (Press icon selectors to select icon):";
            int tw = g2.getFontMetrics().stringWidth(msg);
            g2.drawString(msg, (WIDTH - tw) / 2, HEIGHT - 30);

            Image img1 = Toolkit.getDefaultToolkit().getImage(
                    GDiconlocater.GDiconLocater(
                            1,
                            false,false,true, false,false,false,false,false,false,
                            false,false,false,false,false,false,false,true,false
                    )
            );
            Image img2 = Toolkit.getDefaultToolkit().getImage(
                    GDiconlocater.GDiconLocater(
                            2,
                            false,false,true, false,false,false,false,false,false,
                            false,false,false,false,false,false,false,true,false
                    )
            );
            g2.drawImage(img1, icon1Rect.x, icon1Rect.y,
                    icon1Rect.width, icon1Rect.height, this);
            g2.drawImage(img2, icon2Rect.x, icon2Rect.y,
                    icon2Rect.width, icon2Rect.height, this);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(HIGHLIGHT_STROKE));
            Rectangle selIconRect = (selectedIcon == 1) ? icon1Rect : icon2Rect;
            g2.drawRect(selIconRect.x, selIconRect.y,
                    selIconRect.width, selIconRect.height);

            for (int i = 0; i < COLORS.length; i++) {
                boolean[] o = new boolean[COLORS.length];
                o[i]    = true;
                boolean[] in = new boolean[COLORS.length];
                in[7]   = true;  // white2
                String path = GDiconlocater.GDiconLocater(
                        selectedIcon,
                        o[0],o[1],o[2],o[3],o[4],o[5],o[6],o[7],o[8],
                        in[0],in[1],in[2],in[3],in[4],in[5],in[6],in[7],in[8]
                );
                Image iconC = Toolkit.getDefaultToolkit().getImage(path);
                Rectangle r = outerColorRects[i];
                g2.drawImage(iconC, r.x, r.y, r.width, r.height, this);

                if (i == selectedColor1Index) {
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(HIGHLIGHT_STROKE));
                    g2.drawRect(r.x, r.y, r.width, r.height);
                }
            }

            for (int j = 0; j < COLORS.length; j++) {
                boolean[] o = new boolean[COLORS.length];
                o[selectedColor1Index] = true;
                boolean[] in = new boolean[COLORS.length];
                in[j]                  = true;
                String path = GDiconlocater.GDiconLocater(
                        selectedIcon,
                        o[0],o[1],o[2],o[3],o[4],o[5],o[6],o[7],o[8],
                        in[0],in[1],in[2],in[3],in[4],in[5],in[6],in[7],in[8]
                );
                Image iconC = Toolkit.getDefaultToolkit().getImage(path);
                Rectangle r = innerColorRects[j];
                g2.drawImage(iconC, r.x, r.y, r.width, r.height, this);

                if (j == selectedColor2Index) {
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(HIGHLIGHT_STROKE));
                    g2.drawRect(r.x, r.y, r.width, r.height);
                }
            }

            Image preview = Toolkit.getDefaultToolkit().getImage(selectedIconPath);
            g2.drawImage(preview, PREVIEW_X, PREVIEW_Y,
                    PREVIEW_SIZE, PREVIEW_SIZE, this);
        }

        else if (gameState == 1 || gameState == -2) {
                if (bgImg != null) {
                    int bwBg = bgImg.getWidth(this);
                    for (int i = -1; i <= getWidth() / bwBg + 1; i++) {
                        g2.drawImage(bgImg, bgOffsetX + i * bwBg, 0, bwBg, getHeight(), this);
                    }
                }

                for (Rectangle2D.Double spikeRect : spikes) { // Moving spikes forward
                    int spikeHeight = spike.getHeight(this);
                    int spikeWidth = spike.getWidth(this);
                    spikeWidth /= 3;
                    spikeHeight /= 3;
                    g2.drawImage(spike, (int) spikeRect.x, (int) spikeRect.y, spikeWidth, spikeHeight, this);
                }

                if (shipPortal != null) { // Drawing end portal
                    g2.drawImage(shipPortal, (int) endPort.x, (int) endPort.y, 40, 120, this);
                    g2.setColor(Color.MAGENTA);
                    g2.fillRect((int) endPort.x, 420, 40, 120);
                } else {
                    System.out.println("Nothing");
                }

                if (blockImg != null) { // Moving blocks forward and drawing ground

                    // Spacing the blocks evenly to create the ground
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
                            g2.drawImage(blockImg, offsetX + i * scaledBw, (int) (y - cameraY), scaledBw, scaledBh, null);
                        }
                    }
                    // Drawing the added blocks
                    for (Rectangle2D.Double r : blocks) {
                        g2.drawImage(blockImg, (int) r.x, (int) r.y, (int) r.width, (int) r.height, this);
                    }
                }

            // pick which icon to draw
            Image iconToDraw;
            if (currentPlayerImg != null) {
                iconToDraw = currentPlayerImg;
            } else {
                iconToDraw = defaultPlayerImg;
            }

            if (iconToDraw != null) {
                AffineTransform old = g2.getTransform();
                double cx = player.x + playerSize / 2.0;
                double cy = player.y + playerSize / 2.0;
                g2.rotate(Math.toRadians(rotation), cx, cy);
                g2.drawImage(
                        iconToDraw,
                        (int) player.x,
                        (int) player.y,
                        playerSize,
                        playerSize,
                        this
                );
                g2.setTransform(old);
            } else {
                g2.setColor(Color.BLUE);
                g2.fill(player);
            }


            if (gameState == -2) { // Drawing pause screen
                g2.setFont(new Font("Arial", Font.BOLD, 30));
                g2.drawString("Paused - click to continue", 0, 0);
            }
        }
        else if (gameState == 2){ // Win screen
            g2.drawImage(levelComplete, 0, 0, 900, 700, this);
            g2.setFont(new Font("Arial", Font.BOLD, 30));
            g2.drawString("Click anywhere to continue", 450, 550);
        }
        else if (gameState == -1){ // Instructions screen
            g2.drawImage(instructions, 0, 0, 900, 700, this);
        }
        else if (gameState == 4){ // Credits screen
            g2.drawImage(creditsImage, 0, 0, 900, 700, this);
        }

    }

    private boolean wasHoveringPlay    = false;
    private boolean wasHoveringIcon    = false;
    private boolean wasHoveringCreator = false;

    /**
     * Stops a clip from playing and plays current clip from start.
     * @param c Clip to be played
     */
    private void playClipFromStart(Clip c) {
        if (c == null) return; // if it cannot find the clip
        if (c.isRunning()) c.stop(); // if the clip is already running
        c.setFramePosition(0);
        c.start();
    }

    /**
     * Resets the level and player restarts from the beginning.
     */
    public void resetGame() {
        backgroundMusic2.stop(); // Restarting the song
        // Reset player position and state
        player.x = 100;
        player.y = 500;
        isOnGround = false;

        // Reset camera/scroll variables
        scrollSpeed = 7;
        cameraY = 0;

        // Reset the ship portal position
        endPort.x += playerX;
        endPort.y = 500;

        // Reset blocks/spikes if needed (example)
        for (Rectangle2D.Double spike : spikes) {
            spike.x += playerX; // or set to original position
        }
        for (Rectangle2D.Double block : blocks) {
            block.x += playerX;
        }
        for (Rectangle2D.Double hitbox: hitboxes){
            hitbox.x += playerX;
        }
        if (gameState == 2) {
            backgroundMusic1.setFramePosition(0);
            backgroundMusic1.loop(Clip.LOOP_CONTINUOUSLY);
        }
        else if (gameState == 1){
            backgroundMusic2.stop();
            backgroundMusic2.setFramePosition(0);
            backgroundMusic2.start();
        }
        // Repaint the screen and go back to menu
        playerX = 0;
        repaint();
    }

    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        // Checking to see if player jumps
        if ((code == KeyEvent.VK_SPACE || code == KeyEvent.VK_UP) && isOnGround) {
            velocityY = JUMP_VELOCITY;
            isOnGround = false;
        }
        // Pauses the game
        else if (code == KeyEvent.VK_ESCAPE){
            if (gameState == 1) {
                gameState = -2;
                if (backgroundMusic2.isRunning()) {
                    frames = backgroundMusic2.getFramePosition();
                    backgroundMusic2.stop();
                }
            }
            else if (gameState == -2){ // Exits if escape is pressed in the pause menu
                gameState = 0;
                resetGame();
                backgroundMusic1.setFramePosition(0);
                backgroundMusic1.start();
            }
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
        boolean nowHoveringInstructions = instructionsBounds != null && instructionsBounds.contains(p) && gameState == 0;
        boolean nowHoveringCredits = creditsBounds != null && creditsBounds.contains(p) && gameState == 0;
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
        if (nowHoveringInstructions && !hoveringInstructions){
            playClipFromStart(buttonHover);
        }
        if(nowHoveringCredits && !hoveringCredits){
            playClipFromStart(buttonHover);
        }

        hoveringPlay    = nowHoveringPlay;
        hoveringIcon    = nowHoveringIcon;
        hoveringCreator = nowHoveringCreator;
        hoveringInstructions = nowHoveringInstructions;
        hoveringCredits = nowHoveringCredits;
    }


    public void mouseDragged(MouseEvent e) {}
    @Override
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();

        if (gameState == 5) {
            boolean hitSelector = false;

            if (icon1Rect.contains(p)) {
                selectedIcon        = 1;
                selectedColor1Index = 2; // reset to yellow
                selectedColor2Index = 7; // reset to white
                hitSelector = true;
            } else if (icon2Rect.contains(p)) {
                selectedIcon        = 2;
                selectedColor1Index = 2;
                selectedColor2Index = 7;
                hitSelector = true;
            }

            for (int i = 0; i < outerColorRects.length; i++) {
                if (outerColorRects[i].contains(p)) {
                    selectedColor1Index = i;
                    hitSelector = true;
                    break;
                }
            }

            for (int j = 0; j < innerColorRects.length; j++) {
                if (innerColorRects[j].contains(p)) {
                    selectedColor2Index = j;
                    hitSelector = true;
                    break;
                }
            }

            if (hitSelector) {
                boolean[] o  = new boolean[COLORS.length];
                boolean[] in = new boolean[COLORS.length];
                o[selectedColor1Index] = true;
                in[selectedColor2Index] = true;
                selectedIconPath = GDiconlocater.GDiconLocater(
                        selectedIcon,
                        o[0],o[1],o[2],o[3],o[4],o[5],o[6],o[7],o[8],
                        in[0],in[1],in[2],in[3],in[4],in[5],in[6],in[7],in[8]
                );
                currentPlayerImg = Toolkit.getDefaultToolkit().getImage(selectedIconPath);
                repaint();
                return;
            }

            gameState = 0;

            // stop selector music, resume menu music
            if (iconselectormusic.isRunning()) iconselectormusic.stop();
            if (backgroundMusic1 != null) {
                backgroundMusic1.setFramePosition(0);
                backgroundMusic1.loop(Clip.LOOP_CONTINUOUSLY);
            }

            repaint();
            return;
        }

        if (gameState == 0 && playButtonBounds != null && playButtonBounds.contains(p)) {
            // play
            gameState = 1;
            noClip = false;
            backgroundMusic1.stop();
            backgroundMusic2.setFramePosition(0);
            backgroundMusic2.loop(Clip.LOOP_CONTINUOUSLY);
            repaint();
            return;
        }
        else if (gameState == 0 && iconMenuButtonBounds != null && iconMenuButtonBounds.contains(p)) {
            // enter icon selector
            gameState = 5;
            noClip = false;

            // stop menu music, start selector music
            if (backgroundMusic1.isRunning()) backgroundMusic1.stop();
            iconselectormusic.setFramePosition(0);
            iconselectormusic.loop(Clip.LOOP_CONTINUOUSLY);

            repaint();
            return;
        }
        else if (gameState == 0 && creatorMenuButtonBounds != null && creatorMenuButtonBounds.contains(p)) {
            // cheat play
            gameState = 1;
            noClip = true;
            backgroundMusic1.stop();
            backgroundMusic2.setFramePosition(0);
            backgroundMusic2.loop(Clip.LOOP_CONTINUOUSLY);
            repaint();
            return;
        }
        else if (gameState == 0 && instructionsBounds != null && instructionsBounds.contains(p)) {
            // instructions
            gameState = -1;
            backgroundMusic1.stop();
            instructionsMusic.setFramePosition(0);
            instructionsMusic.loop(Clip.LOOP_CONTINUOUSLY);
            repaint();
            return;
        }
        else if (gameState == 0 && creditsBounds != null && creditsBounds.contains(p)) {
            // credits
            gameState = 4;
            backgroundMusic1.stop();
            credits.setFramePosition(0);
            credits.loop(Clip.LOOP_CONTINUOUSLY);
            repaint();
            return;
        }

        if (gameState == -1 || gameState == 3 || gameState == 4) {
            gameState = 0;
            credits.stop();
            instructionsMusic.stop();
            icon.stop();
            if (backgroundMusic1 != null) {
                backgroundMusic1.setFramePosition(0);
                backgroundMusic1.loop(Clip.LOOP_CONTINUOUSLY);
            }
            repaint();
            return;
        }

        if (gameState == 2) {
            resetGame();
            gameState = 0;
            endMusic.stop();
            repaint();
            return;
        }

        if (gameState == -2) {
            gameState = 1;
            backgroundMusic2.setFramePosition(frames);
            backgroundMusic2.loop(Clip.LOOP_CONTINUOUSLY);
            repaint();
            return;
        }
    }




    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}