package Logic;

import DataModel.ApiResponse;
import com.sun.istack.internal.Nullable;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.TimeZone;

/**
 * Created by pavel on 2/20/17.
 */
public class Main {

    private static String readCurrency() {
        Scanner scanner = new Scanner(System.in);
        String currency = null;
        currency = scanner.nextLine();
        while (!CurrencyUtility.isSupported(currency)) {
            System.out.println("Unsupported currency! Try again:");
            currency = scanner.nextLine();
        }
        return CurrencyUtility.normalize(currency);
    }


    private static boolean isLoadedToday(ApiResponse response) {
        if (response == null) {
            return false;
        }
        Calendar rateDate = new GregorianCalendar();
        rateDate.setTime(response.getDate());
        rateDate.setTimeZone(TimeZone.getTimeZone("CET"));  //set api timezone
        Calendar today = Calendar.getInstance();
        today.clear(Calendar.HOUR_OF_DAY);
        today.clear(Calendar.AM_PM);
        today.clear(Calendar.MINUTE);
        today.clear(Calendar.SECOND);
        today.clear(Calendar.MILLISECOND);
        return today.equals(rateDate);
    }

    @Nullable
    private static ApiResponse parseJSON(String JSONRate) {

        if (JSONRate == null) {
            return null;
        }
        ResponseParser parser = new ResponseParser();
        ApiResponse response = null;
        try {
            response = parser.parse(JSONRate);
        } catch (Throwable ex) {
        }
        return response;
    }

    public static void main(String args[]) {

        String fromCurrency, toCurrency;
        do {
            System.out.println("Enter from currency:");
            fromCurrency = readCurrency();
            System.out.println("Enter to currency");
            toCurrency = readCurrency();
            if (fromCurrency.equals(toCurrency)) {
                System.out.println("Enter different currencies!");
            }
        } while (fromCurrency.equals(toCurrency));

        ExchangeJSONFetcher fetcher = new ExchangeJSONFetcher(fromCurrency, toCurrency);
        String JSONRate = null;
        try {
            JSONRate = fetcher.fetchFromFile();
        } catch (IOException ex) {
        }

        ApiResponse fileResponse = parseJSON(JSONRate);

        if (isLoadedToday(fileResponse) && fileResponse.getRates() != null) { //need to think about null
            System.out.println(fileResponse.getExchangeRate());
        } else {
            JSONRate = null;
            try {
                JSONRate = fetcher.fetchFromApi();
                ApiResponse apiResponse = parseJSON(JSONRate);
                System.out.println(apiResponse.getExchangeRate());
            } catch (IOException ex) {
                System.out.println("Network error!");
                if (fileResponse != null && fileResponse.getRates() != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.YYYY");
                    System.out.println(String.format("Latest loaded exchange rate by %s:",
                            dateFormat.format(fileResponse.getDate())));
                    System.out.print(fileResponse.getExchangeRate());
                }
            }
        }

    }
}
