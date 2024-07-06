package GameLogic;

import GameLogic.helpers.Marker;
import GameLogic.helpers.Position;

public class Gridworld {

    private final int width;
    private final int height;

    private Marker[][] field;

    public Gridworld (int width, int height) {
        this.width = width;
        this.height = height;
        field = new Marker[width][height];
    }

    public Gridworld (Gridworld gridworld) {
        this.width = gridworld.width;
        this.height = gridworld.height;
        field = new Marker[width][height];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (gridworld.field[w][h] != null){
                    field[w][h] = gridworld.field[w][h].copy();
                }
            }
        }
    }

    public void updateField (Position position, Marker marker) {
        field [position.getX()][position.getY()] = marker;
    }

    public void printField() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(symbolMulti('_',width*4+1));
        for(int j = 0; j<height; j++){
            for(int i =0; i<width;i++){
                if(field[i][j]==null){
                    stringBuilder.append("|   ");
                    continue;
                }
                stringBuilder.append(String.format("| %s ",field[i][j]));
            }
            stringBuilder.append("|\n");
            stringBuilder.append(symbolMulti('_',width*4+1));
        }
        System.out.println(stringBuilder);
    }

    private String symbolMulti(char symbol, int amount){
        StringBuilder s = new StringBuilder();
        for (int i=0; i<amount;i++){
            s.append(symbol);
        }
        s.append("\n");
        return s.toString();
    }
}
