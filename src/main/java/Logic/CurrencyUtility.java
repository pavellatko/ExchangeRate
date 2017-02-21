package Logic;

import java.util.ArrayList;
import java.util.Arrays;
/**
 * Created by pavel on 2/20/17.
 */
public class CurrencyUtility{
    public final static ArrayList<String> supportedCurrencies = new ArrayList<String>(
            Arrays.asList("USD", "JPY", "BGN", "CZK", "DKK", "GBP", "HUF", "PLN", "RON",
                    "SEK", "CHF", "NOK", "HRK", "RUB", "TRY", "AUD", "BRL", "CAD", "CNY",
                    "HKD", "IDR", "INR", "KRW", "MXN", "MYR", "NZD", "PHP", "SGD", "THB",
                    "ZAR", "ILS", "EUR"));
    public static boolean isSupported(String currency) {
        return supportedCurrencies.contains(normalize(currency));
    }
    public static String normalize(String currency) {
        return currency.trim().toUpperCase();
    }
}