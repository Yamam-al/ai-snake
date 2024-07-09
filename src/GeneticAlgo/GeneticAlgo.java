package GeneticAlgo;

import GameLogic.SnakeGame;
import GameLogic.helpers.Direction;
import GameLogic.helpers.GameStatus;
import GameLogic.helpers.Position;

import javax.xml.transform.Source;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;


public class GeneticAlgo {

    private final int nodeCount = 4;
    private final int directionsCount = 8;
    private final int elementCount = 3;
    private final Random random = new Random(1234);

    //parameters

    private final double fitnessThreshold = 5000;
    private final int populationSize = 500;

    //Mutation
    private final boolean selfAdaptive = true;
    private final double initialMutationRate = 0.3;
    private final double avoidPercentage = 0.6;
    private final double largeMutationRate = 0.2;
    private final double smallMutationStepSize = 0.2; //stepSize for small mutation
    private final int maxSteps = 500; // for each individual in a generation (can be adjusted)
    private final int eliteCount = populationSize * 5 / 100; // 5% of the population is elite

    //elitism
    //single point or random crossover

    private final int widthField = 10;
    private final int heightField = 8;
    private int generation = 0;
    private final int maxGenerations = 100000; // Annahme, kann angepasst werden
    private final boolean printCSV = false;
    private final String csvFileName = "fitness_stats_" + LocalDate.now() + "--" +LocalTime.now().getHour() + "-" +LocalTime.now().getMinute() + "-" + LocalTime.now().getSecond() +"_SelfAdapting_"+selfAdaptive+".csv";

    public void evolve() {
        // Init population
        ArrayList<Individual> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(initIndividual());
        }


        if (printCSV){
            // Write header to CSV
            try (FileWriter writer = new FileWriter(csvFileName)) {
                writer.append("Generation;minFitness;avgFitness;maxFitness\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // Until termination criterion is met...
        while (!isTerminationCriterionMet(population)) {

            if (printCSV){
                // Collect statistics
                double minFitness = calculateMinFitness(population);
                double avgFitness = calculateAvgFitness(population);
                double maxFitness = calculateMaxFitness(population);
                // Write statistics to CSV
                writeStatsToCSV(generation, minFitness, avgFitness, maxFitness);
            }

            // Make children
            ArrayList<Individual> children = new ArrayList<>();
            // Elitism: Retain top individuals
            for (int i = 0; i < eliteCount; i++) {
                children.add(population.get(i));
            }

            while (children.size() < populationSize) {
                int bound = (int) (populationSize - avoidPercentage * populationSize);
                Individual parent1 = population.get(random.nextInt(bound));
                Individual parent2 = population.get(random.nextInt(bound));
                Individual child = makeCombinationChild(parent1, parent2);

                // Mutate child
                mutateIndividual(child);
                children.add(child);
            }

            System.out.println("Generation: " + generation++);
            population = children;
        }
        if (printCSV){
            // Collect statistics
            double minFitness = calculateMinFitness(population);
            double avgFitness = calculateAvgFitness(population);
            double maxFitness = calculateMaxFitness(population);
            // Write statistics to CSV
            writeStatsToCSV(generation, minFitness, avgFitness, maxFitness);
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

        // Initialisieren Sie die Mutations-parameter mit den gegebenen Werten
        return new Individual(genome, random.nextDouble(), new SnakeGame(widthField, heightField, random, false),
                initialMutationRate, largeMutationRate, smallMutationStepSize);
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
        int applesEaten = 0;
        int stepsSurvived = 0;

        for (int i = 0; i < maxSteps; i++) {
            if (individual.getSnakeGame().isGameOver()) {
                fitness -= 500 - 100 * applesEaten; // Additional penalty for game over unless the snake has eaten enough apples
                break; // Exit the loop if the game is over
            }
            Direction direction = chooseDirection(individual);
            GameStatus gameStatus = individual.moveSnake(direction);
            int currentDistanceToApple = getDistance(individual.getHeadPosition(), individual.getApplePosition());
            stepsSurvived++;

            if (gameStatus == GameStatus.GAME_OVER) {
                fitness -= 500;// - 100 * applesEaten; // Additional penalty for game over unless the snake has eaten enough apples
                break; // Exit the loop if the game is over
            } else if (gameStatus == GameStatus.APPLE) {
                applesEaten++;
                fitness += 100*applesEaten; // High reward for eating an apple
                initialDistance = getDistance(individual.getHeadPosition(), individual.getApplePosition());
            } else { //Step
                // Reward for getting closer to the apple
                if (currentDistanceToApple < initialDistance) {
                    fitness += (widthField+heightField)/2.0-currentDistanceToApple; // Small reward for getting closer to the apple
                } else {
                    fitness -= 5 * currentDistanceToApple; // Small penalty for getting further from the apple
                }
                initialDistance = currentDistanceToApple;
            }

        }
        // Reward for surviving longer
        //fitness += stepsSurvived * 0.1;

        //fitness += 0.01 * (maxSteps - stepsSurvived); // Small reward for steps survived without game over
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
        System.out.println("Directions: " + bestIndividual.getDirections());
        System.out.println("Steps moved: " + bestIndividual.getDirections().size());
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

        // Kombinieren Sie die Mutations-parameter durch Mittelwertbildung
        double childInitialMutationRate = (parent1.getInitialMutationRate() + parent2.getInitialMutationRate()) / 2;
        double childLargeMutationRate = (parent1.getLargeMutationRate() + parent2.getLargeMutationRate()) / 2;
        double childSmallMutationStepSize = (parent1.getSmallMutationStepSize() + parent2.getSmallMutationStepSize()) / 2;

        return new Individual(childGenome, averageBias,
                new SnakeGame(widthField, heightField, random, false),
                childInitialMutationRate, childLargeMutationRate, childSmallMutationStepSize);
    }

    private void mutateIndividual(Individual child) {
        double[][][] genome = child.getGenome();
        double fitnessFactor = 1.0 - (child.getFitness() / getMaxFitness());
        double adaptiveMutationRate = child.getInitialMutationRate() * (1.0 - (double) generation / maxGenerations);
        double adaptiveLargeMutationRate = child.getLargeMutationRate() * fitnessFactor;

        for (int i = 0; i < nodeCount; i++) {
            for (int j = 0; j < directionsCount; j++) {
                for (int k = 0; k < elementCount; k++) {
                    // Mutation
                    if (random.nextDouble() < adaptiveMutationRate) {
                        // big mutation
                        if (random.nextDouble() < adaptiveLargeMutationRate) {
                            genome[i][j][k] = random.nextDouble();
                        } else { // small mutation
                            genome[i][j][k] += (random.nextDouble() - 0.5) * child.getSmallMutationStepSize(); // Kleine Mutation
                        }
                    }
                }
            }
        }
        child.setGenome(genome);

        // Mutieren Sie die Mutations-parameter nur, wenn selfAdaptive wahr ist
        if (selfAdaptive) {
            if (random.nextDouble() < 0.1) { // 10% Chance, die Mutationsrate selbst zu mutieren
                child.setInitialMutationRate(child.getInitialMutationRate() * (1 + (random.nextDouble() - 0.5) * 0.1)); // Kleine Veränderung
            }
            if (random.nextDouble() < 0.1) { // 10 % Chance, die große Mutationsrate zu mutieren
                child.setLargeMutationRate(child.getLargeMutationRate() * (1 + (random.nextDouble() - 0.5) * 0.1)); // Kleine Veränderung
            }
            if (random.nextDouble() < 0.1) { // 10 % Chance, die kleine Mutationsschrittgröße zu mutieren
                child.setSmallMutationStepSize(child.getSmallMutationStepSize() * (1 + (random.nextDouble() - 0.5) * 0.1)); // Kleine Veränderung
            }
        }
    }


    private double getMaxFitness() {
        return fitnessThreshold;
    }


    private int getDistance(Position head, Position apple) {
        return Math.abs(head.getX() - apple.getX()) + Math.abs(head.getY() - apple.getY());
    }

    private void writeStatsToCSV(int generation, double minFitness, double avgFitness, double maxFitness) {
        try (FileWriter writer = new FileWriter(csvFileName, true)) {
            writer.append(String.valueOf(generation)).append(";").append(String.valueOf(minFitness)).append(";").append(String.valueOf(avgFitness)).append(";").append(String.valueOf(maxFitness)).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double calculateMinFitness(ArrayList<Individual> population) {
        return population.stream().mapToDouble(Individual::getFitness).min().orElse(0);
    }

    private double calculateAvgFitness(ArrayList<Individual> population) {
        return population.stream().collect(Collectors.averagingDouble(Individual::getFitness));
    }

    private double calculateMaxFitness(ArrayList<Individual> population) {
        return population.stream().mapToDouble(Individual::getFitness).max().orElse(0);
    }

}
