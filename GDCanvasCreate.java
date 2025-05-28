import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public class GDCanvasCreate extends JComponent{
        private int frameWidth;
        private int frameHeight;

        public GDCanvasCreate(int w, int h){
            frameWidth = w;
            frameHeight = h;
        }
        protected void paintComponent(Graphics g){
            Graphics2D g2d = (Graphics2D) g;

        }

}
