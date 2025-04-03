public class AmbiguousPersonException extends RuntimeException {
    public AmbiguousPersonException(String fullName) {
        super(fullName + " already exists");
    }
}
