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

    public void setRgb(Vec3 vec, Vec2 uv) {
        Vec2 texCoords = VecUtils.uvToTex(uv, content[0].length, content.length);
        Vec3 realColor = VecUtils.colToRGB(vec);

        uv.x = Math.max(0, Math.min(1, uv.x));
        uv.y = Math.max(0, Math.min(1, uv.y));

        content[(int) texCoords.y][(int) texCoords.x][0] = (int) realColor.x;
        content[(int) texCoords.y][(int) texCoords.x][1] = (int) realColor.y;
        content[(int) texCoords.y][(int) texCoords.x][2] = (int) realColor.z;
    }

    public void setRgbTex(Vec3 vec, Vec2 tex) {
        Vec3 realColor = VecUtils.colToRGB(vec);

        tex.x = Math.max(0, Math.min(content[0].length, tex.x));
        tex.y = Math.max(0, Math.min(content.length, tex.y));

        content[(int) tex.y][(int) tex.x][0] = (int) realColor.x;
        content[(int) tex.y][(int) tex.x][1] = (int) realColor.y;
        content[(int) tex.y][(int) tex.x][2] = (int) realColor.z;
    }

    public Vec3 getRgb(Vec2 uv) {
        Vec2 realCoords = VecUtils.uvToTex(uv, content[0].length, content.length);

        realCoords.x = Math.max(0, Math.min(content[0].length-1, realCoords.x));
        realCoords.y = Math.max(0, Math.min(content.length-1, realCoords.y));

        return new Vec3(
                content[(int) realCoords.y][(int) realCoords.x][0],
                content[(int) realCoords.y][(int) realCoords.x][1],
                content[(int) realCoords.y][(int) realCoords.x][2]
        );
    }

    public Vec2 getUv(int x, int y) {
        return VecUtils.texToUV(new Vec2(x, y), content[0].length, content.length);
    }
}
