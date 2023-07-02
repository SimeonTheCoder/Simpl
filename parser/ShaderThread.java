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

    private int pointer;
    private int reached;

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

        this.width = mainTexture.content[0].length;
        this.height = mainTexture.content.length;
    }

    @Override
    public void run() {
        this.pointer = parsed.length - 1;
        this.reached = 0;

        vectors2[0] = new Vec2(0, 0);

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

        unbranched();

        branched();

        this.isDone = true;
    }

    private int handleLogic(int j) {
        switch (parsed[j][0]) {
            case 1: {
                return -1;
            }

            case 9: {
                this.reached = j;
                break;
            }

            case 5: {
                this.pointer = j;
                j = labels[parsed[j][1]];
                break;
            }

            case 0: {
                j = this.pointer;
                this.pointer = parsed.length - 1;
                break;
            }

            case 4: {
                j = handleIf(j);

                break;
            }

            case 2: {
                Vec3 result = new Vec3(
                        parsed[j][2] / 10000f,
                        parsed[j][3] / 10000f,
                        parsed[j][4] / 10000f
                );

                vectors3[parsed[j][1]] = result;

                break;
            }

            case 3: {
                Vec2 result = new Vec2(
                        parsed[j][2] / 10000f,
                        parsed[j][3] / 10000f
                );

                vectors2[parsed[j][1]] = result;

                break;
            }

            case 7: {
                if(parsed[j][1] < vec3Count) {
                    arithmetics3(j);
                }

                break;
            }

            case 8: {
                if(parsed[j][1] < vec2Count) {
                    arithmetics2(j);
                }

                break;
            }
        }

        return j;
    }

    private boolean boolOperations(float valA, float valB, int operator) {
        switch (operator) {
            case 0:
                return valA > valB;
            case 1:
                return valA < valB;
            case 2:
                return valA == valB;
            case 3:
                return valA >= valB;
            case 4:
                return valA <= valB;
            case 5:
                return valA != valB;
        }

        return false;
    }

    private int handleIf(int j) {
        float valA = 0;
        float valB = 0;

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

        boolean result = boolOperations(valA, valB, parsed[j][5]);

        if (result) {
            j = labels[parsed[j][3]];
        } else {
            j = labels[parsed[j][4]];
        }

        return j;
    }

    private void arithmetics3(int j) {
        Vec3 vecA = vectors3[parsed[j][2]];
        Vec3 vecB = vectors3[parsed[j][4]];

        float[] valsA = new float[]{
            vecA.x, vecA.y, vecA.z
        };

        float[] valsB = new float[]{
                vecB.x, vecB.y, vecB.z
        };

        switch (parsed[j][3]) {
            case 0:
                valsA[0] += valsB[0];
                valsA[1] += valsB[1];
                valsA[2] += valsB[2];

                break;

            case 1:
                valsA[0] -= valsB[0];
                valsA[1] -= valsB[1];
                valsA[2] -= valsB[2];

                break;

            case 2:
                valsA[0] = valsA[0] * valsB[0];
                valsA[1] = valsA[1] * valsB[1];
                valsA[2] = valsA[2] * valsB[2];

                break;

            case 3:
                valsA[0] = valsA[0] / valsB[0];
                valsA[1] = valsA[1] / valsB[1];
                valsA[2] = valsA[2] / valsB[2];

                break;
        }

        vectors3[parsed[j][1]] = new Vec3(valsA[0], valsA[1], valsA[2]);
    }

    private void arithmetics2(int j) {
        Vec2 vecA = vectors2[parsed[j][2]];
        Vec2 vecB = vectors2[parsed[j][4]];

        float[] valsA = new float[]{
                vecA.x, vecA.y
        };

        float[] valsB = new float[]{
                vecB.x, vecB.y
        };

        switch (parsed[j][3]) {
            case 0:
                valsA[0] += valsB[0];
                valsA[1] += valsB[1];

                break;

            case 1:
                valsA[0] -= valsB[0];
                valsA[1] -= valsB[1];

                break;

            case 2:
                valsA[0] = valsA[0] * valsB[0];
                valsA[1] = valsA[1] * valsB[1];

                break;

            case 3:
                valsA[0] = valsA[0] / valsB[0];
                valsA[1] = valsA[1] / valsB[1];

                break;
        }

        vectors2[parsed[j][1]] = new Vec2(valsA[0], valsA[1]);
    }

    private void unbranched() {
        for (int j = this.reached; j < parsed.length; j++) {
            handleLogic(j);

            if (reached != -1) break;
        }
    }

    private void branched() {
        for (int i = 0; i < coords.length; i++) {
            int xCoord = coords[i] % width;
            int yCoord = coords[i] / width;

            Vec2 uv = mainTexture.getUv(xCoord, yCoord);

            vectors2[0] = uv;

            this.pointer = parsed.length - 1;

            for (int j = this.reached; j < parsed.length; j++) {
                int logic = handleLogic(j);

                if (logic == -1) {
                    Vec3 value = textures[parsed[j][2]].getRgb(vectors2[parsed[j][3]]);
                    value = VecUtils.rgbToCol(value);

                    vectors3[parsed[j][1]] = value;
                } else {
                    j = logic;
                }
            }

            mainTexture.setRgbTex(vectors3[vec3Names.indexOf(outputVector)], new Vec2(xCoord, yCoord));
        }
    }
}
