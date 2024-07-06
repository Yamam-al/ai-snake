package GameLogic;

import GameLogic.helpers.Direction;
import GameLogic.helpers.GameStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        SnakeGame game = new SnakeGame(5, 6, new Random(1234));

        //Until length of 4
        //List<gameLogic.helpers.Direction> directions = new ArrayList<>(List.of(gameLogic.helpers.Direction.UP,gameLogic.helpers.Direction.UP,gameLogic.helpers.Direction.UP,gameLogic.helpers.Direction.LEFT,gameLogic.helpers.Direction.LEFT,gameLogic.helpers.Direction.LEFT,gameLogic.helpers.Direction.UP,gameLogic.helpers.Direction.UP,gameLogic.helpers.Direction.RIGHT,gameLogic.helpers.Direction.RIGHT,gameLogic.helpers.Direction.RIGHT,gameLogic.helpers.Direction.DOWN,gameLogic.helpers.Direction.DOWN,gameLogic.helpers.Direction.DOWN,gameLogic.helpers.Direction.RIGHT));

        //Snakes runs into itself
        List<Direction> directions = new ArrayList<>(List.of(Direction.UP,Direction.UP, Direction.UP,Direction.LEFT,Direction.LEFT,Direction.LEFT,Direction.RIGHT));

        //Snake runs into wall
        //List<gameLogic.helpers.Direction> directions = new ArrayList<>(List.of(gameLogic.helpers.Direction.RIGHT,gameLogic.helpers.Direction.RIGHT,gameLogic.helpers.Direction.RIGHT));

        boolean gameRunning = true;
        while (directions.size() != 0 && gameRunning) {
            if (game.step(directions.remove(0)) == GameStatus.GAME_OVER) {
                gameRunning = false;
                game.resetGameLevel();
            }
        }
    }
}
