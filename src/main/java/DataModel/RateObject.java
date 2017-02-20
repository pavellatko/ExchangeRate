package DataModel;

/**
 * Created by pavel on 2/20/17.
 */
public class RateObject {
    private String name;
    private double rate;

    public RateObject(String name, double rate) {
        this.name = name;
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public double getRate() {
        return rate;
    }
}