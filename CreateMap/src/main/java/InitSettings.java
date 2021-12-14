import java.util.List;

public class InitSettings {
    private static int WIDTH;
    private static int HEIGHT;
    private static int CountBot;
    private static List<Point> ListOfSpawn;

    public InitSettings(int WIDTH, int HEIGHT, int countBot, List<Point> listOfSpawn) {
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.CountBot = countBot;
        this.ListOfSpawn = listOfSpawn;
    }


    //задаем радиус видимости в зависимости от количества ботов
    public int getView_radius() {
        if ( CountBot <= 2) {
            return 2;
        } else if (CountBot <= 5) {
            return 3;
        } else if (CountBot <= 9) {
            return 4;
        }else {
            return 5;
        }
    }

    //задаем радиус майнинга монет в зависимости от количества ботов
    public int getMining_radius() {
        if (0 < CountBot && CountBot <= 3) {
            return 1;
        } else if (3 < CountBot && CountBot <= 7) {
            return 2;
        } else {
            return 3;
        }
    }
    //задаем радиус атаки в зависимости от количества ботов
    public int getAtack_radius() {
        if (0 < CountBot && CountBot <= 3) {
            return 1;
        } else if (3 < CountBot && CountBot <= 7) {
            return 2;
        } else {
            return 3;
        }
    }

    public int getWIDTH() {
        return WIDTH;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }
    public List<Point> getListOfSpawn(){
        return ListOfSpawn;
    }
}
