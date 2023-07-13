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

    public static float[] rgbToCol(float[] rgb) {
        return new float[] {
                rgb[0] / 255f,
                rgb[1] / 255f,
                rgb[2] / 255f
        };
    }
}
