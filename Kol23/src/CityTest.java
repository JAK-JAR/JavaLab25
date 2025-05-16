import java.util.Arrays;
import java.util.List;

public class CityTest {

    public void testCityInitialization() {
        Point coalLocation = new Point(1, 1);
        Point woodLocation = new Point(100, 100);
        Point fishLocation1 = new Point(5, 5);
        Point fishLocation2 = new Point(105, 95);

        Point LublinCenter = new Point(0, 0);
        Point GdanskCenter = new Point(100, 90);
        City Lublin = new City(LublinCenter, "Lublin", 40, false);
        City Gdansk = new City(GdanskCenter, "Gdansk", 50, true);

        Resource coal = new Resource(coalLocation, Resource.Type.Coal);
        Resource wood = new Resource(woodLocation, Resource.Type.Wood);
        Resource fish1 = new Resource(fishLocation1, Resource.Type.Fish);
        Resource fish2 = new Resource(fishLocation2, Resource.Type.Fish);

        List<Resource> resourcesLublin = Arrays.asList(coal);
        List<Resource> resourcesLublin2 = Arrays.asList(wood);
        List<Resource> resourcesLublin3 = Arrays.asList(fish1);
        List<Resource> resourcesGdansk = Arrays.asList(fish2);


        Lublin.addResourcesInRange(resourcesLublin, 10);
        Gdansk.addResourcesInRange(resourcesGdansk, 10);
        Lublin.addResourcesInRange(resourcesLublin2, 10);
        Lublin.addResourcesInRange(resourcesLublin3, 10);

    }
}