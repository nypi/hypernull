package MapGenerator;

public class CellAutomataStrategy implements Strategy {

    public void makeLikeNeighbours(Map map, int y, int x) {
        int difference = 0; //насколько больше стенок, чем полов

        int height = map.getHeight();
        int width = map.getWidth();

        for(int dx=-1; dx<2; dx++) {
            for (int dy=-1; dy < 2; dy++) {

                if(dx==0 && dy==0) continue;

                int Y=0, X=0;
                if(0 <= y+dy && y+dy < height) Y = y+dy;
                if(0 <= x+dx && x+dx < width) X = x+dx;
                if(y+dy < 0 ) Y = height+y+dy;
                if(y+dy >= height) Y = y+dy-height;
                if(x+dx < 0) X = width+x+dx;
                if(x+dx >= width) X = x+dx-width;

                difference += checkNeighbour(map, Y,X);
            }
        }
        if(difference >= -1 )
            map.set(y, x, Parameter.WALL);
        else
            map.set(y, x, Parameter.FLOOR);
    }

    public int checkNeighbour(Map map, int y, int x) {
        if(map.get(y,x) == Parameter.FLOOR)
            return -1;
        if(map.get(y,x) == Parameter.WALL)
            return +1;
        else return 0;
    }

    public void mirrorCell(Map map, int h, int w) {
        Boolean value = map.get(h, w);
        map.set(map.getHeight() - 1 - h, w, value);
        map.set(h, map.getWidth() - 1 - w, value);
        map.set(map.getHeight() - 1 - h, map.getWidth() - 1 - w, value);
    }

    @Override
    public Map generateMap(int originalHeight, int originalWidth) {
        Map resultMap = new Map(originalHeight, originalWidth);

        int height = originalHeight/2 + originalHeight%2;
        int width = originalWidth/2 + originalWidth%2;

        for(int k=0; k< Parameter.LAYER_DEPTH; ++k) { //наложение сгенерированных карт друг на друга
            Map map = new Map(originalHeight, originalWidth);
            for (int initials = 0; initials <  height * width * Parameter.INITIAL_PERCENTAGE / 100.0; initials++) {
                int y = (int) (Math.random() * (height-2)+1);
                int x = (int) (Math.random() * (width-2)+1);
                map.set(y, x, Parameter.WALL);
            }
            for (int i = 0; i < Parameter.AUTOMATA_DEPTH; ++i) { // глубина просчета клеток автоматом
                for (int h = height - 1; h >= 0; h--) {
                    for (int w = 0; w < width; w++) {
                        makeLikeNeighbours(map, h, w);
                        mirrorCell(map, h, w);
                    }
                }
            }
            resultMap.impose(map);
        }
        return resultMap;
    }
}
