import java.io.IOException;
import java.util.*;

public class CreateMap {
    private static int MAP_WIDTH;
    private static int MAP_HEIGHT;
    private static boolean[][] MAP;
    private static Queue<Point> queuePoint;
    private static int countBot;
    public static List<Point> listOfSpawn;


    public CreateMap(int height, int width) {
        this.MAP_WIDTH = width;
        this.MAP_HEIGHT = height;

        MAP = new boolean[height][width];
        queuePoint = new ArrayDeque<>();

        int rndX = (int) (Math.random() * MAP_HEIGHT);
        int rndY = (int) (Math.random() * MAP_WIDTH);
        queuePoint.add(new Point(rndX, rndY));

        createLabirint();
        setSpawnBot();
        PrintMap();
    }


    /*
     * метод реализует создание идеального лабиринта
     * Логика метода: На вход передается случайная точка, метод определяет в какие точки можно пойти из это точки,
     * создает список этих точек, случайным образом выбирает в какую точку может пойти, удаляет выбранную точку
     * из нашего списка, отправляет наш список в очередь точек, для выбранной точки метод начинается сначала
     * */
    public static void createLabirint() {
        while (!queuePoint.isEmpty()) {
            //получаем точку
            Point point = queuePoint.poll();
            MAP[point.getX()][point.getY()] = true;
            //добавляем точки, в которые можем пойти из начальной точки
            List<Point> list = addPointsList(point);
            //выбираем в какую точку отправимся
            int rnd = (int) (Math.random() * list.size());
            Point newPoint;
            //если список пуст, значит нет доступных точек, пропускаем ход
            if (list.size() != 0) {
                newPoint = list.get(rnd);
                list.remove(rnd);
                queuePoint.addAll(list);
            } else {
                continue;
            }
            //выбираем случайным образом в какую сторону сдвинемся от полученной точки
            // (влево/вправо по Х, или вверх/вниз по У)
            int rnd2 = (int) (Math.random() * 4);
            switch (rnd2) {
                //Сдвигаемся в зависимости от полученного рандомного положения
                case (0):
                    if ((0 <= newPoint.getX() - 2 && newPoint.getX() + 2 < MAP_HEIGHT) && !MAP[newPoint.getX() - 2][newPoint.getY()]) {
                        MAP[newPoint.getX()][newPoint.getY()] = true;
                        MAP[newPoint.getX() + 1][newPoint.getY()] = true;
                        addPoints(newPoint);
                        break;
                    }
                case (1):
                    if ((0 <= newPoint.getX() - 2 && newPoint.getX() + 2 < MAP_HEIGHT) && !MAP[newPoint.getX() + 2][newPoint.getY()]) {
                        MAP[newPoint.getX()][newPoint.getY()] = true;
                        MAP[newPoint.getX() - 1][newPoint.getY()] = true;
                        addPoints(newPoint);
                        break;
                    }
                case (2):
                    if (0 <= newPoint.getY() - 2 && newPoint.getY() + 2 < MAP_WIDTH && !MAP[newPoint.getX()][newPoint.getY() - 2]) {
                        MAP[newPoint.getX()][newPoint.getY()] = true;
                        MAP[newPoint.getX()][newPoint.getY() + 1] = true;
                        addPoints(newPoint);
                        break;
                    }
                case (3):
                    if ((0 <= newPoint.getY() - 2 && newPoint.getY() + 2 < MAP_WIDTH) && !MAP[newPoint.getX()][newPoint.getY() + 2]) {
                        MAP[newPoint.getX()][newPoint.getY()] = true;
                        MAP[newPoint.getX()][newPoint.getY() - 1] = true;
                        addPoints(newPoint);
                        break;
                    }
            }
        }
        if (countBot>=4){
            changeMap();
        }
    }

    //метод изменяет наш лабиринт, "дробя" его на свободные ходы
    //т.е рандомим точку, проверяем есть ли в ней препятствие, если есть удаляем препятствие смешаемся по оси У
    public static void changeMap() {
        for (int i = 0; i < MAP_HEIGHT * 2; i++) {
            int rnd1 = (int) (Math.random() * MAP_HEIGHT-1);
            int rnd2 = (int) (Math.random() * MAP_WIDTH-1);
            if (MAP[rnd1][rnd2]) {
                int count=0;
                while (MAP[rnd1][rnd2] && rnd1 != MAP_HEIGHT-1 &&count<4) {
                    MAP[rnd1][rnd2] = false;
                    rnd2++;
                    count++;
                }
            }
        }
    }

    //задаем количество ботов и координаты их спавна
    public static void setSpawnBot() {
        //проверям размер карты и исходя из него задаем количество ботов
        if (0 < MAP_WIDTH * MAP_HEIGHT && MAP_WIDTH * MAP_HEIGHT < 100) {
            countBot = 2;
        } else if (100 <= MAP_WIDTH * MAP_HEIGHT && MAP_WIDTH * MAP_HEIGHT < 200) {
            countBot = 3;
        } else if (200 <= MAP_WIDTH * MAP_HEIGHT && MAP_WIDTH * MAP_HEIGHT < 900) {
            countBot = 4;
        } else if (900 <= MAP_WIDTH * MAP_HEIGHT && MAP_WIDTH * MAP_HEIGHT < 1_600) {
            countBot = 5;
        } else if (1_600 <= MAP_WIDTH * MAP_HEIGHT && MAP_WIDTH * MAP_HEIGHT < 4_200) {
            countBot = 6;
        } else if (4_200 <= MAP_WIDTH * MAP_HEIGHT && MAP_WIDTH * MAP_HEIGHT < 8_100) {
            countBot = 9;
        } else {
            countBot = 12;
        }
        //создаем список точек в которых будут спавниться боты
        listOfSpawn = new ArrayList<>();
        for (int i = 0; i < countBot; ) {
            int rndX = (int) (Math.random() * MAP_HEIGHT);
            int rndY = (int) (Math.random() * MAP_WIDTH);
            if (!MAP[rndX][rndY]) {
                listOfSpawn.add(new Point(rndX, rndY));
                i++;
            }
        }
    }


    /*
     * Метод реализует список точек, в которые мы можем пойти от изначальной точки
     * */
    public static List<Point> addPointsList(Point point) {
        List<Point> list = new ArrayList<>();
        if (0 <= point.getX() - 2 && !MAP[point.getX() - 2][point.getY()]) {
            list.add(new Point(point.getX() - 2, point.getY()));
        }
        if (point.getX() + 2 < MAP_HEIGHT && !MAP[point.getX() + 2][point.getY()]) {
            list.add(new Point(point.getX() + 2, point.getY()));
        }
        if (point.getY() + 2 < MAP_WIDTH && !MAP[point.getX()][point.getY() + 2]) {
            list.add(new Point(point.getX(), point.getY() + 2));
        }
        if (0 <= point.getY() - 2 && !MAP[point.getX()][point.getY() - 2]) {
            list.add(new Point(point.getX(), point.getY() - 2));
        }
        return list;
    }

    /*
     * Добавляем точки, в которые можем пойти, в нашу очередь
     * */
    public static void addPoints(Point point) {
        if (0 <= point.getX() - 2 && !MAP[point.getX() - 2][point.getY()]) {
            queuePoint.add(new Point(point.getX() - 2, point.getY()));
        }
        if (point.getX() + 2 < MAP_HEIGHT && !MAP[point.getX() + 2][point.getY()]) {
            queuePoint.add(new Point(point.getX() + 2, point.getY()));
        }
        if (point.getY() + 2 < MAP_WIDTH && !MAP[point.getX()][point.getY() + 2]) {
            queuePoint.add(new Point(point.getX(), point.getY() + 2));
        }
        if (0 <= point.getY() - 2 && !MAP[point.getX()][point.getY() - 2]) {
            queuePoint.add(new Point(point.getX(), point.getY() - 2));
        }
    }

    //записываем наши данные в файлы
    public static void PrintMap() {
        try {
            WriteMap writeMap = new WriteMap(MAP, countBot, listOfSpawn);
            writeMap.writeMap();
        } catch (IOException e) {
        }
    }

}
