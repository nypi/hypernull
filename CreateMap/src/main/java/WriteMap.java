import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class WriteMap {
    private boolean[][] MAP;
    private int CountBot;
    private List<Point> ListOfSpawn;

    public WriteMap(boolean[][] MAP,int countBot,List<Point> listOfSpawn) {
        this.MAP = MAP;
        CountBot=countBot;
        ListOfSpawn=listOfSpawn;
    }


    public void writeMap() throws IOException {
        InitSettings settings=new InitSettings(MAP.length,MAP[0].length,CountBot,ListOfSpawn);
        String nameOfFile="maze_"+CountBot+"_6.map";
        Writer writerOnTxt=new FileWriter("/Users/alekseyzhizhin/Documents/GitHub/hypernull/CreateMap/src/main/java/maze/"+nameOfFile);
        writerOnTxt.write("map_size "+settings.getWIDTH()+" "+settings.getHEIGHT()+"\n");
        writerOnTxt.write("view_radius "+settings.getViewRadius()+"\n");
        writerOnTxt.write("mining_radius "+settings.getMiningRadius()+"\n");
        writerOnTxt.write("attack_radius "+settings.getAtackRadius()+"\n");

        for (int i = 0; i < MAP.length; i++) {
            for (int j = 0; j < MAP[0].length; j++) {
                if (MAP[i][j]) {
                    writerOnTxt.write("Block " + i + " " + j + "\n");
                }else {
                }
            }
        }
        for (int i=0;i<ListOfSpawn.size();i++){
            writerOnTxt.write("spawn_position "+ListOfSpawn.get(i).getX()+" "+ListOfSpawn.get(i).getY()+"\n");
        }
        writerOnTxt.flush();
        writerOnTxt.close();
    }

}
