package pl.umcs.oop.threadshomework;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ImageProcessor {
    private BufferedImage image;

    public void loadImage(String path) throws IOException {
        image = ImageIO.read(new File(path));
    }

    public void saveImage(String path, String format) throws IOException {
        if (image != null) {
            ImageIO.write(image, format, new File(path));
        } else {
            throw new IllegalStateException("No image loaded to save.");
        }
    }

    public void increaseBrightness(int value) {
        if (image == null) {
            throw new IllegalStateException("No image loaded.");
        }

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));
                int r = Math.min(255, color.getRed() + value);
                int g = Math.min(255, color.getGreen() + value);
                int b = Math.min(255, color.getBlue() + value);
                image.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
    }

    public void increaseBrightnessMultithreaded(int value) throws InterruptedException {
        if (image == null) {
            throw new IllegalStateException("No image loaded.");
        }

        int cores = Runtime.getRuntime().availableProcessors();
        Thread[] threads = new Thread[cores];
        int heightPerThread = image.getHeight() / cores;

        for (int i = 0; i < cores; i++) {
            final int startY = i * heightPerThread;
            final int endY = (i == cores - 1) ? image.getHeight() : startY + heightPerThread;

            threads[i] = new Thread(() -> {
                for (int y = startY; y < endY; y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        Color color = new Color(image.getRGB(x, y));
                        int r = Math.min(255, color.getRed() + value);
                        int g = Math.min(255, color.getGreen() + value);
                        int b = Math.min(255, color.getBlue() + value);
                        image.setRGB(x, y, new Color(r, g, b).getRGB());
                    }
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }

    public void increaseBrightnessWithThreadPool(int value) throws InterruptedException {
        if (image == null) {
            throw new IllegalStateException("No image loaded.");
        }

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int y = 0; y < image.getHeight(); y++) {
            final int row = y;
            executor.submit(() -> {
                for (int x = 0; x < image.getWidth(); x++) {
                    Color color = new Color(image.getRGB(x, row));
                    int r = Math.min(255, color.getRed() + value);
                    int g = Math.min(255, color.getGreen() + value);
                    int b = Math.min(255, color.getBlue() + value);
                    image.setRGB(x, row, new Color(r, g, b).getRGB());
                }
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            Thread.sleep(10);
        }
    }

    public int[] calculateHistogram(int channel) throws InterruptedException {
        if (image == null) {
            throw new IllegalStateException("No image loaded.");
        }

        int[] histogram = new int[256];
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int y = 0; y < image.getHeight(); y++) {
            final int row = y;
            executor.submit(() -> {
                for (int x = 0; x < image.getWidth(); x++) {
                    Color color = new Color(image.getRGB(x, row));
                    int value = switch (channel) {
                        case 0 -> color.getRed();
                        case 1 -> color.getGreen();
                        case 2 -> color.getBlue();
                        default -> throw new IllegalArgumentException("Invalid channel.");
                    };
                    synchronized (histogram) {
                        histogram[value]++;
                    }
                }
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            Thread.sleep(10);
        }

        return histogram;
    }

    public BufferedImage generateHistogramImage(int[] histogram) {
        int width = 256;
        int height = 100;
        BufferedImage histogramImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int max = 0;
        for (int value : histogram) {
            max = Math.max(max, value);
        }

        for (int x = 0; x < width; x++) {
            int barHeight = (int) ((histogram[x] / (double) max) * height);
            for (int y = height - 1; y >= height - barHeight; y--) {
                histogramImage.setRGB(x, y, Color.WHITE.getRGB());
            }
        }

        return histogramImage;
    }
}