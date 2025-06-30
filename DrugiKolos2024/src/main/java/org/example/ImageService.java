package pl.umcs.oop.image;

import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.awt.image.BufferedImage;
import java.util.List;

@Service
public class ImageService {
    private static final int WIDTH = 512;
    private static final int HEIGHT = 512;
    private final BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    private final DatabaseService databaseService;

    public ImageService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    // Initializes image from database on startup
    @PostConstruct
    public void init() {
        clearImage();
        rebuildImage();
    }

    // Clears image to black
    public void clearImage() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                image.setRGB(x, y, 0);
            }
        }
    }

    // Rebuilds image from database events
    public void rebuildImage() {
        databaseService.getAllPixels().forEach(pixel ->
                setPixel(pixel.x(), pixel.y(), pixel.color())
        );
    }

    // Sets a pixel color
    public void setPixel(int x, int y, String color) {
        int rgb = (0xFF << 24) | Integer.parseInt(color, 16);
        image.setRGB(x, y, rgb);
    }

    // Returns current image
    public BufferedImage getImage() {
        return image;
    }
}