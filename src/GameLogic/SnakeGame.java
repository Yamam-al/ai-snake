package GameLogic;

import GameLogic.helpers.Direction;
import GameLogic.helpers.GameStatus;
import GameLogic.helpers.Position;

import java.util.Random;

public class SnakeGame {

    private Elements elements;
    private final int width;
    private final int height;
    private boolean isGameOver = false;

    private final int appleReward = 1;
    private final int penalty = -1;
    private boolean print;
    private StringBuilder gameField = new StringBuilder();

    public SnakeGame(int width, int height, Random random, boolean print) {
        this.print = print;
        elements = new Elements(width, height, random, print);
        this.width = width;
        this.height = height;
        if (print) System.out.println("Game started");
    }

    public GameStatus move(Direction direction) {
        if (isGameOver) {
            System.out.println("SnakeGame: Game is already over. No further moves allowed.");
            return GameStatus.GAME_OVER;
        }

        int reward = valueOfNextPosition(elements.getHeadPosition(), elements.getApplePosition(), direction);
        if (reward < 0) {
            isGameOver = true;
            return GameStatus.GAME_OVER;
        } else if (reward > 0) {
            elements.spawnApple();
            gameField.append(elements.moveAndGrow(direction));
            return GameStatus.APPLE;
        } else {
            gameField.append(elements.move(direction));
            return GameStatus.NOTHING;
        }
    }

    private int valueOfNextPosition(Position posSnake, Position posApple, Direction direction) {
        Position nextPosition = posSnake.copy();
        nextPosition.move(direction);

        if (nextPosition.getX() >= width || nextPosition.getY() >= height || nextPosition.getX() < 0 || nextPosition.getY() < 0) {
            return penalty;
        } else if (elements.getSnakePositions().contains(nextPosition)) {
            return penalty;
        } else if (nextPosition.equals(posApple)) {
            return appleReward;
        } else {
            return 0;
        }
    }

    public void resetGameLevel() {
        elements.reset();
        isGameOver = false;
    }

    public Position getHeadPosition() {
        return elements.getHeadPosition();
    }

    public Position getApplePosition() {
        return elements.getApplePosition();
    }

    public int[][] getEnvironment() {
        return elements.getEnvironment();
    }
    public boolean isGameOver() {
        return isGameOver;
    }

    public void reset() {
        isGameOver = false;
        elements.reset();
    }

    public void setPrint(boolean b) {
        print = b;
        elements.setPrint(print);

    }

    public int getSnackSize() {
        return elements.getSnakeSize();
    }

    public StringBuilder getGameField() {
        return gameField;
    }
}
