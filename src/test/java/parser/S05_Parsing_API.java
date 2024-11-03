package parser;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import parser.ParserError.InvalidFormat;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.List;
import static io.vavr.API.Match;
import static io.vavr.API.Right;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static io.vavr.control.Either.sequenceRight;
import static libs.ExcelUtils.getArea;
import static libs.ExcelUtils.getSafeCell;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static parser.ParserUtils.numeric;
import static parser.ParserUtils.numericRange;
import static parser.ParserUtils.numericV0;

/**
 * Si on veut généraliser le principe du parseur, on se rend compte que l'API
 * que l'on souhaite construire va ressembler à ça :
 * <p>
 * Either<ParserError, Seq<Double>>  numericRange(Workbook wb, String name)
 * Either<ParserError, Seq<Integer>> intRange(Workbook wb, String name)
 * Either<ParserError, Seq<String>>  stringRange(Workbook wb, String name)
 * <p>
 * Either<ParserError, Double>  numeric(Workbook wb, String name)
 * Either<ParserError, Integer> int(Workbook wb, String name)
 * Either<ParserError, String>  string(Workbook wb, String name)
 * <p>
 * D'ou l'idée de créer l'interface fonctionnelle {@link Parser#parse(Workbook, String)}
 * <p>
 * à partir d'ici, on manipule des fonctions et non plus les valeurs retournés par la méthode numericRange
 */
public class S05_Parsing_API extends WithExampleWorkbook {

    /**
     * Pour éviter l'effet "Sapin de noel" de la version précédente, on utilise flatMap.
     */
    private static Parser<Double> numericFromScratch() {
        return (wb, name) ->
                getArea(wb, name)
                        .flatMap(area -> sequenceRight(List(area.getAllReferencedCells()).map(cell -> getSafeCell(wb, cell))))
                        .flatMap(safeCells -> sequenceRight(safeCells.map(SafeCell::asDouble)))
                        .flatMap(seq ->
                                Match(seq.size()).of(
                                        Case($(1), right(seq.head())),
                                        Case($(), left(new InvalidFormat(name, "Single Numeric", "0 or more than 1 value")))
                                )
                        );
    }

    @Test
    void rewrite_Numeric_From_Scratch() {
        assertEquals(numericFromScratch().parse(wb, "ExplorationFee"), Right(1.4));
    }

    // Slide Numeric
    @Test
    void reusing_previous_numericRange() {
        // numeric utilise le flatMap sur l'either retourné par numericRangeV2
        // On transforme ainsi un Either<ParserError, Seq<Double>> en Either<ParserError, Double>
        // flatMap va retourner la première erreur rencontrée sous forme de ParserError ou bien enchainer les traitements
        assertEquals(numericV0().parse(wb, "ExplorationFee"), Right(1.4));

        assertThat(numericV0().parse(wb, "OilProd").getLeft())
                .isInstanceOf(InvalidFormat.class);

        assertThat(numericV0().parse(wb, "foo").getLeft())
                .isInstanceOf(ParserError.MissingName.class);
    }

    @Test
    void numericAsParserDouble() {
        assertEquals(numeric().parse(wb, "ExplorationFee").get(), Double.valueOf(1.4));

        assertEquals(numericRange().parse(wb, "OilProd").get(),
                List(10.12, 12.34, 8.83, 6.23, 9.18, 12.36, 16.28, 18.25, 20.01));
    }
}
