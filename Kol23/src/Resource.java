public class Resource {
    public enum Type {
        Coal,
        Wood,
        Fish
    };
    public final Type type;
    public final Point point;

    public Resource(Point point, Type type) {
            this.point = point;
            this.type = type;
    }

    public Type getType() {
        return type;
    }
}
