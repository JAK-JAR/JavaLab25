package Game;

public enum Gesture {
    ROCK,
    PAPER,
    SCISSORS;

    public static Gesture fromString(String s) {
        switch(s) {
            case "r" -> {
                return ROCK;
            }
            case "p" -> {
                return PAPER;
            }
            case "s" -> {
                return SCISSORS;
            }
            default -> {
                throw new IllegalArgumentException("Invalid gesture: " + s);
            }
        }
    }

    public int compareWith(Gesture gesture) {
        if(this == gesture) {
            return 0;
        }
        if((this == ROCK && gesture == SCISSORS) || (this == PAPER && gesture == ROCK) || (this == SCISSORS && gesture == PAPER)) {
            return 1;
        } else {
            return -1;
        }
    }
}
