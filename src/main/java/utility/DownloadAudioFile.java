package utility;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;

public class DownloadAudioFile {
    private static final String baseURL = "https://code.responsivevoice.org/getvoice.php?t=";
    private static final String PARAMETER = "&tl=fr&sv=&vn=&pitch=0.5&rate=0.5&vol=1";
    private static final String AUDIO_PATH = "resources/audio/";
    private static final String TEXT_PATH = "resources/text/";


    public static void main(String[] args) throws IOException {
        downloadFile("Hello everyone");
    }

    /**
     * Downloads a file from a URL
     * @param reference HTTP URL of the file to be downloaded
     * @throws IOException
     */
    public static void downloadFile(String reference)
            throws IOException {
        String fileURL = baseURL + URLEncoder.encode(reference,"UTF-8").replace("+", "%20")+PARAMETER;
        System.out.println(fileURL);
        URL url = new URL(fileURL);
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/4.76");
        InputStream is = conn.getInputStream();

        OutputStream outstream1 = new FileOutputStream(new File(AUDIO_PATH+System.currentTimeMillis()+".wav"));
        OutputStream outstream2 = new FileOutputStream(new File(TEXT_PATH+System.currentTimeMillis()+".txt"));

        byte[] buffer = new byte[4096];
        int len;
        while ((len = is.read(buffer)) > 0) {
            outstream1.write(buffer, 0, len);
        }
        outstream1.close();

        outstream2.write(("en-US\n").getBytes());
        outstream2.write(reference.getBytes());

        outstream2.close();
    }

}
