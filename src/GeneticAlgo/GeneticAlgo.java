package GeneticAlgo;

import GameLogic.helpers.Direction;
import GameLogic.helpers.GameStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.Stream;

public class GeneticAlgo {

    private final int genomeSize = 96;
    private final Random random = new Random();
    private final double fitnessThreshold = 0.1;

    //für zufällige Initialisierung der Gewichte
    private final double min = -100;
    private final double max = 100;

    private final int populationSize = 20;
    private final double mutationRate = 0.3;
    private final double avoidPercentage = 0.3; // %/100
    //elitism
    //single point or random crossover

    //methods
    public void evolve(){
        //Init population
        ArrayList<Individual> population = new ArrayList<>();
        for (int i = 0; i<populationSize; i++) {
            population.add(initIndividual());
        }
        //Until termination criterion is met...
        while (population.get(0).getFitness() > fitnessThreshold) {
            //Calculate and set fitness for every individual
            for (Individual ind : population) {
                calcFitness(ind);
            }
            //Sort individuals by fitness
            population.sort(Comparator.comparingDouble(i -> i.getFitness()));
            //Make children
            ArrayList<Individual> children = new ArrayList<>();
            for (int i = 0; i<populationSize; i++) {
                int bound = (int) (populationSize - avoidPercentage * populationSize);
                Individual parent1 = population.get(random.nextInt(bound));
                Individual parent2 = population.get(random.nextInt(bound));
                children.add(makeAChild(parent1, parent2, genomeSize));
            }
            population = children;
        }
        System.out.println("Best individual: " + population.get(0));
        System.out.println("Fitness: " + population.get(0).getFitness());
    }

    //Combine two parents for a child and mutate it
    private Individual makeAChild(Individual parent1, Individual parent2, int size) {
        //random crossover point
        int crossoverPoint = random.nextInt(size);
        //combine two parents
        double[] genomeParent1 = parent1.getGenome();
        double[] genomeParent2 = parent2.getGenome();
        double[] childGenome = new double[size];
        System.arraycopy(genomeParent1, 0, childGenome, 0, crossoverPoint);
        System.arraycopy(genomeParent2, crossoverPoint, childGenome, crossoverPoint, genomeParent2.length-crossoverPoint);
        //mutate child
        if (random.nextInt((int) (1/mutationRate)) == 0) childGenome[random.nextInt(size)] = random.nextDouble();
        return new Individual(childGenome);
    }

    private Individual initIndividual () {
        double[] genome = new double[populationSize];

        for(int i = 0; i<populationSize; i++) {
            genome[i] = random.nextDouble(); //TODO Bounds min,max
        }
        return new Individual(genome);
    }

    private double calcFitness (Individual individual){
        double fitness = 0.0;
        boolean gameOver = false;
        while (!gameOver) {
            Direction direction = chooseDirection(individual);
            GameStatus gameStatus = individual.moveSnake(direction);
            //TODO calculate fitness for given weights using neural network
        }
        individual.setFitness(fitness);
        return fitness;
    }

    //helpers

    //helper Method for calcFitness: Chooses direction based on environment of snake
    private Direction chooseDirection (Individual individual){ //TODO snake environment
        Direction direction = Direction.UP;
        int[][] environment = individual.getEnvironment();
        //TODO calculate output (direction) of Neural Network based on snake environment (input) and individual.getGenome (weights)
        individual.addDirection(direction);
        return direction;
    }
}
