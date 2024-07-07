package GeneticAlgo;

import GameLogic.SnakeGame;
import GameLogic.helpers.Direction;
import GameLogic.helpers.GameStatus;
import GameLogic.helpers.Position;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class GeneticAlgo {

    //genome Array sizes (3-dimensional)

    private final int nodeCount = 4;
    private final int directionsCount = 8;
    private final int elementCount = 3;
    private final Random random = new Random();
    private final double fitnessThreshold = 9;

    //parameters
    private final int populationSize = 20;
    private final double mutationRate = 0.3;
    private final double avoidPercentage = 0.3; // %/100
    //elitism
    //single point or random crossover

    private final int widthField = 5;
    private final int heightField = 5;

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
        while (!isTerminationCriterionMet(population)) {

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
        //Print best individual
        System.out.println("Best individual: " + population.get(0));
        System.out.println("Fitness: " + population.get(0).getFitness());
        SnakeGame game = new SnakeGame(widthField, heightField, random, true);
        for (Direction direction : population.get(0).getDirections()) {
            game.step(direction);
        }
        System.out.println("Directions: " + population.get(0).getDirections());
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

    //Combine two parents for a child and mutate it
    private Individual makeCombinationChild(Individual parent1, Individual parent2) {
        //random crossover point
        int crossoverPoint = random.nextInt(elementCount * directionsCount * nodeCount);
        //combine two parents
        double[][][] genomeParent1 = parent1.getGenome();
        double[][][] genomeParent2 = parent2.getGenome();
        double[][][] childGenome = new double[nodeCount][directionsCount][elementCount];
        //child is average of both parents
        int counter = 0;
        for (int i = 0; i < nodeCount; i++) {
            for (int j = 0; j < directionsCount; j++) {
                for (int k = 0; k < elementCount; k++) {
                    if (counter < crossoverPoint) childGenome[i][j][k] = genomeParent1[i][j][k];
                    else childGenome[i][j][k] = genomeParent2[i][j][k];
                    counter++;
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

    private Individual initIndividual() {
        double[][][] genome = new double[nodeCount][directionsCount][elementCount];

        for (int i = 0; i < nodeCount; i++) {
            for (int j = 0; j < directionsCount; j++) {
                for (int k = 0; k < elementCount; k++) {
                    genome[i][j][k] = random.nextDouble(); //random initial weights
                }
            }
        }
        return new Individual(genome, random.nextDouble(), new SnakeGame(widthField, heightField, random, false));
    }

    private void calcFitness(Individual individual) {
        individual.getSnakeGame().resetGameLevel(); // Spiel zurücksetzen
        double fitness = 0.0;
        int initialDistance = getDistance(individual.getHeadPosition(), individual.getApplePosition());

        for (int i = 0; i < 100 ; i++) {
            Direction direction = chooseDirection(individual);
            GameStatus gameStatus = individual.moveSnake(direction); //Outcome of last step
            int currentDistance = getDistance(individual.getHeadPosition(), individual.getApplePosition());
            if (gameStatus == GameStatus.GAME_OVER) {
                System.err.println("Game over detected @ calcFitness");
                fitness -= 5; // negative penalty for game over
                individual.setFitness(fitness); // set fitness
                return; // Game over
            } else if (gameStatus == GameStatus.APPLE) {
                fitness += 10; // reward for eating an apple
                initialDistance = getDistance(individual.getHeadPosition(), individual.getApplePosition()); // Reset distance after eating an apple
            } else {
                fitness += 0.1; // small reward for a valid move
            }
            // Reward for getting closer to the apple
            if (currentDistance < initialDistance) {
                fitness += (initialDistance - currentDistance) * 0.5;
            } else {
                fitness -= (currentDistance - initialDistance) * 0.5;
            }
            initialDistance = currentDistance;
        }
        individual.setFitness(fitness);
    }

    private int getDistance(Position head, Position apple) {
        return Math.abs(head.getX() - apple.getX()) + Math.abs(head.getY() - apple.getY());
    }

    //helpers

    //helper Method for calcFitness: Chooses direction based on environment of snake
    private Direction chooseDirection(Individual individual) {
        int[][] environment = individual.getEnvironment();
        Network network = new Network(environment, individual.getGenome(), individual.getBias());
        Direction direction = network.getDirection();
        // output (direction) of Neural Network based on snake environment (input) and individual.getGenome (weights)
        individual.addDirection(direction);
        return direction;
    }
}
