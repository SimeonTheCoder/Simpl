package parser;

import data.Texture;
import data.Vec2;
import data.Vec3;
import utils.VecUtils;

import java.util.HashMap;
import java.util.List;

public class ShaderThread extends Thread {
    public List<List<Object>> parsed;

    public HashMap<String, Vec2> args2;
    public HashMap<String, Vec3> args3;

    private List<Integer> xCoords;
    private List<Integer> yCoords;

    public Texture mainTexture;

    public boolean isDone = false;

    private HashMap<String, Vec3> vectors3;
    private HashMap<String, Vec2> vectors2;

    private HashMap<String, Texture> textures;

    public String outputVector;

    public ShaderThread(List<Integer> xCoords, List<Integer> yCoords, Texture mainTexture, HashMap<String, Texture> textures) {
        this.xCoords = xCoords;
        this.yCoords = yCoords;

        this.mainTexture = mainTexture;
        this.textures = textures;

        vectors3 = new HashMap<>();
        vectors2 = new HashMap<>();

        this.isDone = false;
    }

    @Override
    public void run() {
        for (int i = 0; i < xCoords.size(); i++) {
            int xCoord = xCoords.get(i);
            int yCoord = yCoords.get(i);

            Vec2 uv = mainTexture.getUv(xCoord, yCoord);

            vectors2.put("uv", uv);

            vectors2.putAll(args2);
            vectors3.putAll(args3);

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


                if (strings.get(0).equals("var") && vectors3.containsKey((String) strings.get(1))) {
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

                if (strings.get(0).equals("var") && vectors2.containsKey((String) strings.get(1))) {
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

        this.isDone = true;
    }
}
