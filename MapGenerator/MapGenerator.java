package MapGenerator;

public interface MapGenerator {

    public MapGenerator setStrategy(Strategy strategy);
    public Map generateMap(int height, int width);
}
