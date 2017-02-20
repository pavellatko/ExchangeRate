package DataModel;

/**
 * Created by pavel on 2/20/17.
 */
public class ApiResponse {
    private String base;
    private RateObject rates;

    public ApiResponse(String base, RateObject rates) {
        this.base = base;
        this.rates = rates;
    }

    public String getBase() {
        return base;
    }

    public RateObject getRates() {
        return rates;
    }
}