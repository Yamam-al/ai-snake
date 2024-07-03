package gameLogic.helpers;

public enum Marker {
    SNAKE_HEAD,
    SNAKE_NODE,
    APPLE;

    public String toString(){
        switch (this.ordinal()){
            case 0: return "\u001B[32m"+"8"+"\u001B[0m";
            case 1: return "\u001B[32m"+"X"+"\u001B[0m";
            case 2: return "\u001B[31m"+"0"+"\u001B[0m";
        }
        return "";
    }

    public Marker copy() {
        switch (this) {
            case APPLE: return APPLE;
            case SNAKE_HEAD: return SNAKE_HEAD;
            case SNAKE_NODE: return SNAKE_NODE;
        }
        return null;
    }
}
