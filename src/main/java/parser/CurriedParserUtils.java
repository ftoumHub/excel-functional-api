package parser;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Iterator;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Either;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.function.Function;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.For;
import static io.vavr.API.Match;
import static io.vavr.control.Either.sequenceRight;
import static libs.ExcelUtils.getArea;
import static libs.ExcelUtils.getSafeCell;
import static parser.CurriedParser.fail;
import static parser.CurriedParser.flatMap;
import static parser.CurriedParser.success;

public class CurriedParserUtils {

    private CurriedParserUtils() {}

    static CurriedParser<Seq<SafeCell>> range(String name) {
        return new CurriedParser<Seq<SafeCell>>() {
            @Override
            public Either<ParserError, Seq<SafeCell>> parse(Workbook workbook) {
                return getArea(workbook, name)
                        .flatMap(area -> sequenceRight(List.of(area.getAllReferencedCells())
                                .map(cell -> getSafeCell(workbook, cell))));
            }
        };
    }

    public static CurriedParser<Seq<Double>> numericRange(String name) {
        final Function<Seq<SafeCell>, Either<ParserError, Seq<Double>>> f =
                safeCells -> Either.sequenceRight(safeCells.map(SafeCell::asDouble).toList());

        return range(name)
                .flatMapF(safeCells -> f.apply(safeCells));
    }

    static CurriedParser<Double> numeric(String name) {
        return flatMap(numericRange(name), d -> Match(d.asJava().size()).of(
                Case($(1), success(d.head())),
                Case($(), fail(new ParserError.InvalidFormat(name, "Single Numeric", "0 or more than 1 value")))
        ));
    }

    static Tuple product_ko(Workbook workbook) {

        Iterator<Tuple2<Seq<Double>, Seq<Double>>> t = For(
                numericRange("OilProd").parse(workbook),
                numericRange("GasProd").parse(workbook)
        ).yield(
                (oilParseur, gasParseur) -> Tuple.of(oilParseur, gasParseur)
        );

        return t.get();
    }

    public static CurriedParser<Production> production2() {
        return numericRange("OilProd")
                .flatMap(oil ->
                        flatMap(numericRange("GasProd"), gas ->
                                success(new Production(oil, gas))));
    }

    public static CurriedParser<Production> prod() {
        return flatMap(numericRange("OilProd"), oil ->
                flatMap(numericRange("GasProd"), gas ->
                        success(new Production(oil, gas))));
    }
}
