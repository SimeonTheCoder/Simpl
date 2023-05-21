package parser;

import data.Texture;
import data.Vec2;
import data.Vec3;
import utils.VecUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ShaderThread extends Thread {
    public List<List<String>> parsed;

    public LinkedHashMap<String, Vec2> args2;
    public LinkedHashMap<String, Vec3> args3;

    private List<Integer> xCoords;
    private List<Integer> yCoords;

    public Texture mainTexture;

    public boolean isDone = false;

    private LinkedHashMap<String, Vec3> vectors3;
    private LinkedHashMap<String, Vec2> vectors2;

    private LinkedHashMap<String, Texture> textures;

    public String outputVector;

    public ShaderThread(List<Integer> xCoords, List<Integer> yCoords, Texture mainTexture, LinkedHashMap<String, Texture> textures) {
        this.xCoords = xCoords;
        this.yCoords = yCoords;

        this.mainTexture = mainTexture;
        this.textures = textures;

        vectors3 = new LinkedHashMap<>();
        vectors2 = new LinkedHashMap<>();

        this.isDone = false;
    }

    @Override
    public void run() {
        for (int i = 0; i < xCoords.size(); i++) {
            int xCoord = xCoords.get(i);
            int yCoord = yCoords.get(i);

            Vec2 uv = mainTexture.getUv(xCoord, yCoord);

            vectors2.put("uv", uv);

            for (Map.Entry<String, Vec2> entry : args2.entrySet()) {
                vectors2.put(entry.getKey(), entry.getValue());
            }

            for (Map.Entry<String, Vec3> entry : args3.entrySet()) {
                vectors3.put(entry.getKey(), entry.getValue());
            }

            for (List<String> strings : parsed) {
                if (strings.get(0).equals("sample")) {
                    Vec3 value = textures.get(strings.get(2)).getRgb(vectors2.get(strings.get(3)));
                    value = VecUtils.rgbToCol(value);

                    vectors3.put(strings.get(1), value);
                } else if (strings.get(0).equals("vec3")) {
                    Vec3 result = new Vec3(
                            Double.parseDouble(strings.get(2)),
                            Double.parseDouble(strings.get(3)),
                            Double.parseDouble(strings.get(4))
                    );

                    vectors3.put(strings.get(1), result);
                }

                if (strings.get(0).equals("vec2")) {
                    Vec2 result = new Vec2(
                            Double.parseDouble(strings.get(2)),
                            Double.parseDouble(strings.get(3))
                    );

                    vectors2.put(strings.get(1), result);
                }


                if (strings.get(0).equals("var") && vectors3.containsKey(strings.get(1))) {
                    Vec3 vecA = vectors3.get(strings.get(2));
                    Vec3 vecB = vectors3.get(strings.get(4));

                    Vec3 res = new Vec3();

                    switch (strings.get(3)) {
                        case "+":
                            res = vecA.add(vecB);

                            break;

                        case "-":
                            res = vecA.sub(vecB);

                            break;

                        case "*":
                            res = vecA.mul(vecB);

                            break;
                    }

                    vectors3.put(strings.get(1), res);
                }

                if (strings.get(0).equals("var") && vectors2.containsKey(strings.get(1))) {
                    Vec2 vecA = vectors2.get(strings.get(2));
                    Vec2 vecB = vectors2.get(strings.get(4));

                    Vec2 res = new Vec2();

                    switch (strings.get(3)) {
                        case "+":
                            res = vecA.add(vecB);

                            break;

                        case "-":
                            res = vecA.sub(vecB);

                            break;

                        case "*":
                            res = vecA.mul(vecB);

                            break;
                    }

                    vectors2.put(strings.get(1), res);
                }
            }

            mainTexture.setRgbTex(vectors3.get(outputVector), new Vec2(xCoord, yCoord));

            vectors3.clear();
            vectors2.clear();
        }

        this.isDone = true;
    }
}
