package parser;

import data.Texture;
import data.Vec2;
import data.Vec3;

import java.util.HashMap;
import java.util.Map;

public class ShaderThread extends Thread {
    public int PARSED_SIZE;

    public int[][] parsed;

    public HashMap<String, Vec2> args2;
    public HashMap<String, Vec3> args3;

    private int[] coordsX;
    private int[] coordsY;

    private int arg00;
    private int arg01;
    private int arg02;
    private int arg03;
    private int arg04;
    private int arg05;

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
        switch (arg00) {
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
                vec[arg01][0] = arg02 / 10000f;
                vec[arg01][1] = arg03 / 10000f;
                vec[arg01][2] = arg04 / 10000f;

                break;
            }

            case 3: {
                vec[arg01][arg02] = vec[arg03][arg04];

                break;
            }

            case 4: {
                j = handleIf(arg01, arg02, arg03, arg04, arg05);

                break;
            }

            case 5: {
                this.pointer = j;
                j = labels[arg01];

                break;
            }

            case 7: {
                arithmetics3(arg01, arg02, arg03, arg04);

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

    private int handleIf(int a, int b, int c, int d, int e) {
        boolean res = boolOperations(vec[a / 3][a % 3], vec[b / 3][b % 3], c);

        return res ? labels[d] : labels[e];
    }

    private void arithmetics3(int a, int b, int c, int d) {
        switch (c) {
            case 0:
                vec[a][0] = vec[b][0] + vec[d][0];
                vec[a][1] = vec[b][1] + vec[d][1];
                vec[a][2] = vec[b][2] + vec[d][2];

                break;

            case 1:
                vec[a][0] = vec[b][0] - vec[d][0];
                vec[a][1] = vec[b][1] - vec[d][1];
                vec[a][2] = vec[b][2] - vec[d][2];

                break;

            case 2:
                vec[a][0] = vec[b][0] * vec[d][0];
                vec[a][1] = vec[b][1] * vec[d][1];
                vec[a][2] = vec[b][2] * vec[d][2];

                break;

            case 3:
                vec[a][0] = vec[b][0] / vec[d][0];
                vec[a][1] = vec[b][1] / vec[d][1];
                vec[a][2] = vec[b][2] / vec[d][2];

                break;

            case 4:
                vec[a][0] = (float) Math.pow(vec[b][0], vec[d][0]);
                vec[a][1] = (float) Math.pow(vec[b][1], vec[d][1]);
                vec[a][2] = (float) Math.pow(vec[b][2], vec[d][2]);

                break;
        }
    }

    private void extractArgs(int j) {
        this.arg00 = parsed[j][0];
        this.arg01 = parsed[j][1];
        this.arg02 = parsed[j][2];
        this.arg03 = parsed[j][3];
        this.arg04 = parsed[j][4];
        this.arg05 = parsed[j][5];
    }

    private void unbranched() {
        for (int j = this.reached; j < PARSED_SIZE; j++) {
            extractArgs(j);

            j = handleLogic(j);

            if (reached != 0) break;
        }
    }

    private void branched() {
        int l = coordsX.length;

        for (int i = 0; i < l; i++) {
            vec[0][0] = coordsX[i] / (mainTexture.WIDTH + 0f);
            vec[0][1] = coordsY[i] / (mainTexture.HEIGHT + 0f);

            this.pointer = PARSED_SIZE - 1;

            for (int j = this.reached; j < PARSED_SIZE; j++) {
                extractArgs(j);

                int logic = handleLogic(j);

                if (logic == -1) {
                    int x = (int) Math.max(0, Math.min(textures[arg02].WIDTH - 1, vec[0][0] * textures[arg02].WIDTH));
                    int y = (int) Math.max(0, Math.min(textures[arg02].HEIGHT - 1, vec[0][1] * textures[arg02].HEIGHT));

                    vec[arg01][0] = textures[arg02].content[y][x][0] / 255f;
                    vec[arg01][1] = textures[arg02].content[y][x][1] / 255f;
                    vec[arg01][2] = textures[arg02].content[y][x][2] / 255f;
                } else {
                    j = logic;
                }
            }

            mainTexture.setRgbTex(vec[outputVector], new Vec2(coordsX[i], coordsY[i]));
        }
    }
}