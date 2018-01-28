import recognizers.BingRecognizer;
import recognizers.GoogleRecognizer;
import utility.ApproximateStringMatching;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        //testBing();

        doBenchmark();
    }

    private static void testBing() throws Exception  {
        System.out.println("Bing Speech Api Call:");
        //InputStream input = new FileInputStream(Paths.get(AUDIO_DIR+"audio.flac").toFile());
        String result = BingRecognizer.process(AUDIO_DIR+"1517156803155.wav", "en-US");
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
                if (expectedResult!=null && !expectedResult.isEmpty())
                    compareServices(audioFileName, expectedResult, langCode);
            } catch (FileNotFoundException e) {
                System.out.println(String.format("File %s cannot be found", textFileName));
                e.printStackTrace();
            }catch (IOException e) {
                System.out.println(String.format("Can't read File %s", textFileName));
                e.printStackTrace();
            }

        }
    }

    private static void compareServices(String audioFileName, String expectedResult, String lang) {
        String resultGoogle;
        String resultBing;

        try {
            resultGoogle = GoogleRecognizer.RecognizeFile(audioFileName, lang);
            System.out.println("GCP Speech Api Call:");
            System.out.println(resultGoogle);
            final Map<String, Integer> GoogleStats = ApproximateStringMatching.computeDistance(expectedResult, resultGoogle);

            resultBing = BingRecognizer.process(audioFileName, lang);
            if (resultBing != null) {
                System.out.println("Bing Speech Api Call:");
                System.out.println(resultBing);
                final Map<String, Integer> BingStats = ApproximateStringMatching.computeDistance(expectedResult, resultBing);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        /*for (String word: result.split(" ")) {

        }*/
    }
}
