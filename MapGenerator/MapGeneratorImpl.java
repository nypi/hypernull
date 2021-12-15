package MapGenerator;

public class MapGeneratorImpl implements MapGenerator {
    private Strategy strategy;

    @Override
    public MapGenerator setStrategy(Strategy strategy) {
        this.strategy = strategy;
        return this;
    }
    @Override
    public Map generateMap(int height, int width) {
        Map map = strategy.generateMap(height, width);
        setSpawnpoints(map, Parameter.SPAWNPOINTS);
        return map;
    }

    public void setSpawnpoints(Map map, int num) {
        int y, x;
        for(int point=0; point < num/2; point++) {
            do {
                y = (int) (Math.random() * (map.getHeight()-2)+2);
                x = (int) (Math.random() * (map.getWidth()-2)+2);
            } while(!map.get(y,x));

            map.setSpawnpoint(y,x);
            map.setSpawnpoint(map.getHeight()-1-y,map.getWidth()-1-x);
        }
    }
}
