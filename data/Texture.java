package data;

import utils.VecUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Texture {
    public final int[][][] content;
    
    public final int WIDTH;
    public final int HEIGHT;

    public Texture(BufferedImage content) {
        this.content = new int[content.getHeight()][content.getWidth()][3];
        
        this.WIDTH = content.getWidth();
        this.HEIGHT = content.getWidth();

        for (int i = 0; i < content.getHeight(); i++) {
            for (int j = 0; j < content.getWidth(); j++) {
                Color col = new Color(content.getRGB(j, i));

                this.content[i][j][0] = col.getRed();
                this.content[i][j][1] = col.getGreen();
                this.content[i][j][2] = col.getBlue();
            }
        }
    }

    public void setRgbTex(float[] vec, Vec2 tex) {
        float[] realColor = VecUtils.colToRGB(vec);

        int y = (int) tex.y;
        int x = (int) tex.x;

        content[y][x][0] = (int) realColor[0];
        content[y][x][1] = (int) realColor[1];
        content[y][x][2] = (int) realColor[2];
    }

    public float[] getRgb(float xUv, float yUv) {
        int x = (int) Math.max(0, Math.min(WIDTH - 1, xUv * WIDTH));
        int y = (int) Math.max(0, Math.min(HEIGHT - 1, yUv * HEIGHT));

        return new float[]{
                content[y][x][0],
                content[y][x][1],
                content[y][x][2]
        };
    }

    public Vec2 getUv(int x, int y) {
        return VecUtils.texToUV(new Vec2(x, y), WIDTH, HEIGHT);
    }
}
