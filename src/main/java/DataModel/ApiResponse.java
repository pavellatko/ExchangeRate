package DataModel;

import java.util.Date;
import java.util.Locale;

/**
 * Created by pavel on 2/20/17.
 */
public class ApiResponse {
    private String base;
    private Date date;
    private RateObject rates;

    public ApiResponse(String base, RateObject rates) {
        this.base = base;
        this.rates = rates;
    }

    public String getExchangeRate() {
        String format = "%s => %s %.3f";
        return String.format(Locale.US, format, base, rates.getName(), rates.getRate());
    }

    public String getBase() {
        return base;
    }
    public Date getDate() {
        return date;
    }
    public RateObject getRates() {
        return rates;
    }
}