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
}
