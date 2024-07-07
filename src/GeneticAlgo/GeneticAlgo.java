package GeneticAlgo;

import GameLogic.helpers.Direction;
import GameLogic.helpers.GameStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class GeneticAlgo {

    private final int genomeSize = 96;
    private final Random random = new Random();
    private final double fitnessThreshold = 0.1;

    //parameters
    private final int populationSize = 20;
    private final double mutationRate = 0.3;
    private final double avoidPercentage = 0.3; // %/100
    //elitism
    //single point or random crossover

    //methods
    private boolean isTerminationCriterionMet(ArrayList<Individual> population) {
        //Calculate and set fitness for every individual
        for (Individual ind : population) {
            calcFitness(ind);
        }
        //Sort individuals by fitness
        population.sort(Comparator.comparingDouble(i -> i.getFitness() * (-1)));
        System.out.println("Best individual: " + population.get(0));
        if (population.get(0).getFitness() >= fitnessThreshold) {
            return true;
        } else {
            return false;
        }
    }

    public void evolve() {
        int generation = 0;
        //Init population
        ArrayList<Individual> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
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
            for (int i = 0; i < populationSize; i++) {
                int bound = (int) (populationSize - avoidPercentage * populationSize);
                Individual parent1 = population.get(random.nextInt(bound));
                Individual parent2 = population.get(random.nextInt(bound));
                children.add(makeAverageChild(parent1, parent2));
            }
            System.out.println("Generation: " + generation++);
            population = children;

        }
        System.out.println("Best individual: " + population.get(0));
        System.out.println("Fitness: " + population.get(0).getFitness());
    }

    //Combine two parents for a child and mutate it
    private Individual makeAverageChild(Individual parent1, Individual parent2) {
        //combine two parents
        double[][][] genomeParent1 = parent1.getGenome();
        double[][][] genomeParent2 = parent2.getGenome();
        double[][][] childGenome = new double[nodeCount][directionsCount][elementCount];
        //child is average of both parents
        for (int i = 0; i < nodeCount; i++) {
            for (int j = 0; j < directionsCount; j++) {
                for (int k = 0; k < elementCount; k++) {
                    childGenome[i][j][k] = (genomeParent1[i][j][k] + genomeParent2[i][j][k]) / 2;
                }
            }
        }
        //set bias
        double averageBias = parent1.getBias() + parent2.getBias() / 2;
        Individual child = new Individual(childGenome, averageBias, new SnakeGame(widthField, heightField, random, false));
        //mutate child
        if (random.nextInt((int) (1 / mutationRate)) == 0) {
            childGenome[random.nextInt(nodeCount)][random.nextInt(directionsCount)][random.nextInt(elementCount)] = random.nextDouble();
            child.setGenome(childGenome);
        }
        return child;
    }

    private Individual initIndividual () {
        double[] genome = new double[populationSize];

        for(int i = 0; i<populationSize; i++) {
            genome[i] = random.nextDouble(); //TODO Bounds min,max
        }
        return new Individual(genome);
    }

    private double calcFitness(Individual individual) {
        double fitness = 0.0;
        boolean gameOver = false;
        for (int i = 0; i < 100 && !gameOver; i++) {
            Direction direction = chooseDirection(individual);
            GameStatus gameStatus = individual.moveSnake(direction);
            //TODO calculate fitness for given weights using neural network
            if (gameStatus == GameStatus.GAME_OVER) {
                gameOver = true;
                fitness -= 5 ; //negative penalty for game over
            } else if (gameStatus == GameStatus.APPLE) {
                fitness += 1;
            } else {
                fitness += 0.1;
            }
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
