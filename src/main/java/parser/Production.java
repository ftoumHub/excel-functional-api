package parser;

import io.vavr.collection.List;
import io.vavr.collection.Seq;

public class Production {

    private final List<Double> oil;
    private final List<Double> gas;

    public Production(Seq<Double> oil, Seq<Double> gas) {
        this.oil = List.ofAll(oil);
        this.gas = List.ofAll(gas);
    }

    @Override
    public String toString() {
        return "Production(" + oil +  ", " + gas + ")";
    }
}
