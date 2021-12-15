package MapGenerator;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        while(true) {

            MapGenerator mapgen = new MapGeneratorImpl();
            mapgen.setStrategy(new CellAutomataStrategy());

            Scanner scan = new Scanner(System.in);

            int height = scan.nextInt(), width = scan.nextInt();
            Map map = mapgen.generateMap(height, width);
            map.print();

            if (scan.nextInt() == 1)
                Saver.save(map);
        }
    }
}
