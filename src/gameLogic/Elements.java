package gameLogic;

import gameLogic.helpers.Direction;
import gameLogic.helpers.Marker;
import gameLogic.helpers.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Elements {
    private final int width;
    private final int height;
    private final Random random;
    private Gridworld field;
    private Gridworld checkPointField;

    private Position apple;

    private ArrayList<SnakeNode> snake;
    private ArrayList<SnakeNode> checkPointSnake;

    public Elements(int width, int height, Random random) {
        this.width = width;
        this.height = height;
        this.random = random;
        this.field = new Gridworld(width, height);

        snake = new ArrayList<>();
        snake.add(spawnSnake());
        setCheckPoint();
        spawnApple();

        field.printField();
    }

    public void spawnApple() {
        //spawn Apple with random position
        int xA = random.nextInt(width);
        int yA = random.nextInt(height);
        Position position = new Position(xA, yA);
        field.updateField(position, Marker.APPLE);
        apple = position;
    }

    private SnakeNode spawnSnake() {
        //spawn Snake with random position
        int xS = random.nextInt(width);
        int yS = random.nextInt(height);
        int direction = random.nextInt(4);
        Position position = new Position(xS, yS);
        //update gameLogic.Gridworld
        field.updateField(position, Marker.SNAKE_HEAD);
        return new SnakeNode(position, Direction.getInt(direction));
    }

    public void move(Direction direction) {
        Direction headDirec = snake.get(0).getDirection();
        snake.get(0).setDirection(direction);
        field.updateField(snake.get(0).getPosition(), null); //Erase head
        snake.get(0).getPosition().move(direction);
        field.updateField(snake.get(0).getPosition(), Marker.SNAKE_HEAD);

        SnakeNode currentNode = snake.get(0);
        for (int i = 1; i < snake.size(); i++) {
            Direction curDirec = currentNode.getDirection();
            currentNode = snake.get(i); //get next node
            if (i == snake.size() - 1) field.updateField(currentNode.getPosition(), null); //Erase last snake part
            currentNode.getPosition().move(currentNode.getDirection());
            field.updateField(currentNode.getPosition(), Marker.SNAKE_NODE);
            currentNode.setDirection(curDirec);
        }
        field.printField();
    }

    public void moveAndGrow(Direction direction) {
        Position lastHeadPos = snake.get(0).getPosition().copy();
        field.updateField(lastHeadPos, Marker.SNAKE_NODE);
        snake.get(0).getPosition().move(direction);
        field.updateField(snake.get(0).getPosition(), Marker.SNAKE_HEAD);
        snake.add(1,(new SnakeNode(lastHeadPos, direction))); //add new node just behind the head
        setCheckPoint();
        field.printField();
    }

    public void reset () {
        System.out.println("\n"+"Checkpoint:");
        snake = checkPointSnake;
        field = checkPointField;
        setCheckPoint();
        field.printField();
    }

    private void setCheckPoint () {
        checkPointSnake = new ArrayList<>(snake);
        checkPointField = new Gridworld(field);
    }

    public Position getApplePosition() {
        return apple.copy();
    }

    public Position getHeadPosition() {
        return snake.get(0).getPosition().copy();
    }

    public List<Position> getSnakePositions() {
        return snake.stream().map(s -> s.getPosition().copy()).collect(Collectors.toList());
    }
}
