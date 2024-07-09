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
    private Position checkPointApple;

    private ArrayList<SnakeNode> snake;
    private ArrayList<SnakeNode> checkPointSnake;

    public Elements(int width, int height, Random random, boolean print) {
        this.width = width;
        this.height = height;
        this.random = random;
        this.field = new Gridworld(width, height, print);

        snake = new ArrayList<>();
        snake.add(spawnSnake());
        spawnApple();
        setCheckPoint();

        field.printField();
    }

    public void spawnApple() {
        Position position = null;
        //Check that Apple does not get spawned on top of any snake part
        boolean isOnSnake = true;
        while (isOnSnake) {
            isOnSnake = false;
            //spawn Apple with random position
            int xA = random.nextInt(width);
            int yA = random.nextInt(height);
            position = new Position(xA, yA);
            Position finalPosition = position;
            if (snake.stream().anyMatch(s -> s.getPosition().equals(finalPosition))) {
                isOnSnake = true;
            }
        }
        //update gameLogic.Gridworld
        field.updateField(position, Marker.APPLE);
        apple = position;
    }

    private SnakeNode spawnSnake() {
        //spawn Snake with random position
        int xS = random.nextInt(width);
        int yS = random.nextInt(height);
        Position position = new Position(xS, yS);
        int direction = random.nextInt(4);
        //update gameLogic.Gridworld
        field.updateField(position, Marker.SNAKE_HEAD);
        return new SnakeNode(position, Direction.getInt(direction));
    }

    public void move(Direction direction) {
        // Move head
        Position headPosition = snake.get(0).getPosition();
        Position newHeadPosition = headPosition.copy();
        newHeadPosition.move(direction);

        // Clear current head position
        field.updateField(headPosition, null);

        // Move head to new position
        snake.get(0).setPosition(newHeadPosition);
        snake.get(0).setDirection(direction);
        field.updateField(newHeadPosition, Marker.SNAKE_HEAD);

        // Move the rest of the body
        Position prevPosition = headPosition.copy();
        for (int i = 1; i < snake.size(); i++) {
            SnakeNode currentNode = snake.get(i);
            Position currentPosition = currentNode.getPosition().copy();
            Direction prevDirection = currentNode.getDirection();

            // Update current node position to the previous node's position
            currentNode.setPosition(prevPosition);
            currentNode.setDirection(prevDirection);

            // Clear previous position of the current node
            field.updateField(currentPosition, null);

            // Mark the new position of the current node
            field.updateField(prevPosition, Marker.SNAKE_NODE);

            // Save the current position for the next iteration
            prevPosition = currentPosition;
        }

        field.printField();
    }

    public void moveAndGrow(Direction direction) {
        Position headPosition = snake.get(0).getPosition();
        Position newHeadPosition = headPosition.copy();
        newHeadPosition.move(direction);

        // Check if the new position is out of bounds
        if (newHeadPosition.getX() < 0 || newHeadPosition.getX() >= width ||
                newHeadPosition.getY() < 0 || newHeadPosition.getY() >= height) {
            System.err.println("Position out of bounds: " + newHeadPosition);
            return;
        }

        Position lastHeadPos = headPosition.copy();
        field.updateField(lastHeadPos, Marker.SNAKE_NODE);
        snake.get(0).setPosition(newHeadPosition);
        field.updateField(newHeadPosition, Marker.SNAKE_HEAD);
        snake.add(1, new SnakeNode(lastHeadPos, direction)); // Add new node just behind the head

        field.printField();
    }

    public void reset() {
        snake = checkPointSnake;
        field = checkPointField;
        apple = checkPointApple;
        //setCheckPoint();
        field.printField();
    }

    private void setCheckPoint() {
        checkPointSnake = copySnake(snake);
        checkPointField = new Gridworld(field);
        checkPointApple = apple.copy();
    }

    private ArrayList<SnakeNode> copySnake(ArrayList<SnakeNode> original) {
        ArrayList<SnakeNode> copy = new ArrayList<>();
        for (SnakeNode node : original) {
            copy.add(new SnakeNode(node.getPosition().copy(), node.getDirection()));
        }
        return copy;
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

    public int getSnakeSize () {
        return snake.size();
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
