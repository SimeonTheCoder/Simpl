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

public class JPSLProgram {
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

    private List<String> vec3Names;
    private List<String> vec2Names;

    private List<String> labelNames;
    private List<Integer> labels;

    private boolean display;

    public JPSLProgram(String filename, HashMap<String, Vec2> args2, HashMap<String, Vec3> args3, boolean display, String out, int THREAD_COUNT) {
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

        JPSLProgram.THREAD_COUNT = THREAD_COUNT;
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

        List<String> vec3Names = new ArrayList<>();
        List<String> vec2Names = new ArrayList<>();

        vec2Names.add("uv");

        for (Map.Entry<String, Vec3> stringVec3Entry : args3.entrySet()) {
            vec3Names.add(stringVec3Entry.getKey());
        }

        for (Map.Entry<String, Vec2> stringVec2Entry : args2.entrySet()) {
            vec2Names.add(stringVec2Entry.getKey());
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

            int[] args = new int[10];
            boolean done = false;

            switch (content[0]) {
                case "return": {
                    args[0] = 0;
                    done = true;

                    break;
                }

                case "vec3": {
                    vec3Names.add(content[1]);

                    if (content[3].startsWith("sample")) {
                        String[] inside = content[3].split("\\(")[1].split("\\)")[0].split(",");

                        args[0] = 1;
                        args[1] = vec3Names.indexOf(content[1]);

                        args[2] = textureNames.indexOf(inside[0]);
                        args[3] = vec2Names.indexOf(inside[1]);

                        vectors3.put(content[1], new Vec3(0, 0, 0));
                        done = true;
                    } else if (content[3].startsWith("vec3")) {
                        String[] inside = content[3].split("\\(")[1].split("\\)")[0].split(",");

                        args[0] = 2;
                        args[1] = vec3Names.indexOf(content[1]);
                        args[2] = (int) (Double.parseDouble(inside[0]) * 10000f);
                        args[3] = (int) (Double.parseDouble(inside[1]) * 10000f);
                        args[4] = (int) (Double.parseDouble(inside[2]) * 10000f);

                        vectors3.put(content[1], new Vec3(0, 0, 0));
                        done = true;
                    }

                    break;
                }

                case "vec2": {
                    vec2Names.add(content[1]);

                    if (content[3].startsWith("vec2")) {
                        String[] inside = content[3].split("\\(")[1].split("\\)")[0].split(",");

                        args[0] = 3;
                        args[1] = vec2Names.indexOf(content[1]);
                        args[2] = (int) (Double.parseDouble(inside[0]) * 10000f);
                        args[3] = (int) (Double.parseDouble(inside[1]) * 10000f);

                        vectors2.put(content[1], new Vec2(0, 0));
                        done = true;
                    }

                    break;
                }

                case "con": {
                    args[0] = 4;

                    String varA = String.valueOf(content[1].split("\\(")[1].split("\\)")[0].split("_")[0].split("\\.")[0]);
                    String varB = String.valueOf(content[1].split("\\(")[1].split("\\)")[0].split("_")[2].split("\\.")[0]);

                    if (!vec2Names.contains(varA) && !vec2Names.contains(varB) &&
                            !vec3Names.contains(varA) && !vec3Names.contains(varB)) break;

                    if (vec3Names.contains(varA)) args[1] = 0;
                    if (vec2Names.contains(varA)) args[1] = 1;

                    if (vec3Names.contains(varB)) args[2] = 0;
                    if (vec2Names.contains(varB)) args[2] = 1;

                    if (!labelNames.contains(content[2])) {
                        labelNames.add(content[2]);
                        labels.add(0);
                    }

                    if (!labelNames.contains(content[3])) {
                        labelNames.add(content[3]);
                        labels.add(0);
                    }

                    args[3] = labelNames.indexOf(content[2]);
                    args[4] = labelNames.indexOf(content[3]);

                    String sign = content[1].split("\\(")[1].split("\\)")[0].split("_")[1];

                    int signIndex = -1;

                    switch (sign) {
                        case ">":
                            signIndex = 0;
                            break;
                        case "<":
                            signIndex = 1;
                            break;
                        case "==":
                            signIndex = 2;
                            break;
                        case ">=":
                            signIndex = 3;
                            break;
                        case "<=":
                            signIndex = 4;
                            break;
                        case "!=":
                            signIndex = 5;
                            break;
                    }

                    args[5] = signIndex;
                    args[6] = vec3Names.contains(varA) ? vec3Names.indexOf(varA) : vec2Names.indexOf(varA);

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

                    args[7] = argA;
                    args[8] = vec3Names.contains(varB) ? vec3Names.indexOf(varB) : vec2Names.indexOf(varB);

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

                    args[9] = argB;
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
                    } else if (vec3Names.contains(content[0])) {
                        args[0] = 7;

                        args[1] = vec3Names.indexOf(content[0]);
                        args[2] = vec3Names.indexOf(content[2]);

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
                        }

                        args[3] = signIndex;

                        args[4] = vec3Names.indexOf(content[4]);
                        done = true;
                    } else if (vec2Names.contains(content[0])) {
                        args[0] = 8;

                        args[1] = vec2Names.indexOf(content[0]);

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
                        }

                        args[2] = vec2Names.indexOf(content[4]);
                        args[3] = signIndex;
                        args[4] = vec2Names.indexOf(content[2]);
                        done = true;
                    }
                }
            }

            if (done) parsed.add(args);
        }

        this.vec2Names = vec2Names;
        this.vec3Names = vec3Names;

        return parsed;
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

        int[][] parsedArr = new int[parsed.size()][11];

        for (int j = 0; j < parsed.size(); j++) {
            parsedArr[j] = parsed.get(j);
        }

        for (int yCoord = 0; yCoord < MAIN_HEIGHT; yCoord++) {
            for (int xCoord = 0; xCoord < MAIN_WIDTH; xCoord++) {
                int bucket = (yCoord * MAIN_HEIGHT + xCoord) % THREAD_COUNT;

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
            ShaderThread thread = new ShaderThread(posX[i], posY[i], mainTexture, textures, labelPointers, vec2Names.size(), vec3Names.size());

            thread.parsed = parsedArr;

            thread.mainTexture = this.mainTexture;
            thread.outputVector = this.vec3Names.indexOf(outputVector);

            thread.args2 = this.args2;
            thread.args3 = this.args3;

            thread.start();

            threads[i] = thread;
        }

        boolean done;

        do {
            done = true;

            for (ShaderThread thread : threads) {
                if (!thread.isDone) done = false;
            }
        } while (!done);

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
