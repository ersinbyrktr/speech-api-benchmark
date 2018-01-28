package recognizers;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

public class GoogleRecognizer {

    /**
     * Performs speech recognition on raw PCM audio and prints the transcription.
     *
     * @param fileName the path to a PCM audio file to transcribe.
     */
    public static String RecognizeFile(String fileName, String langCode) throws Exception, IOException {
        SpeechClient speech = SpeechClient.create();
        StringBuilder resultText = new StringBuilder();
        Path path = Paths.get(fileName);
        byte[] data = Files.readAllBytes(path);
        ByteString audioBytes = ByteString.copyFrom(data);

        // Configure request with local raw PCM audio
        RecognitionConfig config = RecognitionConfig.newBuilder()
                .setLanguageCode(langCode)
                .build();
        RecognitionAudio audio = RecognitionAudio.newBuilder()
                .setContent(audioBytes)
                .build();

        // Use blocking call to get audio transcript
        RecognizeResponse response = speech.recognize(config, audio);
        List<SpeechRecognitionResult> results = response.getResultsList();

        for (SpeechRecognitionResult result: results) {
            // There can be several alternative transcripts for a given chunk of speech. Just use the
            // first (most likely) one here.
            SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
            resultText.append(alternative.getTranscript());
            //System.out.printf("Transcription: %s%n", alternative.getTranscript());
        }
        speech.close();
        return resultText.toString().toLowerCase(Locale.forLanguageTag(langCode));
    }
}
