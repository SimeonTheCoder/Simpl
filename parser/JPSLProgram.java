package parser;

import data.Texture;
import data.Vec2;
import data.Vec3;
import imgdisplay.ImgDisplay;
import utils.VecUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class JPSLProgram {
    private static int THREAD_COUNT;

    private File file;
    private Scanner scanner;

    public Texture mainTexture;

    private String outputVector;
    private String outFile;

    private HashMap<String, Texture> textures;
    private HashMap<String, Vec3> vectors3;
    private HashMap<String, Vec2> vectors2;

    private HashMap<String, Vec3> args3;
    private HashMap<String, Vec2> args2;

    private boolean display;

    public JPSLProgram(String filename, HashMap<String, Vec2> args2, HashMap<String, Vec3> args3, boolean display, String out, int THREAD_COUNT) {
        textures = new HashMap<>();

        vectors3 = new HashMap<>();
        vectors2 = new HashMap<>();

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

        this.THREAD_COUNT = THREAD_COUNT;
    }

    public List<List<Object>> parse() {
        List<List<Object>> parsed = new ArrayList<>();

        vectors2.put("uv", new Vec2(0, 0));

        for (Map.Entry<String, Vec2> entry : args2.entrySet()) {
            vectors2.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Vec3> entry : args3.entrySet()) {
            vectors3.put(entry.getKey(), entry.getValue());
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (line.equals("__main")) break;

            String[] content = line.split(" ");

            switch (content[0]) {
                case "tex": {
                    try {
                        Texture texture = new Texture(ImageIO.read(new File(content[3])));

                        textures.put(
                                content[1],
                                texture
                        );

                        if (content.length == 5) {
                            mainTexture = texture;
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    break;
                }
            }
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            String[] content = line.split(" ");

            if (line.startsWith("out")) {
                outputVector = content[1];
                break;
            }

            List<Object> args = new ArrayList<>();

            switch (content[0]) {
                case "vec3": {
                    if (content[3].startsWith("sample")) {
                        String[] inside = content[3].split("\\(")[1].split("\\)")[0].split(",");

                        args.add("sample");
                        args.add(content[1]);

                        args.add(inside[0]);
                        args.add(inside[1]);

                        parsed.add(args);

                        vectors3.put(content[1], new Vec3(0,0,0));
                    } else if (content[3].startsWith("vec3")) {
                        String[] inside = content[3].split("\\(")[1].split("\\)")[0].split(",");

                        args.add("vec3");
                        args.add(content[1]);
                        args.add(Double.parseDouble(inside[0]));
                        args.add(Double.parseDouble(inside[1]));
                        args.add(Double.parseDouble(inside[2]));

                        parsed.add(args);

                        vectors3.put(content[1], new Vec3(0,0,0));
                    }

                    break;
                }

                case "vec2": {
                    if (content[3].startsWith("vec2")) {
                        String[] inside = content[3].split("\\(")[1].split("\\)")[0].split(",");

                        args.add("vec2");
                        args.add(content[1]);
                        args.add(Double.parseDouble(inside[0]));
                        args.add(Double.parseDouble(inside[1]));

                        parsed.add(args);

                        vectors2.put(content[1], new Vec2(0,0));
                    }

                    break;
                }

                default: {
                    if (vectors3.containsKey(content[0]) || vectors2.containsKey(content[0])) {
                        args.add("var");
                        args.add(content[0]);
                        args.add(content[2]);
                        args.add(content[3]);
                        args.add(content[4]);

                        parsed.add(args);
                    }
                }
            }
        }

        return parsed;
    }

    public void run() {
        List<List<Object>> parsed = parse();

        if (display) {
            ImgDisplay display = new ImgDisplay(mainTexture.content, 1600, 900);
        }

        int MAIN_WIDTH = mainTexture.content.getWidth();
        int MAIN_HEIGHT = mainTexture.content.getHeight();

        vectors3.clear();
        vectors2.clear();

        for (int yCoord = 0; yCoord < MAIN_HEIGHT; yCoord++) {
            for (int xCoord = 0; xCoord < MAIN_WIDTH; xCoord++) {
                Vec2 uv = mainTexture.getUv(xCoord, yCoord);

                vectors2.put("uv", uv);

                for (Map.Entry<String, Vec2> entry : args2.entrySet()) {
                    vectors2.put(entry.getKey(), entry.getValue());
                }

                for (Map.Entry<String, Vec3> entry : args3.entrySet()) {
                    vectors3.put(entry.getKey(), entry.getValue());
                }

                for (List<Object> strings : parsed) {
                    if (strings.get(0).equals("sample")) {
                        Vec3 value = textures.get((String) strings.get(2)).getRgb(vectors2.get((String) strings.get(3)));
                        value = VecUtils.rgbToCol(value);

                        vectors3.put((String) strings.get(1), value);
                    } else if (strings.get(0).equals("vec3")) {
                        Vec3 result = new Vec3(
                                (Double) strings.get(2),
                                (Double) strings.get(3),
                                (Double) strings.get(4)
                        );

                        vectors3.put((String) strings.get(1), result);
                    }

                    if (strings.get(0).equals("vec2")) {
                        Vec2 result = new Vec2(
                                (Double) strings.get(2),
                                (Double) strings.get(3)
                        );

                        vectors2.put((String) strings.get(1), result);
                    }


                    if (strings.get(0).equals("var") && vectors3.containsKey(strings.get(1))) {
                        Vec3 vecA = vectors3.get((String) strings.get(2));
                        Vec3 vecB = vectors3.get((String) strings.get(4));

                        Vec3 res = new Vec3();

                        if ("+".equals(strings.get(3))) {
                            res = vecA.add(vecB);
                        } else if ("-".equals(strings.get(3))) {
                            res = vecA.sub(vecB);
                        } else if ("*".equals(strings.get(3))) {
                            res = vecA.mul(vecB);
                        }

                        vectors3.put((String) strings.get(1), res);
                    }

                    if (strings.get(0).equals("var") && vectors2.containsKey(strings.get(1))) {
                        Vec2 vecA = vectors2.get((String) strings.get(2));
                        Vec2 vecB = vectors2.get((String) strings.get(4));

                        Vec2 res = new Vec2();

                        if ("+".equals(strings.get(3))) {
                            res = vecA.add(vecB);
                        } else if ("-".equals(strings.get(3))) {
                            res = vecA.sub(vecB);
                        } else if ("*".equals(strings.get(3))) {
                            res = vecA.mul(vecB);
                        }

                        vectors2.put((String) strings.get(1), res);
                    }
                }

                mainTexture.setRgbTex(vectors3.get(outputVector), new Vec2(xCoord, yCoord));

                vectors3.clear();
                vectors2.clear();
            }
        }
    }

    public void runThreaded() {
        List<List<Object>> parsed = parse();

        if(display) {
            ImgDisplay display = new ImgDisplay(mainTexture.content, 1600, 900);
        }

        int MAIN_WIDTH = mainTexture.content.getWidth();
        int MAIN_HEIGHT = mainTexture.content.getHeight();

        List<String> lines = new ArrayList<>();

        List<List<Integer>> posX = new ArrayList<>();
        List<List<Integer>> posY = new ArrayList<>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            posX.add(new ArrayList<>());
            posY.add(new ArrayList<>());
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            lines.add(line);
        }

        for (int yCoord = 0; yCoord < MAIN_HEIGHT; yCoord++) {
            for (int xCoord = 0; xCoord < MAIN_WIDTH; xCoord++) {
                int bucket = (yCoord * MAIN_HEIGHT + xCoord) % THREAD_COUNT;

                posX.get(bucket).add(xCoord);
                posY.get(bucket).add(yCoord);
            }
        }

        List<ShaderThread> threads = new ArrayList<>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            ShaderThread thread = new ShaderThread(posX.get(i), posY.get(i), mainTexture, textures);
            thread.parsed = parsed;

            thread.mainTexture = this.mainTexture;
            thread.outputVector = this.outputVector;

            thread.args2 = this.args2;
            thread.args3 = this.args3;

            thread.start();

            threads.add(thread);
        }

        boolean done;

        do{
            done = true;

            for (ShaderThread thread : threads) {
                if(!thread.isDone) done = false;
            }
        }while(!done);

        if(this.outFile != null) {
            File file = new File(outFile);

            try {
                ImageIO.write(mainTexture.content, "jpg", file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
