package recognizers;

import org.junit.Test;


import static com.google.common.truth.Truth.assertThat;

public class GoogleRecognizerTest {
    // GOOGLE_CLOUD_PROJECT should point to the json file path
    private static final String PROJECT_ID = System.getenv("GOOGLE_CLOUD_PROJECT");

    // The path to the audio file to transcribe
    private String fileName = "./resources/Eisenhower.raw";

    @Test
    public void testRecognizeFile() throws Exception {
        String result = GoogleRecognizer.syncRecognizeFile(fileName);
        System.out.println(result);
        assertThat(result).contains("my fellow Americans");
    }
}