package parser;

import io.vavr.collection.Seq;
import io.vavr.control.Either;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.ParserErrorClass;
import util.SafeCell_V0;

import static io.vavr.API.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Dans cette version de numericRange on utilise une classe pour représenter les erreurs possibles.
 */
public class S04_Numeric_Range_V1 extends WithExampleWorkbook {

    /**
     * En utilisant SafeCell, on renvoi une Either<ParserErrorClass, Double> pour chaque cellule
     */
    private static Either<ParserErrorClass, Seq<Double>> numericRangeV1(Workbook wb, String name) {
        var formula = wb.getName(name).getRefersToFormula();
        var area = new AreaReference(formula, wb.getSpreadsheetVersion());

        // Problème, ici, on se retrouve avec une List<Either<ParserErrorClass, Double>>
        // On transforme cette liste en Either<ParserErrorClass, Seq<Double>> avec Either.sequenceRight

        //final List<Cell> cells = List.of(area.getAllReferencedCells())
        //        .map(cellRef -> wb.getSheet(cellRef.getSheetName()).getRow(cellRef.getRow()).getCell(cellRef.getCol())).toList();
        //final List<SafeCell_V0> safeCells = cells.map(SafeCell_V0::new);
        //final List<Either<ParserErrorClass, Double>> doubles = safeCells.map(SafeCell_V0::asDouble);
        //final Either<ParserErrorClass, Seq<Double>> seqs = Either.sequenceRight(doubles);

        return Either.sequenceRight(List(area.getAllReferencedCells())
                .map(cellRef -> getCellAt(cellRef, wb))
                .map(SafeCell_V0::new)
                .map(SafeCell_V0::asDouble)
        );
    }

    /**
     * On construit une classe {@link ParserErrorClass} qui va représenter un type d'erreur possible.
     * <p>
     * On utilise également {@link SafeCell} qui est un wrapper "safe"
     */
    @DisplayName("Avec ParserError en tant que classe")
    @Test
    void numRangeV1_WithSafeCell() {
        // On utilise maintenant SafeCell pour retourner un Either<ParserErrorClass, Double> pour chaque cellule.
        Either<ParserErrorClass, Seq<Double>> oilProd = numericRangeV1(wb, "OilProd");

        assertThat(oilProd.get().toJavaList())
                .containsExactly(10.12, 12.34, 8.83, 6.23, 9.18, 12.36, 16.28, 18.25, 20.01);
    }

    @Test
    void cellErrorHandling() {
        Either<ParserErrorClass, Seq<Double>> primaryProdEither = numericRangeV1(wb, "PrimaryProduct");

        assertEquals("Cannot get a NUMERIC value from a STRING cell", primaryProdEither.getLeft().getMessage());
    }

    @Test
    void stillNotTotalFunction() {
        // numericRangeV1 n'est pas une fonction totale,
        // on peut de se prendre une exception si on recherche une plage de cellule inexistante
        assertThrows(NullPointerException.class, () -> numericRangeV1(wb, "foo"));
    }
}
