import javax.swing.*;

public class GDmain {
    public static void main(String[] args) {
        final int w = 640;
        final int h = 480;
        final int targetFPS = 60;
        final int frameDelay = 1000 / targetFPS;

        JFrame f = new JFrame("Geometry Dash");
        final GDgraphics canvas = new GDgraphics(w, h);

        f.setSize(w, h);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(canvas);
        f.setVisible(true);

        // Game loop thread using anonymous inner class
        Thread gameThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    long startTime = System.currentTimeMillis();

                    // Update game logic
                    canvas.updateGame();

                    // Repaint the screen
                    canvas.repaint();

                    // Wait for next frame
                    long elapsed = System.currentTimeMillis() - startTime;
                    long sleepTime = frameDelay - elapsed;
                    if (sleepTime < 0) {
                        sleepTime = 2;
                    }

                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // Start the thread using run() inside a new thread wrapper
        new Thread(gameThread).run(); // This avoids directly calling start()
    }
}
