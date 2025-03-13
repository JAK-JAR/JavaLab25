import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
public class SvgScene 
{
        private Polygon[] polygons = new Polygon[3];
        private int index = 0;
        
        public void addPolygon(Polygon polygon)
        {   
            if(index >= polygons.length)
            {
                polygons[(index++)%3] = polygon;
            }
        }

        @Override
        public String toString() 
        {
            return "SvgScene{" + "index" + index + ", polygons=" + Arrays.toString(polygons) + '}';
        }

        public String toSvg()
        {
        String result = "<svg xmlns=\"http://www.w3.org/2000/svg\">";
        for(var polygon : polygons)
        {
            result += "\n\t" + polygon.toSvg();
        }
        result += "\n</svg>";
        return result;
        }

        public void save(String filePath) throws IOException
        {
        double xMin = Double.MAX_VALUE;
        double yMin = Double.MAX_VALUE;
        double xMax = Double.MIN_VALUE;
        double yMax = Double.MIN_VALUE;

        // Oblicza rozmiar obrazu na podstawie boundingBox() wszystkich wielokątów
        for (Polygon polygon : polygons) {
            if (polygon != null) {
                BoundingBox bbox = polygon.boundingBox();
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
        for (Polygon polygon : polygons) {
            if (polygon != null) {
                svgContent.append(polygon.toSvg()).append("\n");
            }
        }
        svgContent.append("</svg>");

        // Zapisuje zawartość SVG do pliku
        try (FileWriter writer = new FileWriter(filePath)) 
        {
            writer.write(svgContent.toString());
        }
        }

}
