import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class SvgScene {
    private Shape[] shapes = new Shape[3];
    private int index = 0;

    public void addShape(Shape shape) {
        if (index >= shapes.length) {
            shapes[(index++) % 3] = shape;
        } else {
            shapes[index++] = shape;
        }
    }

    @Override
    public String toString() {
        return "SvgScene{" + "index=" + index + ", shapes=" + Arrays.toString(shapes) + '}';
    }

    public String toSvg() {
        StringBuilder result = new StringBuilder("<svg xmlns=\"http://www.w3.org/2000/svg\">");
        for (Shape shape : shapes) {
            if (shape != null) {
                result.append("\n\t").append(shape.toSvg());
            }
        }
        result.append("\n</svg>");
        return result.toString();
    }

    public void save(String filePath) throws IOException {
        double xMin = Double.MAX_VALUE;
        double yMin = Double.MAX_VALUE;
        double xMax = Double.MIN_VALUE;
        double yMax = Double.MIN_VALUE;

        // Oblicza rozmiar obrazu na podstawie boundingBox() wszystkich kształtów
        for (Shape shape : shapes) {
            if (shape != null) {
                BoundingBox bbox = shape.boundingBox();
                xMin = Math.min(xMin, bbox.x());
                yMin = Math.min(yMin, bbox.y());
                xMax = Math.max(xMax, bbox.x() + bbox.width());
                yMax = Math.max(yMax, bbox.y() + bbox.height());
            }
        }

        double width = xMax - xMin;
        double height = yMax - yMin;

        // Tworzy zawartość SVG z odpowiednimi rozmiarami
        StringBuilder svgContent = new StringBuilder(String.format("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"%f\" height=\"%f\">\n", width, height));
        for (Shape shape : shapes) {
            if (shape != null) {
                svgContent.append(shape.toSvg()).append("\n");
            }
        }
        svgContent.append("</svg>");

        // Zapisuje zawartość SVG do pliku
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(svgContent.toString());
            
        }
    }
    
}