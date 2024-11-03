package parser;

import io.vavr.collection.Seq;
import io.vavr.control.Either;
import libs.ExcelUtils;
import org.apache.poi.ss.usermodel.Workbook;
import parser.ParserError.InvalidFormat;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.For;
import static io.vavr.API.List;
import static io.vavr.API.Match;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static io.vavr.control.Either.sequenceRight;
import static libs.ExcelUtils.getArea;
import static libs.ExcelUtils.getSafeCell;
import static parser.Parser.fail;
import static parser.Parser.success;

public class ParserUtils {

    private ParserUtils() {
    }

    /**
     * Implémentation finale de numericRange en utilisant le flatMap de Either.
     */
    public static Either<ParserError, Seq<Double>> numericRange(final Workbook wkb, final String name) {
        return getArea(wkb, name)
                .flatMap(area -> sequenceRight(List(area.getAllReferencedCells()).map(cell -> getSafeCell(wkb, cell))))
                .flatMap(safeCells -> sequenceRight(safeCells.map(SafeCell::asDouble)));
    }

    /**
     * On réutilise numericRange pour implémenter numeric, en faisant simplement
     * un Patter Matching sur la taille de la liste.
     */
    public static Parser<Double> numericV0() {
        return (wb, name) ->
                numericRange(wb, name)
                        .flatMap(seq -> Match(seq.size()).of(
                                Case($(1), right(seq.head())),
                                Case($(), left(new InvalidFormat(name, "Single Numeric", "0 or more than 1 value")))
                        ));

    }

    public static Parser<Double> numeric() {
        return Parser.flatMap(numericRange(), seq -> Match(seq.size()).of(
                Case($(1), success(seq.head())),
                Case($(), fail(name -> new InvalidFormat(name, "Single Numeric", "0 or more than 1 value")))
        ));
    }

    /**
     * L'interface fonctionnelle Parser représente
     */
    public static Parser<Seq<Double>> numericRange() {
        return ParserUtils::numericRange;
    }


    // Other building blocks
    // intRange: Parser<Seq<Integer>>
    // int: Parser<Integer>
    public static Either<ParserError, Seq<Integer>> intRange(final Workbook workbook, final String name) {
        return getArea(workbook, name)
                .flatMap(area -> sequenceRight(List(area.getAllReferencedCells()).map(cell -> getSafeCell(workbook, cell))))
                .flatMap(safeCells -> sequenceRight(safeCells.map(SafeCell::asInteger)));
    }

    public static Parser<Seq<Integer>> intRange() {
        return ParserUtils::intRange;
    }

    // stringRange: Parser<Seq<String>>
    // string: Parser<String>
    public static Either<ParserError, Seq<String>> stringRange(final Workbook workbook, final String name) {
        return getArea(workbook, name)
                .flatMap(area -> sequenceRight(List(area.getAllReferencedCells()).map(cell -> getSafeCell(workbook, cell))))
                .flatMap(safeCells -> sequenceRight(safeCells.map(SafeCell::asString)));
    }

    public static Parser<Seq<String>> stringRange() {
        return ParserUtils::stringRange;
    }



    @Deprecated
    private static Either<ParserError, Seq<Double>> numericRangeV2_Old(Workbook workbook, String name) {
        return getArea(workbook, name)
                .fold(
                        Either::left,
                        areaRef -> {
                            Either<ParserError, Seq<SafeCell>> seqSafe =
                                    sequenceRight(List(areaRef.getAllReferencedCells())
                                            .map(cellRef -> getSafeCell(workbook, cellRef)));
                            return sequenceRight(seqSafe.get().map(SafeCell::asDouble).toList());
                        });
    }


    private static Either<ParserError, Seq<Double>> numericRangeV2Fun(Workbook workbook, String name) {
        return ExcelUtils.getArea(workbook, name)
                .fold(
                        Either::left,
                        area -> sequenceRight(
                                For(List(area.getAllReferencedCells())
                                                .map(cellRef -> getSafeCell(workbook, cellRef)),
                                        cells -> For(cells.map(SafeCell::asDouble)).yield()
                                ))
                );
    }
}
