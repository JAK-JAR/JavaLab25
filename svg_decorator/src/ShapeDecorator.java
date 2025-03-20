public abstract class ShapeDecorator implements Shape{
    private Shape decoratedShape;

    public ShapeDecorator(Shape decoratedShape) {
        this.decoratedShape = decoratedShape;
    }

    @Override
    public BoundingBox boundingBox(){
        return decoratedShape.boundingBox();
    }

    @Override
    public String toSvg(String inputSvg) {
        return decoratedShape.toSvg(inputSvg);
    }
}
