
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class PlantUMLRunner {
    private static String plantUMLPath;
    public static void setjarPath(String plantUMLPath) {
        PlantUMLRunner.plantUMLPath = plantUMLPath;
    }

    public static void generateDiagram(String umlData, String dirPath, String filename)
            throws IOException, InterruptedException
    {
        umlData = "@startuml\n" + umlData + "\n@enduml";
        File dir = new File(dirPath);
        if(!dir.exists()) {
            if(!dir.mkdirs()){
                throw new IOException("Nie udało się stworzyć katalogu: "+dir.getAbsolutePath());
            }
        }
        File outDiagram = new File(dir, filename);

        Process plantUMLProcess = new ProcessBuilder(
                "java", "-jar", plantUMLPath, "-pipe")
                .redirectOutput(outDiagram)
                .start();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                plantUMLProcess.getOutputStream()))) {
            writer.write(umlData);
        }
        int exitCode = plantUMLProcess.waitFor();
        if (exitCode != 0 ) {
            System.err.println("PlantUML zakończył się z błędem: "+exitCode);
        }

    }
}
