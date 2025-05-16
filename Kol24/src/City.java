import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.*;

public class City {
    public String capital;
    public int timeZone;
    public double latitude, longitude;

    public City(String capital, int timeZone, double latitude, double longitude) {
        this.capital = capital;
        this.timeZone = timeZone;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCapital() {
        return capital;
    }

    public int getTimeZone() {
        return timeZone;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    private static City parseLine(String line) {
        String[] parts = line.split(",");
        if(parts.length != 4) {
            throw new IllegalArgumentException("Invalid line format");
        } else {
            String capital = parts[0].trim();
            int timeZone = Integer.parseInt(parts[1].trim());
            String[] latTokens = parts[2].trim().split(" ");
            double latitude = Double.parseDouble(latTokens[0].trim());
            String latDir = latTokens[1].trim();
            if(latDir.equalsIgnoreCase("S"))  latitude = -latitude;

            String[] lonTokens = parts[3].trim().split(" ");
            double longitude = Double.parseDouble(lonTokens[0].trim());
            String lonDir = lonTokens[1].trim();
            if(lonDir.equalsIgnoreCase("W")) longitude = -longitude;
            City city = new City(capital, timeZone, latitude, longitude);
            return city;
        }
    }

    public static Map<String, City> parseFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("Invalid file path");
        }
        Map<String, City> cities = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while((line = br.readLine()) != null) {
                try {
                    City city = parseLine(line);
                    cities.put(city.capital, city);
                } catch (IllegalArgumentException e) {
                    System.err.printf("Error parsing line: %s. %s%n", line, e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("File reading error: " + e.getMessage());
        }
        return cities;
    }

    public LocalTime localMeanTime(LocalTime zoneTime) {
        LocalTime utc = zoneTime.minusHours(this.timeZone);
        double offset = this.longitude / 15;
        int hoursToAdd = (int) offset;
        double franctionOfHours = offset - hoursToAdd;
        long secondsToAdd = Math.round(franctionOfHours * 3600);
        LocalTime meanTime = zoneTime.plusHours(hoursToAdd).plusSeconds(secondsToAdd);
        return meanTime;
    }

    public static Comparator<City> worstTimeZoneFit() {
        return (city1, city2) -> {
            double diff1 = Math.abs(city1.longitude / 15.0 - city1.timeZone);
            double diff2 = Math.abs(city2.longitude / 15.0 - city2.timeZone);
            return Double.compare(diff2, diff1);
        };
    }

    public static void generateAnalogClocksSvg(List<City> cities, AnalogClock clock) {
        Path dir = Paths.get(clock.toString());
        try {
            Files.createDirectories(dir);
            for (City c : cities) {
                clock.setCity(c);
                clock.toSvg(c.getCapital() + ".txt");
            }
        } catch (IOException e) {
            throw new RuntimeException("Nie można zapisać plików SVG: " + e.getMessage(), e);
        }
    }
}


