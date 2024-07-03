public enum Direction {
    UP,
    LEFT,
    RIGHT,
    DOWN;

    public static Direction getInt(int i){
        switch (i){
            case 0: return UP;
            case 1: return DOWN;
            case 2: return LEFT;
            case 3: return RIGHT;
        }
        return null;
    }
}
