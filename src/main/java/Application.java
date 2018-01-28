import recognizers.BingRecognizer;
import recognizers.GoogleRecognizer;
import utility.ApproximateStringMatching;

import java.io.*;
import java.util.*;

import static utility.ApproximateStringMatching.printResult;

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
        Set<Map<String, Object>> totalResultGCP = new HashSet<>();
        Set<Map<String, Object>> totalResultBing = new HashSet<>();
        Map[] stats;
        for (String resourceName:resourceNames) {
            String rawFileName =  resourceName.split("\\.")[0];
            String audioFileName =  AUDIO_DIR + rawFileName+ AUDIO_FORMAT;
            String textFileName =  TEXT_DIR + rawFileName+ TEXT_FORMAT;
            BufferedReader br;
            try {
                br = new BufferedReader(new FileReader(textFileName));
                String langCode = br.readLine();
                String expectedResult = br.readLine();
                if (expectedResult!=null && !expectedResult.isEmpty()) {

                    stats = compareServices(audioFileName, expectedResult, langCode);

                    totalResultGCP.add(stats[0]);
                    totalResultBing.add(stats[1]);
                }
            } catch (FileNotFoundException e) {
                System.out.println(String.format("File %s cannot be found", textFileName));
                e.printStackTrace();
            }catch (IOException e) {
                System.out.println(String.format("Can't read File %s", textFileName));
                e.printStackTrace();
            }

        }
        //Create and initialize map
        double errorGCP=0d;
        double errorBing=0d;

        //We need know to do the mean of the result
        for (Map<String, Object> map : totalResultGCP) {
            //printResult(map);
            try {
                errorGCP += (double)map.get("Percentage_total_error");
            }catch (NullPointerException npe){
                System.out.println("Attribute 'Percentage_total_error' nor found !");
            }
        }
        errorGCP/=(double)totalResultGCP.size();


        for (Map<String, Object> map : totalResultBing) {
            try {
                errorBing += (double)map.get("Percentage_total_error");
            }catch (NullPointerException npe){
                System.out.println("Attribute 'Percentage_total_error' nor found !");
            }
        }
        errorBing/=(double)totalResultBing.size();

        System.out.println("Percentage_total_error of GCP is : "+((int)(errorGCP*100))/100d+"%");
        System.out.println("Percentage_total_error of Bing is : "+((int)(errorBing*100))/100d+"%");

    }

    private static Map[] compareServices(String audioFileName, String expectedResult, String lang) {
        String resultGoogle;
        String resultBing;

        Map[] stats = new Map[2];

        try {
            resultGoogle = GoogleRecognizer.RecognizeFile(audioFileName, lang);
            System.out.println("\nGCP Speech Api Call:");
            System.out.println(resultGoogle);
            stats[0] = new ApproximateStringMatching().computeDistance(expectedResult, resultGoogle);
            ApproximateStringMatching.printResult(stats[0]);

            resultBing = BingRecognizer.process(audioFileName, lang);
            if (resultBing != null) {
                System.out.println("\nBing Speech Api Call:");
                System.out.println(resultBing);
                stats[1] = new ApproximateStringMatching().computeDistance(expectedResult, resultBing);
                ApproximateStringMatching.printResult(stats[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return stats;
        }


        return stats;
    }
}
