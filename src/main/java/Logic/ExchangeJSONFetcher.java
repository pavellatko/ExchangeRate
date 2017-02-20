package Logic;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by pavel on 2/20/17.
 */
public class ExchangeJSONFetcher {

    private final static String apiUrl = "http://api.fixer.io/latest?base=%s&symbols=%s";
    private final static String cacheFolder = "cache/";
    private String fromCurrency;
    private String toCurrency;
    public ExchangeJSONFetcher(String fromCurrency, String toCurrency) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
    }



    private String formatApiUrl() {
        return String.format(apiUrl, fromCurrency, toCurrency);
    }

    private String readFromBufferedReader(BufferedReader reader) throws IOException {
        StringBuilder buf = new StringBuilder();
        String line = null;
        while ((line=reader.readLine()) != null) {
            buf.append(line + "\n");
        }
        return buf.toString();
    }

    private String getFilePath() {
        String fileFormat = "%s%s-%s.txt";
        return String.format(fileFormat, cacheFolder, fromCurrency, toCurrency);
    }

    public String fetchFromApi() throws IOException {
        BufferedReader reader = null;
        URL url = new URL(formatApiUrl());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setReadTimeout(10000);
        connection.connect();
        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String response = readFromBufferedReader(reader);
        connection.disconnect();
        try {
            saveToFile(response);
        } catch (IOException ex) {
        }
        return response;
    }

    public String fetchFromFile() throws IOException {
        BufferedReader fileBufferedReader = new BufferedReader(new FileReader(getFilePath()));
        return readFromBufferedReader(fileBufferedReader);
    }

    public void saveToFile(String data) throws IOException {
        File file = new File(getFilePath());
        file.getParentFile().mkdirs();
        file.createNewFile();
        PrintWriter writer = new PrintWriter(file);
        writer.print(data);
        writer.close();
    }

}
