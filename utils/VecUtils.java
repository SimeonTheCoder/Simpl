package utils;

import data.Vec2;
import data.Vec3;

public class VecUtils {
    public static Vec2 texToUV(Vec2 textureCoords, int width, int height) {
        float uvX = textureCoords.x / (width + 0f);
        float uvY = textureCoords.y / (height + 0f);

        return new Vec2(uvX, uvY);
    }

    public static Vec2 uvToTex(Vec2 uv, int width, int height) {
        float texX = uv.x * (width + .0f);
        float texY = uv.y * (height + .0f);

        return new Vec2(texX, texY);
    }

    public static Vec3 colToRGB(Vec3 col) {
        float r = col.x * 255;
        float g = col.y * 255;
        float b = col.z * 255;

        int red = Math.max(0, Math.min(255, (int) r));
        int green = Math.max(0, Math.min(255, (int) g));
        int blue = Math.max(0, Math.min(255, (int) b));

        return new Vec3(red, green, blue);
    }

    public static Vec3 rgbToCol(Vec3 rgb) {
        int r = (int) rgb.x;
        int g = (int) rgb.y;
        int b = (int) rgb.z;

        float red = r / 255f;
        float green = g / 255f;
        float blue = b / 255f;

        return new Vec3(red, green, blue);
    }
}
