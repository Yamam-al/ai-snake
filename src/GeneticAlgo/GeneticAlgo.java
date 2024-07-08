package GeneticAlgo;

import GameLogic.SnakeGame;
import GameLogic.helpers.Direction;
import GameLogic.helpers.GameStatus;
import GameLogic.helpers.Position;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class GeneticAlgo {

    private final int nodeCount = 4;
    private final int directionsCount = 8;
    private final int elementCount = 3;
    private final Random random = new Random();
    private final double fitnessThreshold = 9;

    //parameters
    private final int populationSize = 20;
    private final double initialMutationRate = 0.3;
    private final double avoidPercentage = 0.3;
    private final double largeMutationRate = 0.1;
    private final double smallMutationScale = 0.2;

    //elitism
    //single point or random crossover

    private final int widthField = 5;
    private final int heightField = 5;
    private int generation = 0;
    private int maxGenerations = 10000; // Annahme, kann angepasst werden
    private int maxFitness = 100; // Annahme, kann angepasst werden

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
                Individual child = makeCombinationChild(parent1, parent2);

                //mutate child
                mutateIndividual(child);
                children.add(child);
            }
            System.out.println("Generation: " + generation++);
            population = children;
            this.generation = generation; // Update generation count
        }
        printBestIndividual(population.get(0));
    }

    private Individual initIndividual() {
        double[][][] genome = new double[nodeCount][directionsCount][elementCount];

        for (int i = 0; i < nodeCount; i++) {
            for (int j = 0; j < directionsCount; j++) {
                for (int k = 0; k < elementCount; k++) {
                    genome[i][j][k] = (random.nextDouble() - 0.5) * 2; // Werte zwischen -1 und 1
                }
            }
        }
        return new Individual(genome, random.nextDouble(), new SnakeGame(widthField, heightField, random, false));
    }

    private boolean isTerminationCriterionMet(ArrayList<Individual> population) {
        //Calculate and set fitness for every individual
        for (Individual ind : population) {
            calcFitness(ind);
        }
        //Sort individuals by fitness
        population.sort(Comparator.comparingDouble(i -> i.getFitness() * (-1)));
        System.out.println("Best individual: " + population.get(0));
        return population.get(0).getFitness() >= fitnessThreshold;
    }

    private void calcFitness(Individual individual) {
        double fitness = 0.0;
        int initialDistance = getDistance(individual.getHeadPosition(), individual.getApplePosition());

        for (int i = 0; i < 100; i++) {
            if (individual.getSnakeGame().isGameOver()) {
                break; // Stop the loop if the game is over
            }

            Direction direction = chooseDirection(individual);
            GameStatus gameStatus = individual.moveSnake(direction);
            int currentDistance = getDistance(individual.getHeadPosition(), individual.getApplePosition());

            if (gameStatus == GameStatus.GAME_OVER) {
                fitness -= 5; // Negative penalty for game over
                break; // Exit the loop if the game is over
            } else if (gameStatus == GameStatus.APPLE) {
                fitness += 10; // Reward for eating an apple
                initialDistance = getDistance(individual.getHeadPosition(), individual.getApplePosition());
            } else {
                fitness += 0.1; // Small reward for a valid move
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

    private Direction chooseDirection(Individual individual) {
        int[][] environment = individual.getEnvironment();
        Network network = new Network(environment, individual.getGenome(), individual.getBias());
        Direction direction = network.getDirection();
        // output (direction) of Neural Network based on snake environment (input) and individual.getGenome (weights)
        individual.addDirection(direction);
        return direction;
    }

    private void printBestIndividual(Individual bestIndividual) {
        System.out.println("Best individual: " + bestIndividual);
        System.out.println("Fitness: " + bestIndividual.getFitness());

        // Replay the moves of the best individual and print each step
        SnakeGame game = bestIndividual.getSnakeGame();
        game.reset(); // Reset to initial state
        game.setPrint(true); // Enable printing of the game state
        for (Direction direction : bestIndividual.getDirections()) {
            game.move(direction);
            // Print the game state after each move
        }
        System.out.println("Directions: " + bestIndividual.getDirections());
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
                    if (counter < crossoverPoint) {
                        childGenome[i][j][k] = genomeParent1[i][j][k];
                    } else {
                        childGenome[i][j][k] = genomeParent2[i][j][k];
                    }
                    counter++;
                }
            }
        }
        //set bias
        double averageBias = (parent1.getBias() + parent2.getBias()) / 2;
        return new Individual(childGenome, averageBias, new SnakeGame(widthField, heightField, random, false));
    }

    private void mutateIndividual(Individual child) {
        double[][][] genome = child.getGenome();
        double fitnessFactor = 1.0 - (child.getFitness() / getMaxFitness());
        double adaptiveMutationRate = initialMutationRate * (1.0 - (double) generation / maxGenerations);
        double adaptiveLargeMutationRate = largeMutationRate * fitnessFactor;

        for (int i = 0; i < nodeCount; i++) {
            for (int j = 0; j < directionsCount; j++) {
                for (int k = 0; k < elementCount; k++) {
                    // Mutation
                    if (random.nextDouble() < adaptiveMutationRate) {
                        // big mutation (10%)
                        if (random.nextDouble() < adaptiveLargeMutationRate) {
                            genome[i][j][k] = random.nextDouble();
                        } else { // small mutation (90%)
                            genome[i][j][k] += (random.nextDouble() - 0.5) * smallMutationScale; // Kleine Mutation
                        }
                    }
                }
            }
        }
        child.setGenome(genome);
    }

    private double getMaxFitness() {
        return maxFitness;
    }

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
        double averageBias = (parent1.getBias() + parent2.getBias()) / 2;
        return new Individual(childGenome, averageBias, new SnakeGame(widthField, heightField, random, false));
    }

    private int getDistance(Position head, Position apple) {
        return Math.abs(head.getX() - apple.getX()) + Math.abs(head.getY() - apple.getY());
    }
}
