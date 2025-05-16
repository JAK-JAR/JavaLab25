import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DeathCaouseStatisticList {
    public Map<String, DeathCauseStatistic> deathCauseStatistics;


    public void repopulate(String filePath) throws FileNotFoundException {
        Map<String, DeathCauseStatistic> deathCauseStatistics = new HashMap<String, DeathCauseStatistic>();
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            while((line = br.readLine()) != null) {
                DeathCauseStatistic deathCauseStatistic = DeathCauseStatistic.fromCsvLine(line);
                String ICD10 = deathCauseStatistic.getICD10();
                deathCauseStatistics.put(ICD10, deathCauseStatistic);
            }

        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Error opening file: " + filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
