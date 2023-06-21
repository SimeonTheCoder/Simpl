package parser;

import data.Texture;
import data.Vec2;
import data.Vec3;
import utils.VecUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShaderThread extends Thread {
    public List<List<Object>> parsed;

    public HashMap<String, Vec2> args2;
    public HashMap<String, Vec3> args3;

    private List<Integer> xCoords;
    private List<Integer> yCoords;

    private List<String> vec3Names;
    private List<String> vec2Names;

    private int vec2Count = 0;
    private int vec3Count = 0;

    public Texture mainTexture;

    public boolean isDone = false;

    private Vec3[] vectors3;
    private Vec2[] vectors2;

    private HashMap<String, Texture> textures;

    private HashMap<String, Integer> labels;

    public String outputVector;

    public ShaderThread(List<Integer> xCoords, List<Integer> yCoords, Texture mainTexture, HashMap<String, Texture> textures,
                        List<String> vec3Names, List<String> vec2Names, HashMap<String, Integer> labels) {
        this.xCoords = xCoords;
        this.yCoords = yCoords;

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
    }

    @Override
    public void run() {
        for (int i = 0; i < xCoords.size(); i++) {
            int xCoord = xCoords.get(i);
            int yCoord = yCoords.get(i);

            Vec2 uv = mainTexture.getUv(xCoord, yCoord);

            vectors2[0] = uv;

            int arg2Index = 1;
            for (Map.Entry<String, Vec2> stringVec2Entry : args2.entrySet()) {
                vectors2[arg2Index] = stringVec2Entry.getValue();

                arg2Index ++;
            }

            int arg3Index = 0;
            for (Map.Entry<String, Vec3> stringVec3Entry : args3.entrySet()) {
                vectors3[arg3Index] = stringVec3Entry.getValue();

                arg3Index ++;
            }

            for (int j = 0; j < parsed.size(); j++) {
                List<Object> strings = parsed.get(j);

                if (strings.get(0).equals("sample")) {
                    Vec3 value = textures.get((String) strings.get(2)).getRgb(vectors2[(Integer) strings.get(3)]);
                    value = VecUtils.rgbToCol(value);

                    vectors3[(Integer) strings.get(1)] = value;
                }

                if(strings.get(0).equals("jmp")) {
                    j = labels.get((String) strings.get(1));
                }

                if(strings.get(0).equals("if")) {
                    double valA = 0;
                    double valB = 0;

                    if(strings.get(1).equals("vec2")) {
                        Vec2 val = vectors2[(Integer) strings.get(6)];

                        switch ((String) strings.get(7)) {
                            case "x":
                                valA = val.x;

                                break;

                            case "y":
                                valA = val.y;

                                break;
                        }
                    }else if(strings.get(1).equals("vec3")) {
                        Vec3 val = vectors3[(Integer) strings.get(6)];

                        switch ((String) strings.get(7)) {
                            case "x":
                                valA = val.x;

                                break;

                            case "y":
                                valA = val.y;

                                break;

                            case "z":
                                valA = val.z;

                                break;
                        }
                    }

                    if(strings.get(2).equals("vec2")) {
                        Vec2 val = vectors2[(Integer) strings.get(8)];

                        switch ((String) strings.get(9)) {
                            case "x":
                                valB = val.x;

                                break;

                            case "y":
                                valB = val.y;

                                break;
                        }
                    }else if(strings.get(2).equals("vec3")) {
                        Vec3 val = vectors3[(Integer) strings.get(8)];

                        switch ((String) strings.get(9)) {
                            case "x":
                                valB = val.x;

                                break;

                            case "y":
                                valB = val.y;

                                break;

                            case "z":
                                valB = val.z;

                                break;
                        }
                    }

                    boolean result = false;

                    switch ((String) strings.get(5)) {
                        case ">":
                            result = valA > valB;

                            break;

                        case "<":
                            result = valA < valB;

                            break;

                        case "==":
                            result = valA == valB;

                            break;

                        case ">=":
                            result = valA >= valB;

                            break;

                        case "<=":
                            result = valA <= valB;

                            break;

                        case "!=":
                            result = valA != valB;

                            break;
                    }

                    if(result) {
                        j = labels.get((String) strings.get(3));
                    }else{
                        j = labels.get((String) strings.get(4));
                    }
                }

                if (strings.get(0).equals("vec3")) {
                    Vec3 result = new Vec3(
                            (Double) strings.get(2),
                            (Double) strings.get(3),
                            (Double) strings.get(4)
                    );

                    vectors3[(Integer) strings.get(1)] = result;
                }

                if (strings.get(0).equals("vec2")) {
                    Vec2 result = new Vec2(
                            (Double) strings.get(2),
                            (Double) strings.get(3)
                    );

                    vectors2[(Integer) strings.get(1)] = result;
                }


                if (strings.get(0).equals("var3") && ((Integer) strings.get(1)) < vec3Count) {
                    Vec3 vecA = vectors3[(Integer) strings.get(2)];
                    Vec3 vecB = vectors3[(Integer) strings.get(4)];

                    Vec3 res = new Vec3();

                    if ("+".equals(strings.get(3))) {
                        res = vecA.add(vecB);
                    } else if ("-".equals(strings.get(3))) {
                        res = vecA.sub(vecB);
                    } else if ("*".equals(strings.get(3))) {
                        res = vecA.mul(vecB);
                    } else if ("/".equals(strings.get(3))) {
                        res = vecA.div(vecB);
                    }

                    vectors3[(Integer) strings.get(1)] = res;
                }

                if (strings.get(0).equals("var2") && ((Integer) strings.get(1)) < vec2Count) {
                    Vec2 vecA = vectors2[(Integer) strings.get(2)];
                    Vec2 vecB = vectors2[(Integer) strings.get(4)];

                    Vec2 res = new Vec2();

                    if ("+".equals(strings.get(3))) {
                        res = vecA.add(vecB);
                    } else if ("-".equals(strings.get(3))) {
                        res = vecA.sub(vecB);
                    } else if ("*".equals(strings.get(3))) {
                        res = vecA.mul(vecB);
                    } else if ("/".equals(strings.get(3))) {
                        res = vecA.div(vecB);
                    }

                    vectors2[(Integer) strings.get(1)] = res;
                }
            }

            mainTexture.setRgbTex(vectors3[vec3Names.indexOf(outputVector)], new Vec2(xCoord, yCoord));

            vectors3 = new Vec3[256];
            vectors2 = new Vec2[256];
        }

        this.isDone = true;
    }
}
