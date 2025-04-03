public class NegativeLifespanException extends Exception {
    public NegativeLifespanException(Person  p) {
        super(p + " has negative lifespan");
    }
}
