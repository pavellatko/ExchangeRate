package Logic;

import DataModel.ApiResponse;
import com.sun.istack.internal.Nullable;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.*;

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
        rateDate.add(Calendar.HOUR_OF_DAY, 16);
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DAY_OF_YEAR, -1);
        return (today.before(rateDate));
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

    enum LoadStatus {
        Success,
        NetworkErrorOldCache,       //unable to load data from api, but there is old data in cache
        NetworkCacheError   //unable to load data from api and cache
    }

    private static class LoadResult {
        private LoadStatus status;
        private ApiResponse response;

        public LoadStatus getStatus() {
            return status;
        }

        public void setStatus(LoadStatus status) {
            this.status = status;
        }

        public ApiResponse getResponse() {
            return response;
        }

        public void setResponse(ApiResponse response) {
            this.response = response;
        }
    }

    private static class LoadDataCallable implements Callable {
        private String fromCurrency, toCurrency;

        public LoadDataCallable(String fromCurrency, String toCurrency) {
            this.fromCurrency = fromCurrency;
            this.toCurrency = toCurrency;
        }

        public LoadResult call() {
            ExchangeJSONFetcher fetcher = new ExchangeJSONFetcher(fromCurrency, toCurrency);
            String JSONRate = null;
            try {
                JSONRate = fetcher.fetchFromFile();
            } catch (IOException ex) {
            }

            ApiResponse fileResponse = parseJSON(JSONRate);

            LoadResult result = new LoadResult();

            if (isLoadedToday(fileResponse) && fileResponse.getRates() != null) {
                result.setResponse(fileResponse);
                result.setStatus(LoadStatus.Success);
            } else {
                JSONRate = null;
                try {
                    JSONRate = fetcher.fetchFromApi();
                    ApiResponse apiResponse = parseJSON(JSONRate);
                    result.setResponse(apiResponse);
                    result.setStatus(LoadStatus.Success);
                } catch (IOException ex) {
                    result.setStatus(LoadStatus.NetworkCacheError);
                    if (fileResponse != null && fileResponse.getRates() != null) {
                        result.setStatus(LoadStatus.NetworkErrorOldCache);
                        result.setResponse(fileResponse);
                    }
                }
            }
            return result;
        }
    }

    public static void main(String args[]) throws InterruptedException {
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

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<LoadResult> future = executorService.submit(new LoadDataCallable(
                fromCurrency, toCurrency));

        while(!future.isDone()) {
            System.out.print(".");
            Thread.sleep(1000);
        }
        System.out.println();


        try{
            switch(future.get().getStatus()) {
                case Success:
                    System.out.println(future.get().getResponse().getExchangeRate());
                    break;
                case NetworkCacheError:
                    System.out.println("Network error!");
                    break;
                case NetworkErrorOldCache:
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.YYYY");
                    System.out.println(String.format("Latest loaded exchange rate by %s:",
                            dateFormat.format(future.get().getResponse().getDate())));
                    System.out.print(future.get().getResponse().getExchangeRate());
                    break;
            }
        } catch (ExecutionException ex) {
        }
        executorService.shutdown();
    }
}
