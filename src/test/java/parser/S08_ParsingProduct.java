package parser;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Either;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.println;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static io.vavr.Patterns.$Tuple2;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static parser.CurriedParser.map;
import static parser.CurriedParserUtils.numericRange;
import static parser.CurriedParserUtils.prod;

public class S08_ParsingProduct extends WithExampleWorkbook{

    @Test
    void implementingProduction() {
        println(production_V0().parse(wb));

        println(CurriedParserUtils.production2().parse(wb));

        final CurriedParser<Seq<Double>> oil = numericRange("OilProd");
        final CurriedParser<Seq<Double>> gas = numericRange("GasProd");
        final CurriedParser<Seq<Double>> foo = numericRange("foo");

        println(product(oil, gas).parse(wb));
        println(product(oil, foo).parse(wb));

        final CurriedParser<OilProduction> oilProduction = map(numericRange("OilProd"), OilProduction::new);
        println(oilProduction.parse(wb));

        println(production().parse(wb));

        println(prod().parse(wb));
    }


    class OilProduction {
        private List<Double> value;

        public OilProduction(Seq<Double> value) {
            this.value = List.ofAll(value);
        }
    }


    public static CurriedParser<Production> production_V0() {
        final CurriedParser<Tuple2<Seq<Double>, Seq<Double>>> productFunc =
                product(numericRange("OilProd"), numericRange("GasProd"));

        return new CurriedParser<Production>() {
            @Override
            public Either<ParserError, Production> parse(Workbook workbook) {
                return productFunc.parse(workbook)
                        .flatMap(t -> right(new Production(t._1, t._2)));
            }
        };
    }

    public static <A, B> CurriedParser<Tuple2<A, B>> product(CurriedParser<A> pa, CurriedParser<B> pb) {
        return new CurriedParser<Tuple2<A, B>>() {
            @Override
            public Either<ParserError, Tuple2<A, B>> parse(Workbook workbook) {
                final Tuple2<Either<ParserError, A>, Either<ParserError, B>> t = Tuple.of(pa.parse(workbook), pb.parse(workbook));

                return Match(Tuple.of(pa.parse(workbook), pb.parse(workbook))).of(
                        Case($Tuple2($Left($()), $Right($())), () -> left(t._1.getLeft())),
                        Case($Tuple2($Right($()), $Left($())), () -> left(t._2.getLeft())),
                        Case($Tuple2($Right($()), $Right($())), () -> right(Tuple.of(t._1.get(), t._2.get()))));
            }
        };
    }

    public static CurriedParser<Production> production() {
        return map(
                product(numericRange("OilProd"), numericRange("GasProd")),
                t -> new Production(t._1, t._2)
        );
    }
}
