import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        SnakeGame game = new SnakeGame(5, 6, new Random(1234));

        //Until length of 4
        //List<Direction> directions = new ArrayList<>(List.of(Direction.UP,Direction.UP,Direction.UP,Direction.LEFT,Direction.LEFT,Direction.LEFT,Direction.UP,Direction.UP,Direction.RIGHT,Direction.RIGHT,Direction.RIGHT,Direction.DOWN,Direction.DOWN,Direction.DOWN,Direction.RIGHT));

        //Snakes runs into itself
        List<Direction> directions = new ArrayList<>(List.of(Direction.UP,Direction.UP,Direction.UP,Direction.LEFT,Direction.LEFT,Direction.LEFT,Direction.RIGHT));

        //Snake runs into wall
        //List<Direction> directions = new ArrayList<>(List.of(Direction.RIGHT,Direction.RIGHT,Direction.RIGHT));

        boolean gameRunning = true;
        while (directions.size() != 0 && gameRunning) {
            if (game.step(directions.remove(0)) < 0) {
                gameRunning = false;
                game.resetGameLevel();
            }
        }
    }
}
