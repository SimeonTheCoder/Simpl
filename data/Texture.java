package data;

import utils.VecUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Texture {
    public BufferedImage content;

    public Texture(BufferedImage content) {
        this.content = content;
    }

    public Texture() {

    }

    public void setRgb(Vec3 vec, Vec2 uv) {
        Vec2 texCoords = VecUtils.uvToTex(uv, content.getWidth(), content.getHeight());
        Vec3 realColor = VecUtils.colToRGB(vec);

        uv.x = Math.max(0, Math.min(1, uv.x));
        uv.y = Math.max(0, Math.min(1, uv.y));

        content.setRGB(
                (int) texCoords.x,
                (int) texCoords.y,
                new Color(
                        (int) realColor.x,
                        (int) realColor.y,
                        (int) realColor.z
                ).getRGB()
        );
    }

    public void setRgbTex(Vec3 vec, Vec2 tex) {
        Vec3 realColor = VecUtils.colToRGB(vec);

        tex.x = Math.max(0, Math.min(content.getWidth(), tex.x));
        tex.y = Math.max(0, Math.min(content.getHeight(), tex.y));

        content.setRGB(
                (int) tex.x,
                (int) tex.y,
                new Color(
                        (int) realColor.x,
                        (int) realColor.y,
                        (int) realColor.z
                ).getRGB()
        );
    }

    public Vec3 getRgb(Vec2 uv) {
        Vec2 realCoords = VecUtils.uvToTex(uv, content.getWidth(), content.getHeight());

        realCoords.x = Math.max(0, Math.min(content.getWidth()-1, realCoords.x));
        realCoords.y = Math.max(0, Math.min(content.getHeight()-1, realCoords.y));

        Color col = new Color(content.getRGB((int) realCoords.x, (int) realCoords.y));

        return new Vec3(col.getRed(), col.getGreen(), col.getBlue());
    }

    public Vec2 getUv(int x, int y) {
        return VecUtils.texToUV(new Vec2(x, y), content.getWidth(), content.getHeight());
    }
}
