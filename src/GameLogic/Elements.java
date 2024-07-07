package GameLogic;

import GameLogic.helpers.Direction;
import GameLogic.helpers.Marker;
import GameLogic.helpers.Position;

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

    public Elements(int width, int height, Random random, boolean print) {
        this.width = width;
        this.height = height;
        this.random = random;
        this.field = new Gridworld(width, height, print);

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
        Position position = new Position(xS, yS);
        //Check that snake does not get spawned on top of apple
        while(position.equals(apple)) {
            xS = random.nextInt(width);
            yS = random.nextInt(height);
        }
        int direction = random.nextInt(4);
        //update gameLogic.Gridworld
        field.updateField(position, Marker.SNAKE_HEAD);
        return new SnakeNode(position, Direction.getInt(direction));
    }

    public void move(Direction direction) {
        snake.get(0).setDirection(direction);
        System.out.println("Erasing head at: " + snake.get(0).getPosition());
        field.updateField(snake.get(0).getPosition(), null); // Erase head
        snake.get(0).getPosition().move(direction);
        System.out.println("New head position: " + snake.get(0).getPosition());
        field.updateField(snake.get(0).getPosition(), Marker.SNAKE_HEAD);

        SnakeNode currentNode = snake.get(0);
        for (int i = 1; i < snake.size(); i++) {
            Direction curDirec = currentNode.getDirection();
            currentNode = snake.get(i); // get next node
            if (i == snake.size() - 1) {
                System.out.println("Erasing last part at: " + currentNode.getPosition());
                field.updateField(currentNode.getPosition(), null); // Erase last snake part
            }
            currentNode.getPosition().move(currentNode.getDirection());
            System.out.println("Node " + i + " new position: " + currentNode.getPosition());
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
        snake.add(1, (new SnakeNode(lastHeadPos, direction))); //add new node just behind the head
        setCheckPoint();
        field.printField();
    }

    public void reset() {
        System.out.println("\n" + "Checkpoint:");
        snake = checkPointSnake;
        field = checkPointField;
        setCheckPoint();
        field.printField();
    }

    private void setCheckPoint() {
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

    public int[][] getEnvironment() {
        int[][] environment = new int[8][3]; //Apple, Wall, Snake
        Position snakePosition = snake.get(0).getPosition().copy();
        //Apple
        int xA = snakePosition.getX() - apple.getX();
        int yA = snakePosition.getY() - apple.getY();
        if (xA < 0 && yA == 0) { //Apple straight on the right
            environment[2][0] = xA * (-1);
            environment[6][0] = xA;
        } else if (xA > 0 && yA == 0) { //Apple straight on the left
            environment[6][0] = xA;
            environment[2][0] = xA * (-1);
        } else if (yA < 0 && xA == 0) { //Apple is straight below
            environment[4][0] = yA * (-1);
            environment[0][0] = yA;
        } else if (yA > 0 && xA == 0) { //Apple is straight above
            environment[0][0] = yA;
            environment[4][0] = yA * (-1);
        } else if (xA < 0 && yA < 0) { //Apple is on diagonal right below
            environment[3][0] = xA + yA * (-1);
            environment[7][0] = xA + yA;
        } else if (xA < 0 && yA > 0) { //diagonal right above
            environment[1][0] = xA * (-1) + yA;
            environment[5][0] = xA + yA * (-1);
        } else if (xA > 0 && yA < 0) { //diagonal left below
            environment[5][0] = xA + yA * (-1);
            environment[1][0] = xA * (-1) + yA;
        } else { //diagonal left above
            environment[7][0] = xA + yA;
            environment[3][0] = xA + yA * (-1);
        }
        //Walls
        int w = width - snakePosition.getX();
        environment[2][1] = w;
        environment[6][1] = snakePosition.getX() + 1;
        int h = height - snakePosition.getY();
        environment[4][1] = h;
        environment[0][1] = snakePosition.getY() + 1;
        //TODO should the wall distance also be set on diagonals?
        //TODO Snake
        int minDistance = Integer.MAX_VALUE;

        for (int i = 1; i < snake.size(); i++) {
            Position snakePos = snake.get(i).getPosition();
            int xS = snakePosition.getX() - snakePos.getX();
            int yS = snakePosition.getY() - snakePos.getY();
            int currentDistance = Math.abs(xS) + Math.abs(yS);

            if (currentDistance < minDistance) {
                minDistance = currentDistance;

                if (xS < 0 && yS == 0) { //Snake straight on the right
                    environment[2][2] = xS * (-1);
                    environment[6][2] = xS;
                } else if (xS > 0 && yS == 0) { //Snake straight on the left
                    environment[6][2] = xS;
                    environment[2][2] = xS * (-1);
                } else if (yS < 0 && xS == 0) { //Snake is straight below
                    environment[4][2] = yS * (-1);
                    environment[0][2] = yS;
                } else if (yS > 0 && xS == 0) { //Snake is straight above
                    environment[0][2] = yS;
                    environment[4][2] = yS * (-1);
                } else if (xS < 0 && yS < 0) { //Snake is on diagonal right below
                    environment[3][2] = xS + yS * (-1);
                    environment[7][2] = xS + yS;
                } else if (xS < 0 && yS > 0) { //diagonal right above
                    environment[1][2] = xS * (-1) + yS;
                    environment[5][2] = xS + yS * (-1);
                } else if (xS > 0 && yS < 0) { //diagonal left below
                    environment[5][2] = xS + yS * (-1);
                    environment[1][2] = xS * (-1) + yS;
                } else { //diagonal left above
                    environment[7][2] = xS + yS;
                    environment[3][2] = xS + yS * (-1);
                }
            }
        }

        return environment;
    }

    public void setPrint(boolean b) {
        field.setPrint(b);
    }
}
