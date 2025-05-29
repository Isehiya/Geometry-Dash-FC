import javax.swing.*;

public class GDmain {
    public static void main(String[] args) {
        int w = 640;
        int h = 480;
        int targetFPS = 60;
        int frameDelay = 1000 / targetFPS;

        JFrame f = new JFrame("Geometry Dash");
        GDgraphics canvas = new GDgraphics(w, h);

        f.setSize(w, h);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(canvas);
        f.setVisible(true);

        // Game loop in a new thread
        Thread gameThread = new Thread(() -> {
            while (true) {
                long startTime = System.currentTimeMillis();

                // Update game state
                canvas.updateGame();

                // Repaint the screen
                canvas.repaint();

                // Control frame rate
                long elapsed = System.currentTimeMillis() - startTime;
                long sleepTime = frameDelay - elapsed;
                if (sleepTime < 0) sleepTime = 2; // prevent freezing

                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        gameThread.start();
    }
}
