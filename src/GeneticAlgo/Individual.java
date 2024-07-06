package GeneticAlgo;
import GameLogic.helpers.Direction;

import java.util.ArrayList;
import java.util.Arrays;

public class Individual {

    private double[] genome;

    private double fitness;

    private ArrayList<Direction> directions;

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

    @Override
    public String toString() {
        return Arrays.toString(genome);
    }
}
