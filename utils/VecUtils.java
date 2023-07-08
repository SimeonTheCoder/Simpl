package utils;

import data.Vec2;
import data.Vec3;

public class VecUtils {
    public static Vec2 texToUV(Vec2 textureCoords, int width, int height) {
        float uvX = textureCoords.x / (width + 0f);
        float uvY = textureCoords.y / (height + 0f);

        return new Vec2(uvX, uvY);
    }

    public static float[] uvToTex(float[] uv, int width, int height) {
        return new float[] {
                uv[0] * width,
                uv[1] * height
        };
    }

    public static float[] colToRGB(float[] vec) {
        float[] col = new float[] {
                vec[0],
                vec[1],
                vec[2]
        };

        col[0] *= 255;
        col[1] *= 255;
        col[2] *= 255;

        col[0] = (int) Math.max(0, Math.min(255, col[0]));
        col[1] = (int) Math.max(0, Math.min(255, col[1]));
        col[2] = (int) Math.max(0, Math.min(255, col[2]));

        return col;
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
