package Game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DuelTest {

    @Test
    void testDuel() {
        Player player1 = new Player();
        Player player2 = new Player();
        Duel duel = new Duel(player1, player2);

        assertTrue(player1.isInDuel(), "Player 1 should be in a duel");
        assertTrue(player2.isInDuel(), "Player 2 should be in a duel");

        duel.handleGesture(player1, Gesture.ROCK);
        duel.handleGesture(player2, Gesture.SCISSORS);

        assertEquals(player1, duel.evaluate().winner(), "Player1 should be the winner");

        Duel duel2 = new Duel(player1, player2);
        duel2.handleGesture(player1, Gesture.ROCK);
        duel2.handleGesture(player2, Gesture.ROCK);

        assertNull(duel2.evaluate().winner());
    }
}
