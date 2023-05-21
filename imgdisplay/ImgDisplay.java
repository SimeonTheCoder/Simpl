package imgdisplay;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ImgDisplay extends JPanel {
    private JFrame frame;
    private Image image;

    public ImgDisplay(Image image, int sizeX, int sizeY) {
        this.image = image;

        frame = new JFrame("Image display");
        frame.setSize(sizeX, sizeY);

        frame.add(this);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public ImgDisplay(String path, int sizeX, int sizeY) {
        try {
            this.image = ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        frame = new JFrame("Image display");
        frame.setSize(sizeX, sizeY);

        frame.add(this);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.drawImage(image, 0, 0, frame.getWidth(), frame.getHeight(),null);

        repaint();
    }
}
