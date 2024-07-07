package GeneticAlgo;

import GameLogic.helpers.Direction;

public class Network {
    private Node[] nodes;

    public class Node {
        private double output;
        private double[][] weights;
        private double bias;
        private int[][] environment;

        public Node(int[][] environment, double[][] weights, double bias) {
            this.environment = environment;
            this.weights = weights;
            this.bias = bias;
            setOutput();
        }

        public double getOutput() {
            return output;
        }

        public int[][] getEnvironment() {
            return environment;
        }

        public double[][] getWeights() {
            return weights;
        }

        public double getBias() {
            return bias;
        }

        public void setWeights(double[][] weights) {
            this.weights = weights;
            setOutput();
        }

        public void setBias(double bias) {
            this.bias = bias;
            setOutput();
        }

        private void setOutput() {
            //TODO calculate value of Node based on environment and weights
            for (int i = 0; i < environment.length; i++) {
                for (int j = 0; j < environment[i].length; j++) {
                    output += environment[i][j] * weights[i][j] + bias;
                }
            }
        }

        public void setEnvironment(int[][] environment) {
            this.environment = environment;
        }
    }

    public Network(int[][] environment, double[][][] weights, double bias) {
        this.nodes = new Node[4];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new Node(environment, weights[i], bias);
        }

    }

    public Node[] getNodes() {
        return nodes;
    }

    public Direction getDirection() {
        int winnerIndex = 0;
        double winnerValue = nodes[0].getOutput();
        for (int i = 1; i < nodes.length; i++) {
            if (nodes[i].getOutput() > winnerValue) {
                winnerValue = nodes[i].getOutput();
                winnerIndex = i;
            }
        }
        return Direction.getInt(winnerIndex);
    }
}
