package imgdisplay;

import javax.swing.*;
import java.awt.*;

public class ImgDisplay extends JPanel {
    private JFrame frame;
    private int[][][] image;

    public ImgDisplay(int[][][] image, int sizeX, int sizeY) {
        this.image = image;

        frame = new JFrame("Image display");
        frame.setSize(sizeX, sizeY);

        frame.add(this);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        for(int i = 0; i < image.length; i += 2) {
            for(int j = 0; j < image[0].length; j += 2) {
                g.setColor(new Color(
                        image[i][j][0],
                        image[i][j][1],
                        image[i][j][2]
                ));

                g.fillRect(j, i, 2, 2);
            }
        }

        repaint();
    }
}
