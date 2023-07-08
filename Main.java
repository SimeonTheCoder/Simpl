import data.Vec2;
import data.Vec3;
import parser.JPSLProgram;

import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        HashMap<String, Vec2> args2 = new HashMap<>();
        HashMap<String, Vec3> args3 = new HashMap<>();

        boolean display = false;

        String out = null;
        String path = null;

        int threadCount = 8;

        for (String arg : args) {
            if(arg.equals("-d")) display = true;

            if(arg.startsWith("-o")) {
                out = arg.split("\\(")[1].split("\\)")[0];
            }

            if(arg.startsWith("-p")) {
                path = arg.split("\\(")[1].split("\\)")[0];
            }

            if(arg.startsWith("-a3")) {
                String argName = arg.split("\\(")[1].split("\\)")[0].split(",")[0];

                float a = Float.parseFloat(arg.split("\\(")[1].split("\\)")[0].split(",")[1]);
                float b = Float.parseFloat(arg.split("\\(")[1].split("\\)")[0].split(",")[2]);
                float c = Float.parseFloat(arg.split("\\(")[1].split("\\)")[0].split(",")[3]);

                args3.put(argName, new Vec3(a,b,c));
            }

            if(arg.startsWith("-a2")) {
                String argName = arg.split("\\(")[1].split("\\)")[0].split(",")[0];

                float a = Float.parseFloat(arg.split("\\(")[1].split("\\)")[0].split(",")[1]);
                float b = Float.parseFloat(arg.split("\\(")[1].split("\\)")[0].split(",")[2]);

                args2.put(argName, new Vec2(a,b));
            }

            if(arg.startsWith("-t")) {
                threadCount = Integer.parseInt(arg.substring(2));
            }
        }

        JPSLProgram parser = new JPSLProgram(path, args2, args3, display, out, threadCount);

        List<int[]> parsed = parser.parse();

        parser.runThreaded(parsed);
    }
}
