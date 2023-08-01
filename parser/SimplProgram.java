package parser;

import data.Texture;
import data.Vec2;
import data.Vec3;
import imgdisplay.ImgDisplay;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class SimplProgram {
    private static int THREAD_COUNT;

    private File file;
    private Scanner scanner;

    public Texture mainTexture;

    private String outputVector;
    private String outFile;

    private List<String> textureNames;
    private Texture[] textures;
    private HashMap<String, Vec3> vectors3;
    private HashMap<String, Vec2> vectors2;

    private HashMap<String, Vec3> args3;
    private HashMap<String, Vec2> args2;

    private List<String> vecNames;

    private List<String> labelNames;
    private List<Integer> labels;

    private boolean display;

    public SimplProgram(String filename, HashMap<String, Vec2> args2, HashMap<String, Vec3> args3, boolean display, String out, int THREAD_COUNT) {
        textures = new Texture[32];
        textureNames = new ArrayList<>();

        vectors3 = new HashMap<>();
        vectors2 = new HashMap<>();

        labels = new ArrayList<>();
        labelNames = new ArrayList<>();

        file = new File(filename);

        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.args2 = args2;
        this.args3 = args3;

        this.display = display;

        this.outFile = out;

        SimplProgram.THREAD_COUNT = THREAD_COUNT;
    }

    public List<int[]> parse() {
        List<int[]> parsed = new ArrayList<>();

        vectors2.put("uv", new Vec2(0, 0));

        for (Map.Entry<String, Vec2> entry : args2.entrySet()) {
            vectors2.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Vec3> entry : args3.entrySet()) {
            vectors3.put(entry.getKey(), entry.getValue());
        }

        this.vecNames = new ArrayList<>();

        vecNames.add("uv");

        for (Map.Entry<String, Vec3> stringVec3Entry : args3.entrySet()) {
            vecNames.add(stringVec3Entry.getKey());
        }

        for (Map.Entry<String, Vec2> stringVec2Entry : args2.entrySet()) {
            vecNames.add(stringVec2Entry.getKey());
        }

        int textureId = 0;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            line = line.trim();

            if (line.equals("__main")) break;

            String[] content = line.split(" ");

            if (content[0].equals("tex")) {
                try {
                    Texture texture = new Texture(ImageIO.read(new File(content[3])));

                    if (textureNames.contains(content[1])) {
                        textures[textureNames.indexOf(content[1])] = texture;
                    } else {
                        textures[textureId] = texture;
                        textureId++;
                    }

                    textureNames.add(content[1]);

                    if (content.length == 5) {
                        mainTexture = texture;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            line = line.trim();

            String[] content = line.split(" ");

            if (line.startsWith("out")) {
                outputVector = content[1];
                break;
            }

            int[] args = new int[6];

            boolean done = false;

            switch (content[0]) {
                case "return": {
                    args[0] = 0;
                    done = true;

                    break;
                }

                case "var": {
                    vecNames.add(content[1]);

                    if (content[3].startsWith("sample")) {
                        String[] inside = content[3].split("\\(")[1].split("\\)")[0].split(",");

                        args[0] = 1;
                        args[1] = vecNames.indexOf(content[1]);

                        args[2] = textureNames.indexOf(inside[0]);
                        args[3] = vecNames.indexOf(inside[1]);

                        vectors3.put(content[1], new Vec3(0, 0, 0));
                        done = true;
                    } else if (content[3].startsWith("[")) {
                        String[] inside = content[3].split("\\[")[1].split("\\]")[0].split(",");

                        args[0] = 2;
                        args[1] = vecNames.indexOf(content[1]);

                        args[2] = (int) (Double.parseDouble(inside[0]) * 10000f);
                        args[3] = (int) (Double.parseDouble(inside[1]) * 10000f);

                        if (inside.length == 3) {
                            args[4] = (int) (Double.parseDouble(inside[2]) * 10000f);
                        } else {
                            args[4] = 0;
                        }

                        vectors3.put(content[1], new Vec3(0, 0, 0));
                        done = true;
                    }

                    break;
                }

                case "if": {
                    args[0] = 4;

                    String varA = String.valueOf(content[1].split("\\(")[1].split("\\)")[0].split("_")[0].split("\\.")[0]);
                    String varB = String.valueOf(content[1].split("\\(")[1].split("\\)")[0].split("_")[2].split("\\.")[0]);

                    if (!vecNames.contains(varA) && !vecNames.contains(varB) &&
                            !vecNames.contains(varA) && !vecNames.contains(varB)) break;

                    if (!labelNames.contains(content[2])) {
                        labelNames.add(content[2]);
                        labels.add(0);
                    }

                    if (!labelNames.contains(content[3])) {
                        labelNames.add(content[3]);
                        labels.add(0);
                    }

                    args[4] = labelNames.indexOf(content[2]);
                    args[5] = labelNames.indexOf(content[3]);

                    String charA = content[1].split("\\(")[1].split("\\)")[0].split("_")[0].split("\\.")[1];

                    int argA = -1;

                    switch (charA) {
                        case "x":
                            argA = 0;
                            break;

                        case "y":
                            argA = 1;
                            break;

                        case "z":
                            argA = 2;
                            break;
                    }

                    args[1] = vecNames.indexOf(varA) * 3 + argA;

                    String charB = content[1].split("\\(")[1].split("\\)")[0].split("_")[0].split("\\.")[1];

                    int argB = -1;

                    switch (charB) {
                        case "x":
                            argB = 0;
                            break;

                        case "y":
                            argB = 1;
                            break;

                        case "z":
                            argB = 2;
                            break;
                    }

                    args[2] = vecNames.indexOf(varB) * 3 + argB;

                    String sign = content[1].split("\\(")[1].split("\\)")[0].split("_")[1];

                    int signIndex = -2;

                    switch (sign) {
                        case ">":
                            signIndex = 1;
                            break;
                        case "<":
                            signIndex = -1;
                            break;
                        case "==":
                            signIndex = 0;
                            break;
                        case "!=":
                            signIndex = 2;
                            break;
                    }

                    args[3] = signIndex;

                    done = true;

                    break;
                }

                case "jmp": {
                    args[0] = 5;

                    if (!labelNames.contains(content[1])) {
                        labelNames.add(content[1]);
                        labels.add(0);
                    }

                    args[1] = labelNames.indexOf(content[1]);
                    done = true;

                    break;
                }

                case "--b": {
                    args[0] = 9;
                    done = true;

                    break;
                }

                default: {
                    if (content[0].startsWith("_")) {
                        args[0] = 6;

                        int labelIndex = 0;

                        if (!labelNames.contains(content[0].substring(1))) {
                            labelNames.add(content[0].substring(1));
                            labels.add(parsed.size());
                        }

                        labels.set(labelNames.indexOf(content[0].substring(1)), parsed.size());

                        args[1] = labelIndex;
                        done = true;
                    } else if (vecNames.contains(content[0])) {
                        args[0] = 7;

                        args[1] = vecNames.indexOf(content[0]);
                        args[2] = vecNames.indexOf(content[2]);

                        String sign = content[3];
                        int signIndex = -1;

                        switch (sign) {
                            case "+":
                                signIndex = 0;
                                break;

                            case "-":
                                signIndex = 1;
                                break;

                            case "*":
                                signIndex = 2;
                                break;

                            case "/":
                                signIndex = 3;
                                break;

                            case "^":
                                signIndex = 4;
                                break;
                        }

                        args[3] = signIndex;

                        args[4] = vecNames.indexOf(content[4]);

                        if (content.length >= 6 && content[5].equals("[-c]")) args[5] = 1;

                        done = true;
                    } else if (content[0].startsWith("[") && content[0].length() > 1) {
                        String varInfoA = content[0].substring(1).split("\\]")[0];
                        String varInfoB = content[2].substring(1).split("\\]")[0];

                        int varA = vecNames.indexOf(varInfoA.split("\\.")[0]);
                        int varB = vecNames.indexOf(varInfoB.split("\\.")[0]);

                        int componentA = 0;
                        int componentB = 0;

                        String componentStringA = varInfoA.split("\\.")[1];
                        String componentStringB = varInfoB.split("\\.")[1];

                        switch (componentStringA) {
                            case "x":
                                componentA = 0;
                                break;

                            case "y":
                                componentA = 1;
                                break;

                            case "z":
                                componentA = 2;
                                break;
                        }

                        switch (componentStringB) {
                            case "x":
                                componentB = 0;
                                break;

                            case "y":
                                componentB = 1;
                                break;

                            case "z":
                                componentB = 2;
                                break;
                        }

                        args[0] = 3;

                        args[1] = varA;
                        args[2] = componentA;

                        args[3] = varB;
                        args[4] = componentB;

                        done = true;
                    }
                }
            }

            if (done) parsed.add(args);
        }

        return parsed;
    }

    private int[][] parsedToArr(List<int[]> parsed) {
        int[][] parsedArr = new int[parsed.size()][6];

        for (int j = 0; j < parsed.size(); j++) {
            parsedArr[j] = parsed.get(j);
        }

        return parsedArr;
    }

    public void runThreaded(List<int[]> parsed) {
        if (display) {
            new ImgDisplay(mainTexture.content, 1600, 900);
        }

        int MAIN_WIDTH = mainTexture.content[0].length;
        int MAIN_HEIGHT = mainTexture.content.length;

        int[][] posX = new int[THREAD_COUNT][mainTexture.content.length * mainTexture.content[0].length / THREAD_COUNT + 10];
        int[][] posY = new int[THREAD_COUNT][mainTexture.content.length * mainTexture.content[0].length / THREAD_COUNT + 10];

        int[] bucketCounts = new int[THREAD_COUNT];

        int[][] parsedArr = parsedToArr(parsed);

//        int currBucket = 0;

        for (int yCoord = 0; yCoord < MAIN_HEIGHT; yCoord++) {
            for (int xCoord = 0; xCoord < MAIN_WIDTH; xCoord++) {
                int bucket = (yCoord * MAIN_HEIGHT + xCoord) % THREAD_COUNT;
//                if(bucketCounts[currBucket] >= posX[0].length) currBucket ++;

                posX[bucket][bucketCounts[bucket]] = xCoord;
                posY[bucket][bucketCounts[bucket]] = yCoord;

                bucketCounts[bucket]++;
            }
        }

        ShaderThread[] threads = new ShaderThread[THREAD_COUNT];

        int[] labelPointers = new int[labelNames.size()];

        for (int j = 0; j < labels.size(); j++) {
            labelPointers[j] = labels.get(j);
        }

        for (int i = 0; i < THREAD_COUNT; i++) {
            ShaderThread thread = new ShaderThread(parsedArr, posX[i], posY[i], mainTexture, textures, labelPointers, vecNames.size());

            thread.mainTexture = this.mainTexture;
            thread.outputVector = this.vecNames.indexOf(outputVector);
            thread.args2 = this.args2;
            thread.args3 = this.args3;

            thread.start();

            threads[i] = thread;
        }

        for (ShaderThread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (this.outFile != null) {
            File file = new File(outFile);

            BufferedImage result = new BufferedImage(mainTexture.content[0].length, mainTexture.content.length, BufferedImage.TYPE_INT_RGB);

            for (int i = 0; i < mainTexture.content.length; i++) {
                for (int j = 0; j < mainTexture.content[0].length; j++) {
                    result.setRGB(j, i, new Color(
                            mainTexture.content[i][j][0],
                            mainTexture.content[i][j][1],
                            mainTexture.content[i][j][2]
                    ).getRGB());
                }
            }

            try {
                ImageIO.write(result, "jpg", file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
