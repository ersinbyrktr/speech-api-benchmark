import recognizers.BingRecognizer;
import recognizers.GoogleRecognizer;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Application {
    // GOOGLE_CLOUD_PROJECT should point to the json file path
    private static final String PROJECT_ID = System.getenv("GOOGLE_CLOUD_PROJECT");

    // Directory paths
    private static final String AUDIO_DIR = "./resources/audio/";
    private static final String TEXT_DIR = "./resources/text/";
    private static final String AUDIO_FORMAT = ".wav";
    private static final String TEXT_FORMAT = ".txt";

    private static List<String> getResourceNames(){
        File dir = new File(TEXT_DIR);
        List<String> fileNames= new ArrayList<>();
        File[] resourceNames = dir.listFiles();
        if (resourceNames != null) {
            for (File resourceName :resourceNames) {
                fileNames.add(resourceName.getName());
            }
        }
        return fileNames;
    }

    public static void main(String[] args) throws Exception  {
        testBing();
        doBenchmark();
    }

    private static void testBing() throws Exception  {
        System.out.println("Bing Speech Api Call:");
        //InputStream input = new FileInputStream(Paths.get(AUDIO_DIR+"audio.flac").toFile());
        String result = BingRecognizer.process(Paths.get(AUDIO_DIR+"audio.wav"), "en-US");
        System.out.println(result);
    }


    private static void doBenchmark(){
        List<String> resourceNames = getResourceNames();
        for (String resourceName:resourceNames) {
            String rawFileName =  resourceName.split("\\.")[0];
            String audioFileName =  AUDIO_DIR + rawFileName+ AUDIO_FORMAT;
            String textFileName =  TEXT_DIR + rawFileName+ TEXT_FORMAT;
            BufferedReader br;
            try {
                br = new BufferedReader(new FileReader(textFileName));
                String langCode = br.readLine();
                String expectedResult = br.readLine();
                compareServices(audioFileName, langCode);
            } catch (FileNotFoundException e) {
                System.out.println(String.format("File %s cannot be found", textFileName));
                e.printStackTrace();
            }catch (IOException e) {
                System.out.println(String.format("Can't read File %s", textFileName));
                e.printStackTrace();
            }

        }
    }

    private static void compareServices(String resourceName, String lang) {
        String result = null;
        try {
            System.out.println("GCP Speech Api Call:");
            result = GoogleRecognizer.RecognizeFile(resourceName, lang);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        System.out.println(result);
        /*for (String word: result.split(" ")) {

        }*/
    }
}
