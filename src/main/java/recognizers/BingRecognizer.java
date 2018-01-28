/*
Copyright (c) Microsoft Corporation
All rights reserved. 
MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy of this 
software and associated documentation files (the "Software"), to deal in the Software 
without restriction, including without limitation the rights to use, copy, modify, merge, 
publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons 
to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or 
substantial portions of the Software.
THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package recognizers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import utility.BingAuthentication;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.stream.Collectors;

public class BingRecognizer {

  private static final String REQUEST_URI = "https://speech.platform.bing.com/speech/recognition/%s/cognitiveservices/v1";
  private static final String PARAMETERS = "language=%s&format=%s";
  private static final String AUTH_KEY = System.getenv("BING_AUTH_KEY");
  private static final String mode = "conversation";

  private static  URL buildRequestURL(String langCode) throws MalformedURLException {
    String url = String.format(REQUEST_URI, mode);
    String params = String.format(PARAMETERS, langCode, "detailed");
    return new URL(String.format("%s?%s", url, params));
  }

  private static final BingAuthentication getAuth(){
      return new BingAuthentication(AUTH_KEY);
  }

  private static  HttpURLConnection connect(String langCode) throws IOException {


    HttpURLConnection connection = (HttpURLConnection) buildRequestURL(langCode).openConnection();
    connection.setDoInput(true);
    connection.setDoOutput(true); 
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-type", "audio/x-flac; rate=16000");
    connection.setRequestProperty("Accept", "application/json;text/xml");
    connection.setRequestProperty("Authorization", "Bearer " + getAuth().getToken());
    connection.connect();
    return connection;
  }

  private static  String getResponse(HttpURLConnection connection) throws IOException {
    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
      throw new RuntimeException(String.format("Something went wrong, server returned: %d (%s)",
          connection.getResponseCode(), connection.getResponseMessage()));
    }
    try (BufferedReader reader = 
        new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
      return reader.lines().collect(Collectors.joining());
    }
  }

  private static  HttpURLConnection upload(InputStream is, HttpURLConnection connection) throws IOException {
    try (OutputStream output = connection.getOutputStream()) {
      byte[] buffer = new byte[1024];
      int length;
      while ((length = is.read(buffer)) != -1) {
        output.write(buffer, 0, length);
      }
      output.flush();
    }
    return connection;
  }

  private static  HttpURLConnection upload(Path filepath, HttpURLConnection connection) throws IOException {
    try (OutputStream output = connection.getOutputStream()) {
      Files.copy(filepath, output);
    }
    return connection;
  }

  public static String process(String filename, String langCode) throws IOException {
      final Path filepath = Paths.get(filename);
    JsonParser parser = new JsonParser();
    String res = getResponse(upload(filepath, connect(langCode)));
    JsonObject o = parser.parse(res).getAsJsonObject();
    if(o.has("RecognitionStatus")){
        final JsonObject bestResult = o.getAsJsonArray("NBest").get(0).getAsJsonObject();
        final String itn = bestResult.get("ITN").toString();
        return itn.substring(1, itn.length()-1).toLowerCase(Locale.forLanguageTag(langCode));
    }
    else{
        return null;
    }
  }
}
