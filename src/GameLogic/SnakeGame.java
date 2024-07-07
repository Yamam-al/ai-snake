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

    public SnakeGame (int width, int height, Random random, boolean print) {
        elements = new Elements(width, height, random, print);
        this.width = width;
        this.height = height;
        if (print) System.out.println("Game started");
    }

    public GameStatus step (Direction direction) {
        int reward = valueOfNextPosition(elements.getHeadPosition(), elements.getApplePosition(), direction);
        if (reward < 0) {
            System.err.println("Game over detected @ step");
            return GameStatus.GAME_OVER;
        }
        else if (reward > 0) {
            elements.spawnApple();
            elements.moveAndGrow(direction);
            return GameStatus.APPLE;
        }
        else {
            elements.move(direction);
            return GameStatus.NOTHING;
        }
    }

    private int valueOfNextPosition(Position posSnake, Position posApple, Direction direction) {
        Position nextPosition = posSnake.copy();
        nextPosition.move(direction);
        System.out.println("Checking position: " + nextPosition);

        if (nextPosition.getX() >= width || nextPosition.getY() >= height || nextPosition.getX() < 0 || nextPosition.getY() < 0) {
            System.out.println("Hit the wall at: " + nextPosition);
            System.err.println("Game over triggered @ valueOfNextPosition: Wall");
            return penalty;
        } else if (elements.getSnakePositions().contains(nextPosition)) {// checks if snake runs into itself
            System.out.println("Ran into itself at: " + nextPosition);
            System.err.println("Game over triggered @ valueOfNextPosition: Snake");
            return penalty;
        } else if (nextPosition.equals(posApple)) {
            System.out.println("Found apple at: " + nextPosition);
            return appleReward;
        } else {
            return 0;
        }
    }


    public int[][] getEnvironment () {
        return elements.getEnvironment();
    }

    public void resetGameLevel () {
        elements.reset();
    }
    public Position getHeadPosition() {
        return elements.getHeadPosition();
    }

    public Position getApplePosition() {
        return elements.getApplePosition();
    }
}
