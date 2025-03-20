public interface Shape {
    BoundingBox boundingBox();
    String toSvg(String inputSvg);
    default String toSvg() {
        return toSvg("");
    }

}
