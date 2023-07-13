package parser;

import data.Texture;
import data.Vec2;
import data.Vec3;
import utils.VecUtils;

import java.util.HashMap;
import java.util.Map;

public class ShaderThread extends Thread {
    public int PARSED_SIZE;

    public int[][] parsed;

    public HashMap<String, Vec2> args2;
    public HashMap<String, Vec3> args3;

    private int[] coordsX;
    private int[] coordsY;

    private final int vecCount;

    public Texture mainTexture;

    public boolean isDone;

    private float[][] vec;

    private final Texture[] textures;

    private final int[] labels;

    private int pointer;
    private int reached;

    public int outputVector;

    public ShaderThread(int[][] parsed, int[] coordsX, int[] coordsY, Texture mainTexture, Texture[] textures, int[] labels, int vecCount) {
        this.parsed = parsed;
        this.PARSED_SIZE = parsed.length;

        this.coordsX = coordsX;
        this.coordsY = coordsY;

        this.mainTexture = mainTexture;
        this.textures = textures;

        vec = new float[256][3];

        this.vecCount = vecCount;

        this.labels = labels;

        this.isDone = false;
    }

    @Override
    public void run() {
        this.pointer = PARSED_SIZE - 1;
        this.reached = 0;

        vec[0][0] = 0;
        vec[0][1] = 0;
        vec[0][2] = 0;

        int arg2Index = 1;
        for (Map.Entry<String, Vec2> stringVec2Entry : args2.entrySet()) {
            vec[arg2Index][0] = stringVec2Entry.getValue().x;
            vec[arg2Index][1] = stringVec2Entry.getValue().y;
            vec[arg2Index][2] = 0;

            arg2Index++;
        }

        int arg3Index = 0;
        for (Map.Entry<String, Vec3> stringVec3Entry : args3.entrySet()) {
            vec[arg3Index][0] = stringVec3Entry.getValue().x;
            vec[arg3Index][1] = stringVec3Entry.getValue().y;
            vec[arg3Index][2] = stringVec3Entry.getValue().z;

            arg3Index++;
        }

        unbranched();

        branched();

        this.isDone = true;
    }

    private int handleLogic(int j) {
        switch (parsed[j][0]) {
            case 0: {
                j = this.pointer;
                this.pointer = PARSED_SIZE - 1;
                break;
            }

            case 1: {
                j = -1;
                break;
            }

            case 2: {
                vec[parsed[j][1]][0] = parsed[j][2] / 10000f;
                vec[parsed[j][1]][1] = parsed[j][3] / 10000f;
                vec[parsed[j][1]][2] = parsed[j][4] / 10000f;

                break;
            }

            case 3: {
                vec[parsed[j][1]][parsed[j][2]] = vec[parsed[j][3]][parsed[j][4]];

                break;
            }

            case 4: {
                j = handleIf(j);

                break;
            }

            case 5: {
                this.pointer = j;
                j = labels[parsed[j][1]];
                break;
            }

            case 7: {
                arithmetics3(j);

                break;
            }

            case 9: {
                this.reached = j;
                break;
            }
        }

        return j;
    }

    private boolean boolOperations(float valA, float valB, int operator) {
        boolean res = false;

        switch (operator) {
            case 1:
                res = valA > valB;
                break;

            case 0:
                res = valA < valB;
                break;

            case -1:
                res = valA == valB;
                break;

            case 2:
                res = valA != valB;
                break;
        }

        return res;
    }

    private int handleIf(int j) {
        boolean res = boolOperations(vec[parsed[j][2]][parsed[j][3]], vec[parsed[j][4]][parsed[j][5]], parsed[j][6]);

        return res ? labels[parsed[j][7]] : labels[parsed[j][8]];
    }

    private void arithmetics3(int j) {
        switch (parsed[j][3]) {
            case 0:
                vec[parsed[j][1]][0] = vec[parsed[j][2]][0] + vec[parsed[j][4]][0];
                vec[parsed[j][1]][1] = vec[parsed[j][2]][1] + vec[parsed[j][4]][1];
                vec[parsed[j][1]][2] = vec[parsed[j][2]][2] + vec[parsed[j][4]][2];

                break;

            case 1:
                vec[parsed[j][1]][0] = vec[parsed[j][2]][0] - vec[parsed[j][4]][0];
                vec[parsed[j][1]][1] = vec[parsed[j][2]][1] - vec[parsed[j][4]][1];
                vec[parsed[j][1]][2] = vec[parsed[j][2]][2] - vec[parsed[j][4]][2];

                break;

            case 2:
                vec[parsed[j][1]][0] = vec[parsed[j][2]][0] * vec[parsed[j][4]][0];
                vec[parsed[j][1]][1] = vec[parsed[j][2]][1] * vec[parsed[j][4]][1];
                vec[parsed[j][1]][2] = vec[parsed[j][2]][2] * vec[parsed[j][4]][2];

                break;

            case 3:
                vec[parsed[j][1]][0] = vec[parsed[j][2]][0] / vec[parsed[j][4]][0];
                vec[parsed[j][1]][1] = vec[parsed[j][2]][1] / vec[parsed[j][4]][1];
                vec[parsed[j][1]][2] = vec[parsed[j][2]][2] / vec[parsed[j][4]][2];

                break;
        }
    }

    private void unbranched() {
        for (int j = this.reached; j < PARSED_SIZE; j++) {
            handleLogic(j);

            if (reached != 0) break;
        }
    }

    private void branched() {
        int l = coordsX.length;

        for (int i = 0; i < l; i++) {
            int xCoord = coordsX[i];
            int yCoord = coordsY[i];

            Vec2 uv = mainTexture.getUv(xCoord, yCoord);

            vec[0][0] = uv.x;
            vec[0][1] = uv.y;

            this.pointer = PARSED_SIZE - 1;

            for (int j = this.reached; j < PARSED_SIZE; j++) {
                int logic = handleLogic(j);

                if (logic == -1) {
                    int x = (int) Math.max(0, Math.min(textures[parsed[j][2]].WIDTH - 1, vec[0][0] * textures[parsed[j][2]].WIDTH));
                    int y = (int) Math.max(0, Math.min(textures[parsed[j][2]].HEIGHT - 1, vec[0][1] * textures[parsed[j][2]].HEIGHT));

                    vec[parsed[j][1]][0] = textures[parsed[j][2]].content[y][x][0] / 255f;
                    vec[parsed[j][1]][1] = textures[parsed[j][2]].content[y][x][1] / 255f;
                    vec[parsed[j][1]][2] = textures[parsed[j][2]].content[y][x][2] / 255f;
                } else {
                    j = logic;
                }
            }

            mainTexture.setRgbTex(vec[outputVector], new Vec2(xCoord, yCoord));
        }
    }
}