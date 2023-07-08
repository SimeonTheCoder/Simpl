package parser;

import data.Texture;
import data.Vec2;
import data.Vec3;
import utils.VecUtils;

import java.util.HashMap;
import java.util.Map;

public class ShaderThread extends Thread {
    public int[][] parsed;

    public HashMap<String, Vec2> args2;
    public HashMap<String, Vec3> args3;

    private int[] coordsX;
    private int[] coordsY;

    private int vec2Count;
    private int vec3Count;

    public Texture mainTexture;

    public boolean isDone;

    private float[][] vec3;
    private float[][] vec2;

    private Texture[] textures;

    private int[] labels;

    private int pointer;
    private int reached;

    public int outputVector;

    public ShaderThread(int[] coordsX, int[] coordsY, Texture mainTexture, Texture[] textures, int[] labels, int vec2Count, int vec3Count) {
        this.coordsX = coordsX;
        this.coordsY = coordsY;

        this.mainTexture = mainTexture;
        this.textures = textures;

        vec3 = new float[256][3];
        vec2 = new float[256][2];

        this.vec2Count = vec2Count;
        this.vec3Count = vec3Count;

        this.labels = labels;

        this.isDone = false;
    }

    @Override
    public void run() {
        this.pointer = parsed.length - 1;
        this.reached = 0;

        vec2[0][0] = 0;
        vec2[0][1] = 0;

        int arg2Index = 1;
        for (Map.Entry<String, Vec2> stringVec2Entry : args2.entrySet()) {
            vec2[arg2Index][0] = stringVec2Entry.getValue().x;
            vec2[arg2Index][1] = stringVec2Entry.getValue().y;

            arg2Index++;
        }

        int arg3Index = 0;
        for (Map.Entry<String, Vec3> stringVec3Entry : args3.entrySet()) {
            vec3[arg3Index][0] = stringVec3Entry.getValue().x;
            vec3[arg3Index][1] = stringVec3Entry.getValue().y;
            vec3[arg3Index][2] = stringVec3Entry.getValue().z;

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
                vec3[parsed[j][1]][0] = parsed[j][2] / 10000f;
                vec3[parsed[j][1]][1] = parsed[j][3] / 10000f;
                vec3[parsed[j][1]][2] = parsed[j][4] / 10000f;

                break;
            }

            case 3: {
                vec2[parsed[j][1]][0] = parsed[j][2] / 10000f;
                vec2[parsed[j][1]][1] = parsed[j][3] / 10000f;

                break;
            }

            case 7: {
                if (parsed[j][1] < vec3Count) {
                    arithmetics3(j);
                }

                break;
            }

            case 8: {
                if (parsed[j][1] < vec2Count) {
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
            valA = vec2[parsed[j][6]][parsed[j][7]];
        } else if (parsed[j][1] == 0) {
            valA = vec3[parsed[j][6]][parsed[j][7]];
        }

        if (parsed[j][2] == 1) {
            valB = vec2[parsed[j][8]][parsed[j][9]];
        } else if (parsed[j][2] == 0) {
            valB = vec3[parsed[j][8]][parsed[j][9]];
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
//        System.out.println(vec2[0][0] + ", " + vec2[0][1]);

        switch (parsed[j][3]) {
            case 0:
                vec3[parsed[j][1]][0] = vec3[parsed[j][2]][0] + vec3[parsed[j][4]][0];
                vec3[parsed[j][1]][1] = vec3[parsed[j][2]][1] + vec3[parsed[j][4]][1];
                vec3[parsed[j][1]][2] = vec3[parsed[j][2]][2] + vec3[parsed[j][4]][2];

                break;

            case 1:
                vec3[parsed[j][1]][0] = vec3[parsed[j][2]][0] - vec3[parsed[j][4]][0];
                vec3[parsed[j][1]][1] = vec3[parsed[j][2]][1] - vec3[parsed[j][4]][1];
                vec3[parsed[j][1]][2] = vec3[parsed[j][2]][2] - vec3[parsed[j][4]][2];

                break;

            case 2:
                vec3[parsed[j][1]][0] = vec3[parsed[j][2]][0] * vec3[parsed[j][4]][0];
                vec3[parsed[j][1]][1] = vec3[parsed[j][2]][1] * vec3[parsed[j][4]][1];
                vec3[parsed[j][1]][2] = vec3[parsed[j][2]][2] * vec3[parsed[j][4]][2];

                break;

            case 3:
                vec3[parsed[j][1]][0] = vec3[parsed[j][2]][0] / vec3[parsed[j][4]][0];
                vec3[parsed[j][1]][1] = vec3[parsed[j][2]][1] / vec3[parsed[j][4]][1];
                vec3[parsed[j][1]][2] = vec3[parsed[j][2]][2] / vec3[parsed[j][4]][2];

                break;
        }

    }

    private void arithmetics2(int j) {
        switch (parsed[j][3]) {
            case 0:
                vec2[parsed[j][1]][0] = vec2[parsed[j][2]][0] + vec2[parsed[j][4]][0];
                vec2[parsed[j][1]][1] = vec2[parsed[j][2]][1] + vec2[parsed[j][4]][1];

                break;

            case 1:
                vec2[parsed[j][1]][0] = vec2[parsed[j][2]][0] - vec2[parsed[j][4]][0];
                vec2[parsed[j][1]][1] = vec2[parsed[j][2]][1] - vec2[parsed[j][4]][1];

                break;

            case 2:
                vec2[parsed[j][1]][0] = vec2[parsed[j][2]][0] * vec2[parsed[j][4]][0];
                vec2[parsed[j][1]][1] = vec2[parsed[j][2]][1] * vec2[parsed[j][4]][1];

                break;

            case 3:
                vec2[parsed[j][1]][0] = vec2[parsed[j][2]][0] / vec2[parsed[j][4]][0];
                vec2[parsed[j][1]][1] = vec2[parsed[j][2]][1] / vec2[parsed[j][4]][1];

                break;
        }
    }

    private void unbranched() {
        for (int j = this.reached; j < parsed.length; j++) {
            handleLogic(j);

            if (reached != 0) break;
        }
    }

    private void branched() {
        for (int i = 0; i < coordsX.length; i++) {
            int xCoord = coordsX[i];
            int yCoord = coordsY[i];

            Vec2 uv = mainTexture.getUv(xCoord, yCoord);

            vec2[0][0] = uv.x;
            vec2[0][1] = uv.y;

            this.pointer = parsed.length - 1;

            for (int j = this.reached; j < parsed.length; j++) {
                int logic = handleLogic(j);

                if (logic == -1) {
                    Vec3 value = textures[parsed[j][2]].getRgb(vec2[parsed[j][3]]);
                    value = VecUtils.rgbToCol(value);

                    vec3[parsed[j][1]][0] = value.x;
                    vec3[parsed[j][1]][1] = value.y;
                    vec3[parsed[j][1]][2] = value.z;
                } else {
                    j = logic;
                }
            }

            mainTexture.setRgbTex(vec3[outputVector], new Vec2(xCoord, yCoord));
        }
    }
}