import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public class GDmain{
    public static void main(String[] args) {
        int w = 640;
        int h = 480;
        JFrame f = new JFrame();
        GDCanvasCreate GDCC = new GDCanvasCreate(w,h);
        f.setSize(w,h);
        f.setTitle("Geometry Dash");
        f.add(GDCC);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

}
