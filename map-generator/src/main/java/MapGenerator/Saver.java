package MapGenerator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Saver {
    private static int id=1;

    public static void save(Map map) throws IOException {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("map_2_"+String.valueOf(id)+".map"))) {

            writer.write("map_size " + map.getHeight() + " " + map.getWidth());
            writer.newLine();
            int viewRadius = Math.min(map.getHeight(),map.getWidth())/7;
            writer.write("view_radius "+ viewRadius);
            writer.newLine();
            writer.write("mining_radius "+ (viewRadius-1));
            writer.newLine();
            writer.write("attack_radius "+ (viewRadius-2));
            writer.newLine();

            for(int h = map.getHeight()-1; h>=0; h--) {
                for (int w = 0; w < map.getWidth(); w++) {
                    if(map.get(h,w) == Parameter.WALL) {
                        writer.write("block "+h+" "+w);
                        writer.newLine();
                    }
                }
            }

            for(ArrayList<Integer> point : map.getSpawnpoints()) {
                writer.write("spawn_position "+point.get(0)+" "+point.get(1));
                writer.newLine();
            }

            id++;
        }
    }
}
