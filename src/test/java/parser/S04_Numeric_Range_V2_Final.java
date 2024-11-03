package parser;

import io.vavr.control.Either;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.vavr.API.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static parser.ParserUtils.numericRange;

/**
 * Dans cette version de numericRange on utilise la classe SafeCell pour gérer le format
 * des cellules. Avec cette version de numericRange, on peut avoir, à chaque étape
 * une erreur représentée par une implémentation de {@link ParserError}.
 */
public class S04_Numeric_Range_V2_Final extends WithExampleWorkbook {

    private static final String OIL_PROD = "OilProd";

    @DisplayName("Pas d'exception avec SafeCell !!!")
    @Test
    void safe_cell_utilise_parser_error() {
        var safeCell = new SafeCell(extraitPremiereCellule(wb, OIL_PROD));

        assertEquals(Either.right(10.12), safeCell.asDouble());

        assertEquals(
                new ParserError.InvalidFormat("Sheet1!B6", "String","Cannot get a STRING value from a NUMERIC cell"),
                safeCell.asString().getLeft()
        );
    }

    @DisplayName("Version finale de numericRange avec SafeCell et ParserError")
    @Test
    void numericRange_Version_Final_with_ParserError() {

        var oilProd = numericRange(wb, "OilProd");
        assertEquals(Right(List(10.12, 12.34, 8.83, 6.23, 9.18, 12.36, 16.28, 18.25, 20.01)), oilProd);

        var invalidFormat = numericRange(wb, "PrimaryProduct");
        assertEquals(Left(
                new ParserError.InvalidFormat("Sheet1!B4", "Numeric",
                        "Cannot get a NUMERIC value from a STRING cell")
                ),
                invalidFormat);

        var missingName = numericRange(wb, "foo");
        assertEquals(new ParserError.MissingName("foo"), missingName.getLeft());
    }

}
