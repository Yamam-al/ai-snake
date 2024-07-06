package GameLogic;

import GameLogic.helpers.Direction;
import GameLogic.helpers.GameStatus;
import GameLogic.helpers.Position;

import java.util.Random;

public class SnakeGame {

    private Elements elements;
    private final int width;
    private final int height;

    private final int appleReward = 1;
    private final int penalty = -1;


    public SnakeGame (int width, int height, Random random) {
        elements = new Elements(width, height, random);
        this.width = width;
        this.height = height;
    }

    public GameStatus step (Direction direction) {
        int reward = valueOfNextPosition(elements.getHeadPosition(), elements.getApplePosition(), direction);
        if (reward < 0) return GameStatus.GAME_OVER;
        else if (reward > 0) {
            elements.spawnApple();
            elements.moveAndGrow(direction);
            return GameStatus.APPLE;
        }
        else elements.move(direction);
        return GameStatus.NOTHING;
    }

    private int valueOfNextPosition (Position posSnake, Position posApple, Direction direction) {
        posSnake.move(direction);
        if (!(posSnake.getX()<width && posSnake.getY()<height)) return penalty;
        else if (posSnake.equals(elements.getSnakePositions())) return penalty; //checks if snake runs into itself
        else if (posSnake.equals(posApple)) return appleReward;
        else return 0;
    }

    public int[][] getEnvironment () {
        return elements.getEnvironment();
    }

    public void resetGameLevel () {
        elements.reset();
    }
}
