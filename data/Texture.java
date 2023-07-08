package data;

import utils.VecUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Texture {
    public int[][][] content;

    public Texture(BufferedImage content) {
        BufferedImage img = content;

        this.content = new int[img.getHeight()][img.getWidth()][3];

        for(int i = 0; i < img.getHeight(); i ++) {
            for(int j = 0; j < img.getWidth(); j ++) {
                Color col = new Color(img.getRGB(j, i));

                this.content[i][j][0] = col.getRed();
                this.content[i][j][1] = col.getGreen();
                this.content[i][j][2] = col.getBlue();
            }
        }
    }

    public void setRgbTex(float[] vec, Vec2 tex) {
        float[] realColor = VecUtils.colToRGB(vec);

        tex.x = Math.max(0, Math.min(content[0].length, tex.x));
        tex.y = Math.max(0, Math.min(content.length, tex.y));

        content[(int) tex.y][(int) tex.x][0] = (int) realColor[0];
        content[(int) tex.y][(int) tex.x][1] = (int) realColor[1];
        content[(int) tex.y][(int) tex.x][2] = (int) realColor[2];
    }

    public Vec3 getRgb(float[] uv) {
        float[] realCoords = VecUtils.uvToTex(uv, content[0].length, content.length);

        realCoords[0] = Math.max(0, Math.min(content[0].length-1, realCoords[0]));
        realCoords[1] = Math.max(0, Math.min(content.length-1, realCoords[1]));

        return new Vec3(
                content[(int) realCoords[1]][(int) realCoords[0]][0],
                content[(int) realCoords[1]][(int) realCoords[0]][1],
                content[(int) realCoords[1]][(int) realCoords[0]][2]
        );
    }

    public Vec2 getUv(int x, int y) {
        return VecUtils.texToUV(new Vec2(x, y), content[0].length, content.length);
    }
}
