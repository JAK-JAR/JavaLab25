package pl.umcs.oop.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.umcs.oop.auth.TokenService;
import pl.umcs.oop.image.DatabaseService;
import pl.umcs.oop.image.ImageService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;

@RestController
public class WebController {
    private final TokenService tokenService;
    private final ImageService imageService;
    private final DatabaseService databaseService;

    public WebController(TokenService tokenService, ImageService imageService, DatabaseService databaseService) {
        this.tokenService = tokenService;
        this.imageService = imageService;
        this.databaseService = databaseService;
    }

    // Registers new user
    @PostMapping("/register")
    public Map<String, String> register() {
        var token = tokenService.generateToken();
        return Map.of("token", token.token(), "createdAt", token.creationTime().toString());
    }

    // Lists all tokens
    @GetMapping("/tokens")
    public List<Map<String, Object>> tokens() {
        return tokenService.getAllTokens();
    }

    // Returns current image
    @GetMapping(value = "/image", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getImage() throws IOException {
        BufferedImage image = imageService.getImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    // Processes pixel placement
    @PostMapping("/pixel")
    public ResponseEntity<Void> setPixel(@RequestBody PixelRequest request) {
        if (!tokenService.isTokenActive(request.id())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (request.x() < 0 || request.x() >= 512 || request.y() < 0 || request.y() >= 512) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Instant now = Instant.now();
        databaseService.insertPixel(request.id(), request.x(), request.y(), request.color(), now);
        imageService.setPixel(request.x(), request.y(), request.color());

        return ResponseEntity.ok().build();
    }

    public record PixelRequest(String id, int x, int y, String color) {}
}