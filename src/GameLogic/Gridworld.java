package GameLogic;

import GameLogic.helpers.Marker;
import GameLogic.helpers.Position;

public class Gridworld {

    private final int width;
    private final int height;
    private boolean print;

    private Marker[][] field;

    public Gridworld(int width, int height, boolean print) {
        this.width = width;
        this.height = height;
        field = new Marker[width][height];
        this.print = print;
    }

    public Gridworld(Gridworld gridworld) {
        this.width = gridworld.width;
        this.height = gridworld.height;
        field = new Marker[width][height];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (gridworld.field[w][h] != null) {
                    field[w][h] = gridworld.field[w][h].copy();
                }
            }
        }
    }

    public void updateField(Position position, Marker marker) {
        if (position.getX() >= 0 && position.getX() < width && position.getY() >= 0 && position.getY() < height) {
            field[position.getX()][position.getY()] = marker;
        } else {
            throw new IllegalArgumentException("Position out of bounds: " + position);
        }
    }


    public void printField() {
        if (print) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\u001B[32m");
            stringBuilder.append(symbolMulti('_', width * 5 + 1));
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {
                    if (field[i][j] == null) {
                        stringBuilder.append("|    ");
                        continue;
                    }
                    stringBuilder.append(String.format("| %s ", field[i][j]));
                }
                stringBuilder.append("|\n");
                stringBuilder.append(symbolMulti('_', width * 5 + 1));
            }
            stringBuilder.append("\u001B[0m").append("\n");
            stringBuilder.append(symbolMulti('_', width * 5 + 1));
            stringBuilder.append("\n");
            System.out.println(stringBuilder);
        }
    }

    public String getField() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\u001B[32m" + symbolMulti('_', width * 5 + 1));
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (field[i][j] == null) {
                    stringBuilder.append("| ").append("⬛").append(" ");
                    continue;
                }
                stringBuilder.append(String.format("| %s ", field[i][j]));
            }
            stringBuilder.append("|\n");
            stringBuilder.append(symbolMulti('_', width * 5 + 1));
        }
        stringBuilder.append("\u001B[0m" + "\n" + symbolMulti('_', width * 5 + 1) + "\n");
        return stringBuilder.toString();
    }

    private String symbolMulti(char symbol, int amount) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < amount; i++) {
            s.append(symbol);
        }
        s.append("\n");
        return s.toString();
    }

    public void setPrint(boolean b) {
        print = b;
    }
}
