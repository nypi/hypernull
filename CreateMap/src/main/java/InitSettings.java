import java.util.List;

public class InitSettings {
    private int WIDTH;
    private int HEIGHT;
    private int CountBot;
    private List<Point> ListOfSpawn;

    public InitSettings(int WIDTH, int HEIGHT, int countBot, List<Point> listOfSpawn) {
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.CountBot = countBot;
        this.ListOfSpawn = listOfSpawn;
    }


    //задаем радиус видимости в зависимости от количества ботов
    public int getViewRadius() {
        if ( CountBot <= 4) {
            return 4;
        } else if (CountBot <= 8) {
            return 7;
        } else if (CountBot <= 12) {
            return 10;
        }else {
            return 13;
        }
    }

    //задаем радиус майнинга монет в зависимости от количества ботов
    public int getMiningRadius() {
        if ( CountBot <= 4) {
            return 2;
        } else if (CountBot <= 8) {
            return 3;
        } else {
            return 5;
        }
    }
    //задаем радиус атаки в зависимости от количества ботов
    public int getAtackRadius() {
        if ( CountBot <= 4) {
            return 2;
        } else if (CountBot <= 8) {
            return 3;
        } else {
            return 5;
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
