package Logic;

import java.io.IOException;

/**
 * Created by pavel on 2/20/17.
 */
public class Main {
    public static void main(String args[]) {
        ExchangeJSONFetcher fetcher = new ExchangeJSONFetcher("USD", "RUB");
        try {
            fetcher.fetchFromApi();
        } catch (IOException ex) {

        }
    }

}
