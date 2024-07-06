package GameLogic;

import GameLogic.helpers.Direction;
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

    public int step (Direction direction) {
        System.out.println("gameLogic.helpers.Direction " + direction);
        int reward = valueOfNextPosition(elements.getHeadPosition(), elements.getApplePosition(), direction);
        System.out.println("Reward " + reward);
        if (reward < 0) System.out.println("Game Over"); //TODO reset to last checkpoint with snake-position list
        else if (reward > 0) {
            elements.spawnApple();
            elements.moveAndGrow(direction);
        }
        else elements.move(direction);
        return reward;
    }

    private int valueOfNextPosition (Position posSnake, Position posApple, Direction direction) {
        posSnake.move(direction);
        if (!(posSnake.getX()<width && posSnake.getY()<height)) return penalty;
        else if (posSnake.equals(elements.getSnakePositions())) return penalty; //checks if snake runs into itself
        else if (posSnake.equals(posApple)) return appleReward;
        else return 0;
    }

    public void resetGameLevel () {
        elements.reset();
    }
}
