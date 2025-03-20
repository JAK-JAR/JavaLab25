import java.util.Locale;

public class TransformationDecorator extends ShapeDecorator {
    private String transform;

    public TransformationDecorator(Shape decoratedShape) {
        super(decoratedShape);
    }

    @Override
    public String toSvg(String inputSvg) {
        return super.toSvg(String.format(Locale.ENGLISH, "transform=\"%s\" %s", transform, inputSvg));
    }

    public static class Builder {
        private String transform;
        public Builder translate(Vec2 translation) {
            this.transform += String.format(Locale.ENGLISH, " translate(%f,%f) ", translation.x(), translation.y());
            return this;
        }

        public Builder rotate(float angle, Vec2 center){
            this.transform += String.format(Locale.ENGLISH, " rotate(%f %f %f) ", angle, center.x(), center.y());
            this.transform = this.transform.trim();
            return this;
        }

        public Builder scale(Vec2 scaleFactor){
            this.transform += String.format(Locale.ENGLISH, " scale(%f %f) ", scaleFactor.x(), scaleFactor.y());
            this.transform = this.transform.trim();
            return this;
        }

        public TransformationDecorator build(Shape shape) {
            TransformationDecorator decorator = new TransformationDecorator(shape);
            decorator.transform = this.transform;
            return decorator;
        }
    }
}
