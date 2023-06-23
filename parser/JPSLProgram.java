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

    private List<String> vec3Names;
    private List<String> vec2Names;
    private HashMap<String, Integer> labels;

    private boolean display;

    public JPSLProgram(String filename, HashMap<String, Vec2> args2, HashMap<String, Vec3> args3, boolean display, String out, int THREAD_COUNT) {
        textures = new HashMap<>();

        vectors3 = new HashMap<>();
        vectors2 = new HashMap<>();

        labels = new HashMap<>();

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

        List<String> vec3Names = new ArrayList<>();
        List<String> vec2Names = new ArrayList<>();

        vec2Names.add("uv");

        for (Map.Entry<String, Vec3> stringVec3Entry : args3.entrySet()) {
            vec3Names.add(stringVec3Entry.getKey());
        }

        for (Map.Entry<String, Vec2> stringVec2Entry : args2.entrySet()) {
            vec2Names.add(stringVec2Entry.getKey());
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            line = line.trim();

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
            line = line.trim();

            String[] content = line.split(" ");

            if (line.startsWith("out")) {
                outputVector = content[1];
                break;
            }

            List<Object> args = new ArrayList<>();

            switch (content[0]) {
                case "return": {
                    args.add("ret");

                    parsed.add(args);

                    break;
                }

                case "vec3": {
                    vec3Names.add(content[1]);

                    if (content[3].startsWith("sample")) {
                        String[] inside = content[3].split("\\(")[1].split("\\)")[0].split(",");

                        args.add("sample");
                        args.add(vec3Names.indexOf(content[1]));

                        args.add(inside[0]);
                        args.add(vec2Names.indexOf(inside[1]));

                        parsed.add(args);

                        vectors3.put(content[1], new Vec3(0,0,0));
                    } else if (content[3].startsWith("vec3")) {
                        String[] inside = content[3].split("\\(")[1].split("\\)")[0].split(",");

                        args.add("vec3");
                        args.add(vec3Names.indexOf(content[1]));
                        args.add(Double.parseDouble(inside[0]));
                        args.add(Double.parseDouble(inside[1]));
                        args.add(Double.parseDouble(inside[2]));

                        parsed.add(args);

                        vectors3.put(content[1], new Vec3(0,0,0));
                    }

                    break;
                }

                case "vec2": {
                    vec2Names.add(content[1]);

                    if (content[3].startsWith("vec2")) {
                        String[] inside = content[3].split("\\(")[1].split("\\)")[0].split(",");

                        args.add("vec2");
                        args.add(vec2Names.indexOf(content[1]));
                        args.add(Double.parseDouble(inside[0]));
                        args.add(Double.parseDouble(inside[1]));

                        parsed.add(args);

                        vectors2.put(content[1], new Vec2(0,0));
                    }

                    break;
                }

                case "con": {
                    args.add("if");

                    String varA = String.valueOf(content[1].split("\\(")[1].split("\\)")[0].split("_")[0].split("\\.")[0]);
                    String varB = String.valueOf(content[1].split("\\(")[1].split("\\)")[0].split("_")[2].split("\\.")[0]);

                    if(!vec2Names.contains(varA) && !vec2Names.contains(varB) &&
                    !vec3Names.contains(varA) && !vec3Names.contains(varB)) break;

                    if(vec3Names.contains(varA)) args.add("vec3");
                    if(vec2Names.contains(varA)) args.add("vec2");

                    if(vec3Names.contains(varB)) args.add("vec3");
                    if(vec2Names.contains(varB)) args.add("vec2");

                    args.add(content[2]);
                    args.add(content[3]);

                    args.add(content[1].split("\\(")[1].split("\\)")[0].split("_")[1]);

                    args.add(vec3Names.contains(varA) ? vec3Names.indexOf(varA) : vec2Names.indexOf(varA));
                    args.add(content[1].split("\\(")[1].split("\\)")[0].split("_")[0].split("\\.")[1]);

                    args.add(vec3Names.contains(varB) ? vec3Names.indexOf(varB) : vec2Names.indexOf(varB));
                    args.add(content[1].split("\\(")[1].split("\\)")[0].split("_")[2].split("\\.")[1]);

                    parsed.add(args);

                    break;
                }

                case "jmp": {
                    args.add("jmp");
                    args.add(content[1]);

                    parsed.add(args);

                    break;
                }

                default: {
                    if(content[0].startsWith("_")) {
                        args.add("lab");
                        args.add(content[0].substring(1));

                        labels.put(content[0].substring(1), parsed.size());

                        parsed.add(args);
                    }

                    if (vec3Names.contains(content[0])) {
                        args.add("var3");
                        args.add(vec3Names.indexOf(content[0]));
                        args.add(vec3Names.indexOf(content[2]));
                        args.add(content[3]);
                        args.add(vec3Names.indexOf(content[4]));

                        parsed.add(args);
                    }

                    if (vec2Names.contains(content[0])) {
                        args.add("var2");
                        args.add(vec2Names.indexOf(content[0]));
                        args.add(vec2Names.indexOf(content[2]));
                        args.add(content[3]);
                        args.add(vec2Names.indexOf(content[4]));

                        parsed.add(args);
                    }
                }
            }
        }

        this.vec2Names = vec2Names;
        this.vec3Names = vec3Names;

        return parsed;
    }

    public void runThreaded(List<List<Object>> parsed) {
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
            int[] coords = new int[posX.get(i).size()];

            for(int j = 0; j < posX.get(i).size(); j ++) {
                int currX = posX.get(i).get(j);
                int currY = posY.get(i).get(j);

                int curr = currX + currY * mainTexture.content.getWidth();

                coords[j] = curr;
            }

            ShaderThread thread = new ShaderThread(coords, mainTexture, textures, vec3Names, vec2Names, labels);
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
