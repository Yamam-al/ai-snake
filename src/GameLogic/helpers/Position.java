package GameLogic.helpers;

import java.util.List;
import java.util.Objects;

public class Position {

    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void move (Direction direction) {
        switch (direction) { //(0,0) is in the top left corner
            case UP -> y--;
            case DOWN -> y++;
            case LEFT -> x--;
            case RIGHT -> x++;
        }
    }

    public Position copy () {
        return new Position(this.x, this.y);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    public boolean equals (List<Position> positions) {
        for (int i = 0; i < positions.size(); i++) {
            if (this.equals(positions.get(i))) return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("Position(%d, %d)", x, y);
    }
}
