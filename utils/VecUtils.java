package utils;

import data.Vec2;
import data.Vec3;

public class VecUtils {
    public static Vec2 texToUV(Vec2 textureCoords, int width, int height) {
        double uvX = textureCoords.x / (width + 0.0);
        double uvY = textureCoords.y / (height + 0.0);

        return new Vec2(uvX, uvY);
    }

    public static Vec2 uvToTex(Vec2 uv, int width, int height) {
        double texX = uv.x * (width + .0);
        double texY = uv.y * (height + .0);

        return new Vec2(texX, texY);
    }

    public static Vec3 colToRGB(Vec3 col) {
        double r = col.x * 255;
        double g = col.y * 255;
        double b = col.z * 255;

        int red = Math.max(0, Math.min(255, (int) r));
        int green = Math.max(0, Math.min(255, (int) g));
        int blue = Math.max(0, Math.min(255, (int) b));

        return new Vec3(red, green, blue);
    }

    public static Vec3 rgbToCol(Vec3 rgb) {
        int r = (int) rgb.x;
        int g = (int) rgb.y;
        int b = (int) rgb.z;

        double red = r / 255.0;
        double green = g / 255.0;
        double blue = b / 255.0;

        return new Vec3(red, green, blue);
    }
}
