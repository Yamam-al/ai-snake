import java.util.Random;

public class SnakeGame {

    private ElementStatus elementStatus;
    private final int width;
    private final int height;

    private final int appleReward = 1;
    private final int penalty = -1;


    public SnakeGame (int width, int height, Random random) {
        elementStatus = new ElementStatus(width, height, random);
        this.width = width;
        this.height = height;
    }

    public int step (Direction direction) {
        System.out.println("Direction " + direction);
        int reward = valueOfNextPosition(elementStatus.getHeadPosition(), elementStatus.getApplePosition(), direction);
        System.out.println("Reward " + reward);
        if (reward < 0) System.out.println("Game Over"); //TODO reset to last checkpoint with snake-position list
        else if (reward > 0) {
            elementStatus.spawnApple();
            elementStatus.moveAndGrow(direction);
        }
        else elementStatus.move(direction);
        return reward;
    }

    private int valueOfNextPosition (Position posSnake, Position posApple, Direction direction) {
        posSnake.move(direction);
        if (!(posSnake.getX()<width && posSnake.getY()<height)) return penalty;
        else if (posSnake.equals(elementStatus.getSnakePositions())) return penalty; //checks if snake runs into itself
        else if (posSnake.equals(posApple)) return appleReward;
        else return 0;
    }

    public void resetGameLevel () {
        elementStatus.reset();
    }
}
