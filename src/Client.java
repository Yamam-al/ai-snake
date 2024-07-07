import GameLogic.SnakeGame;
import GameLogic.helpers.Direction;
import GameLogic.helpers.GameStatus;
import GeneticAlgo.GeneticAlgo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Client {

    //TODO
    public static void main(String[] args) {
        GeneticAlgo geneticAlgo = new GeneticAlgo();
        geneticAlgo.evolve();
        System.out.println("Done");
    }
}
