package Game;

public class Player {
    private String login;
    private Duel duel;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Player() {
    }

    public Player(String login) {
        this.login = login;
    }

    public void makeGesture(Gesture gesture) {
        duel.handleGesture(this, gesture);
    }

    public void enterDuel(Duel duel) {
        this.duel = duel;
    }

    public void leaveDuel() {
        this.duel = null;
    }

    public boolean isInDuel() {
        return duel != null;
    }


}
