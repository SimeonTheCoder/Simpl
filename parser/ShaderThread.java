package parser;

import data.Texture;
import data.Vec2;
import data.Vec3;
import utils.VecUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShaderThread extends Thread {
    public int[][] parsed;

    public HashMap<String, Vec2> args2;
    public HashMap<String, Vec3> args3;

    private int[] coords;

    private List<String> vec3Names;
    private List<String> vec2Names;

    private int vec2Count;
    private int vec3Count;

    public Texture mainTexture;

    public boolean isDone;

    private Vec3[] vectors3;
    private Vec2[] vectors2;

    private Texture[] textures;

    private int[] labels;

    private int width;
    private int height;

    public String outputVector;

    public ShaderThread(int[] coords, Texture mainTexture, Texture[] textures,
                        List<String> vec3Names, List<String> vec2Names, int[] labels) {
        this.coords = coords;

        this.mainTexture = mainTexture;
        this.textures = textures;

        vectors3 = new Vec3[256];
        vectors2 = new Vec2[256];

        this.vec2Names = vec2Names;
        this.vec3Names = vec3Names;

        this.vec2Count = this.vec2Names.size();
        this.vec3Count = this.vec3Names.size();

        this.labels = labels;

        this.isDone = false;

        this.width = mainTexture.content.getWidth();
        this.height = mainTexture.content.getHeight();
    }

    @Override
    public void run() {
        int pointer = parsed.length - 1;

        int reached = -1;

        vectors2[0] = new Vec2(0,0);

        int arg2Index = 1;
        for (Map.Entry<String, Vec2> stringVec2Entry : args2.entrySet()) {
            vectors2[arg2Index] = stringVec2Entry.getValue();

            arg2Index++;
        }

        int arg3Index = 0;
        for (Map.Entry<String, Vec3> stringVec3Entry : args3.entrySet()) {
            vectors3[arg3Index] = stringVec3Entry.getValue();

            arg3Index++;
        }

        for (int j = 0; j < parsed.length; j++) {
            switch (parsed[j][0]) {
                case 9: {
                    reached = j;

                    break;
                }

                case 5: {
                    pointer = j;

                    j = labels[parsed[j][1]];

                    break;
                }

                case 0: {
                    j = pointer;

                    pointer = parsed.length - 1;

                    break;
                }

                case 4: {
                    double valA = 0;
                    double valB = 0;

                    if (parsed[j][1] == 1) {
                        Vec2 val = vectors2[parsed[j][6]];

                        switch (parsed[j][7]) {
                            case 0:
                                valA = val.x;

                                break;

                            case 1:
                                valA = val.y;

                                break;
                        }
                    } else if (parsed[j][1] == 0) {
                        Vec3 val = vectors3[parsed[j][6]];

                        switch (parsed[j][7]) {
                            case 0:
                                valA = val.x;

                                break;

                            case 1:
                                valA = val.y;

                                break;

                            case 2:
                                valA = val.z;

                                break;
                        }
                    }

                    if (parsed[j][2] == 1) {
                        Vec2 val = vectors2[parsed[j][8]];

                        switch (parsed[j][9]) {
                            case 0:
                                valB = val.x;

                                break;

                            case 1:
                                valB = val.y;

                                break;
                        }
                    } else if (parsed[j][2] == 0) {
                        Vec3 val = vectors3[parsed[j][8]];

                        switch (parsed[j][9]) {
                            case 0:
                                valB = val.x;

                                break;

                            case 1:
                                valB = val.y;

                                break;

                            case 2:
                                valB = val.z;

                                break;
                        }
                    }

                    boolean result = false;

                    switch (parsed[j][5]) {
                        case 0:
                            result = valA > valB;
                            break;

                        case 1:
                            result = valA < valB;
                            break;

                        case 2:
                            result = valA == valB;
                            break;

                        case 3:
                            result = valA >= valB;
                            break;

                        case 4:
                            result = valA <= valB;
                            break;

                        case 5:
                            result = valA != valB;
                            break;
                    }

                    if (result) {
                        j = labels[parsed[j][3]];
                    } else {
                        j = labels[parsed[j][4]];
                    }

                    break;
                }

                case 2: {
                    Vec3 result = new Vec3(
                            parsed[j][2] / 10000.0,
                            parsed[j][3] / 10000.0,
                            parsed[j][4] / 10000.0
                    );

                    vectors3[parsed[j][1]] = result;

                    break;
                }

                case 3: {
                    Vec2 result = new Vec2(
                            parsed[j][2] / 10000.0,
                            parsed[j][3] / 10000.0
                    );

                    vectors2[parsed[j][1]] = result;

                    break;
                }

                default: {
                    if (parsed[j][0] == 7 && parsed[j][1] < vec3Count) {
                        Vec3 vecA = vectors3[parsed[j][2]];
                        Vec3 vecB = vectors3[parsed[j][4]];

                        Vec3 res = new Vec3();

                        switch (parsed[j][3]) {
                            case 0:
                                res = vecA.add(vecB);
                                break;

                            case 1:
                                res = vecA.sub(vecB);
                                break;

                            case 2:
                                res = vecA.mul(vecB);
                                break;

                            case 3:
                                res = vecA.div(vecB);
                                break;
                        }

                        vectors3[parsed[j][1]] = res;
                    }

                    if (parsed[j][0] == 8 && (parsed[j][1]) < vec2Count) {
                        Vec2 vecA = vectors2[parsed[j][2]];
                        Vec2 vecB = vectors2[parsed[j][4]];

                        Vec2 res = new Vec2();

                        switch (parsed[j][3]) {
                            case 0:
                                res = vecA.add(vecB);
                                break;

                            case 1:
                                res = vecA.sub(vecB);
                                break;

                            case 2:
                                res = vecA.mul(vecB);
                                break;

                            case 3:
                                res = vecA.div(vecB);
                                break;
                        }

                        vectors2[parsed[j][1]] = res;
                    }

                    break;
                }
            }

            if(reached != -1) break;
        }

        for (int i = 0; i < coords.length; i++) {
            int xCoord = coords[i] % width;
            int yCoord = coords[i] / width;

            Vec2 uv = mainTexture.getUv(xCoord, yCoord);

            vectors2[0] = uv;

            pointer = parsed.length - 1;

            for (int j = reached; j < parsed.length; j++) {
                switch (parsed[j][0]) {
                    case 1: {
                        Vec3 value = textures[parsed[j][2]].getRgb(vectors2[parsed[j][3]]);
                        value = VecUtils.rgbToCol(value);

                        vectors3[parsed[j][1]] = value;

                        break;
                    }

                    case 5: {
                        pointer = j;

                        j = labels[parsed[j][1]];

                        break;
                    }

                    case 0: {
                        j = pointer;

                        pointer = parsed.length - 1;

                        break;
                    }

                    case 4: {
                        double valA = 0;
                        double valB = 0;

                        if (parsed[j][1] == 1) {
                            Vec2 val = vectors2[parsed[j][6]];

                            switch (parsed[j][7]) {
                                case 0:
                                    valA = val.x;

                                    break;

                                case 1:
                                    valA = val.y;

                                    break;
                            }
                        } else if (parsed[j][1] == 0) {
                            Vec3 val = vectors3[parsed[j][6]];

                            switch (parsed[j][7]) {
                                case 0:
                                    valA = val.x;

                                    break;

                                case 1:
                                    valA = val.y;

                                    break;

                                case 2:
                                    valA = val.z;

                                    break;
                            }
                        }

                        if (parsed[j][2] == 1) {
                            Vec2 val = vectors2[parsed[j][8]];

                            switch (parsed[j][9]) {
                                case 0:
                                    valB = val.x;

                                    break;

                                case 1:
                                    valB = val.y;

                                    break;
                            }
                        } else if (parsed[j][2] == 0) {
                            Vec3 val = vectors3[parsed[j][8]];

                            switch (parsed[j][9]) {
                                case 0:
                                    valB = val.x;

                                    break;

                                case 1:
                                    valB = val.y;

                                    break;

                                case 2:
                                    valB = val.z;

                                    break;
                            }
                        }

                        boolean result = false;

                        switch (parsed[j][5]) {
                            case 0:
                                result = valA > valB;
                                break;

                            case 1:
                                result = valA < valB;
                                break;

                            case 2:
                                result = valA == valB;
                                break;

                            case 3:
                                result = valA >= valB;
                                break;

                            case 4:
                                result = valA <= valB;
                                break;

                            case 5:
                                result = valA != valB;
                                break;
                        }

                        if (result) {
                            j = labels[parsed[j][3]];
                        } else {
                            j = labels[parsed[j][4]];
                        }

                        break;
                    }

                    case 2: {
                        Vec3 result = new Vec3(
                                parsed[j][2] / 10000.0,
                                parsed[j][3] / 10000.0,
                                parsed[j][4] / 10000.0
                        );

                        vectors3[parsed[j][1]] = result;

                        break;
                    }

                    case 3: {
                        Vec2 result = new Vec2(
                                parsed[j][2] / 10000.0,
                                parsed[j][3] / 10000.0
                        );

                        vectors2[parsed[j][1]] = result;

                        break;
                    }

                    default: {
                        if (parsed[j][0] == 7 && parsed[j][1] < vec3Count) {
                            Vec3 vecA = vectors3[parsed[j][2]];
                            Vec3 vecB = vectors3[parsed[j][4]];

                            Vec3 res = new Vec3();

                            switch (parsed[j][3]) {
                                case 0:
                                    res = vecA.add(vecB);
                                    break;

                                case 1:
                                    res = vecA.sub(vecB);
                                    break;

                                case 2:
                                    res = vecA.mul(vecB);
                                    break;

                                case 3:
                                    res = vecA.div(vecB);
                                    break;
                            }

                            vectors3[parsed[j][1]] = res;
                        }

                        if (parsed[j][0] == 8 && (parsed[j][1]) < vec2Count) {
                            Vec2 vecA = vectors2[parsed[j][2]];
                            Vec2 vecB = vectors2[parsed[j][4]];

                            Vec2 res = new Vec2();

                            switch (parsed[j][3]) {
                                case 0:
                                    res = vecA.add(vecB);
                                    break;

                                case 1:
                                    res = vecA.sub(vecB);
                                    break;

                                case 2:
                                    res = vecA.mul(vecB);
                                    break;

                                case 3:
                                    res = vecA.div(vecB);
                                    break;
                            }

                            vectors2[parsed[j][1]] = res;
                        }

                        break;
                    }
                }
            }

            mainTexture.setRgbTex(vectors3[vec3Names.indexOf(outputVector)], new Vec2(xCoord, yCoord));
        }

        this.isDone = true;
    }
}
