package Game;

public class Duel {
    private final Player player1;
    private final Player player2;
    private Gesture gesture1;
    private Gesture gesture2;
    private Runnable onEnd;

    public void setOnEnd(Runnable onEnd) {
        this.onEnd = onEnd;
    }


    public Duel(Player player1, Player player2) {
        this.player1 = player1;
        player1.enterDuel(this);
        this.player2 = player2;
        player2.enterDuel(this);
    }

    public void handleGesture(Player player, Gesture gesture) {
        if(player == player1) {
            gesture1 = gesture;
        }
        else if(player == player2) {
            gesture2 = gesture;
        }
        if (gesture1 != null && gesture2 != null && onEnd != null) {
            onEnd.run();
        }

    }

    public record Result(Player winner, Player loser) { }

    public Result evaluate() {
        if (gesture1 == null || gesture2 == null) {
            throw new IllegalStateException("Cannot evaluate duel without gestures");
        } else {
            int result = gesture1.compareWith(gesture2);
            if (result == 0) {
                player1.leaveDuel();
                player2.leaveDuel();
                return null;
            }
            else if (result > 0) {
                player2.leaveDuel();
                player1.leaveDuel();
                return new Result(player1, player2);
            }
            else {
                player1.leaveDuel();
                player2.leaveDuel();
                return new Result(player2, player1);
            }
        }
    }
}
