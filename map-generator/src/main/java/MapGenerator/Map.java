package MapGenerator;

import java.util.ArrayList;

public class Map {
    private Boolean[][] body;
    private ArrayList<ArrayList<Integer>> spawnpoints = new ArrayList<ArrayList<Integer> >();
    private int height;
    private int width;

    Map(int height, int width) {
        this.height = height;
        this.width = width;
        body = new Boolean[height][width];
        for(int h=0; h<height; h++) {
            for (int w = 0; w < width; w++) {
                body[h][w] = Parameter.FLOOR;
            }
        }
    }

    public Boolean get(int height, int width) {
        return body[height][width];
    }
    public Character getSymbol(int height, int width) {
        if(body[height][width] == Parameter.FLOOR)
            return Parameter.FLOOR_SYMBOL;
        else
            return Parameter.WALL_SYMBOL;

    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void set(int height, int width, Boolean value) {
        body[height][width] = value;
    }

    public ArrayList<ArrayList<Integer>> getSpawnpoints() {
        return this.spawnpoints;
    }

    public void setSpawnpoint(int y, int x) {
        ArrayList<Integer> point = new ArrayList<>();
        point.add(y);
        point.add(x);
        spawnpoints.add(point);
    }

    public void print() {
        for(int h=height-1; h>=0; h--) {;
            for (int w = 0; w < width; w++) {
                boolean notSpawnpoint = true;
                for(int i=0; i<spawnpoints.size(); i++) {
                    if(spawnpoints.get(i).get(0) == h && spawnpoints.get(i).get(1) == w) {
                        System.out.print("O  ");
                        notSpawnpoint = false;
                        break;
                    }
                }
                if(notSpawnpoint) System.out.print(this.getSymbol(h,w)+"  ");
            }
            System.out.println();
        }
    }

    public boolean impose(Map map) {
        if(map.getHeight() != this.height && map.getWidth() != this.width)
            return false;
        for(int h=0; h < height; h++) {
            for(int w = 0; w < width; w++) {
                if(this.body[h][w] == Parameter.FLOOR && map.get(h,w) == Parameter.FLOOR)
                    this.body[h][w] = Parameter.FLOOR;
                else
                    this.body[h][w] = Parameter.WALL;
            }
        }
        return true;
    }
}
