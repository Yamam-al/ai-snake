package GeneticAlgo;

import GameLogic.SnakeGame;
import GameLogic.helpers.Direction;
import GameLogic.helpers.GameStatus;
import GameLogic.helpers.Position;

import java.util.ArrayList;

public class Individual {
    private double[][][] genome;
    private double fitness;
    private double bias;
    private ArrayList<Direction> directions;
    private SnakeGame snakeGame;
    private ArrayList<int[][]> gameStates;

    public Individual(double[][][] genome, double bias, SnakeGame snakeGame) {
        this.genome = genome;
        this.fitness = Double.MIN_VALUE;
        this.directions = new ArrayList<>();
        this.snakeGame = snakeGame;
        this.bias = bias;
        this.gameStates = new ArrayList<>();
    }

    public double[][][] getGenome() {
        return genome;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getBias() {
        return bias;
    }

    public void setBias(double bias) {
        this.bias = bias;
    }

    public void setGenome(double[][][] genome) {
        this.genome = genome;
    }

    public void addDirection(Direction direction) {
        directions.add(direction);
    }

    public ArrayList<Direction> getDirections() {
        return directions;
    }

    public GameStatus moveSnake(Direction direction) {
        GameStatus status = snakeGame.move(direction);
        if (status == GameStatus.GAME_OVER) {
        }
        saveGameState();
        return status;
    }

    public int[][] getEnvironment() {
        return snakeGame.getEnvironment();
    }

    @Override
    public String toString() {
        return String.valueOf(fitness);
    }

    public Position getHeadPosition() {
        return snakeGame.getHeadPosition();
    }

    public Position getApplePosition() {
        return snakeGame.getApplePosition();
    }

    private void saveGameState() {
        int[][] currentState = snakeGame.getEnvironment();
        int[][] stateCopy = new int[currentState.length][];
        for (int i = 0; i < currentState.length; i++) {
            stateCopy[i] = currentState[i].clone();
        }
        gameStates.add(stateCopy);
    }

    public ArrayList<int[][]> getGameStates() {
        return gameStates;
    }

    public SnakeGame getSnakeGame() {
        return snakeGame;
    }

    public int getSnakeSize() {
        return snakeGame.getSnackSize();
    }
}
