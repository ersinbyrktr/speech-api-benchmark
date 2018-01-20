package recognizers;


import okhttp3.Request;
import okhttp3.Response;
import okhttp3.OkHttpClient;


public class BingRecognizer {
    /**
     * Performs speech recognition on raw PCM audio and prints the transcription.
     *
     * @param fileName the path to a PCM audio file to transcribe.
     */

    public static String RecognizeFile(String fileName) throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://speech.platform.bing.com/speech/recognition/interactive/cognitiveservices/v1?language=en-us&format=detailed")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                return response.body().string();
            }else{
                return "";
            }
        }
        catch (NullPointerException e){
            return "";
        }
    }
}
