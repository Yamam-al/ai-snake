package GeneticAlgo;
import GameLogic.SnakeGame;
import GameLogic.helpers.Direction;
import GameLogic.helpers.GameStatus;

import java.util.ArrayList;
import java.util.Arrays;

public class Individual {

    private double[] genome;

    private double fitness;

    private ArrayList<Direction> directions;

    private SnakeGame snakeGame;

    public Individual (double[] genome) {
        this.genome = genome;
        fitness = Double.MAX_VALUE;
        directions = new ArrayList<>();
    }

    public double[] getGenome() {
        return genome;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public void addDirection(Direction direction) {
        directions.add(direction);
    }

    public ArrayList<Direction> getDirections () {
        return directions;
    }

    public GameStatus moveSnake (Direction direction) {
        return snakeGame.step(direction);
    }

    public int[][] getEnvironment () {
        return snakeGame.getEnvironment();
    }

    @Override
    public String toString() {
        return Arrays.toString(genome);
    }
}
