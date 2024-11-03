package parser;

import io.vavr.collection.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.vavr.API.List;
import static io.vavr.API.println;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Création d'une première version de la function numericRange.
 */
public class S02_Numeric_Range_Excel_API extends WithExampleWorkbook {

    /**
     * On retourne une liste de Double, ou une exception si on est pas sur une plage de valeurs numériques.
     *
     * La méthode n'est donc pas 'Total' au sens fonctionnel du terme.
     */
    private static List<Double> numericRangeV0(Workbook wb, String name) {
        var formula = wb.getName(name).getRefersToFormula();
        var area = new AreaReference(formula, wb.getSpreadsheetVersion());

        return List(area.getAllReferencedCells())
                .map(cellRef -> getCellAt(cellRef, wb))
                .map(Cell::getNumericCellValue);
    }

    @DisplayName("Parser la plage 'OilProd' en List<Double>")
    @Test
    void numericRangeV0_with_unsafe_excel_api() {
        // On peut maintenant facilement extraire une liste de valeur depuis un fichier excel
        var oilProdAsDouble = numericRangeV0(wb, "OilProd");

        assertThat(oilProdAsDouble)
                .containsExactly(10.12, 12.34, 8.83, 6.23, 9.18, 12.36, 16.28, 18.25, 20.01);
    }

    @DisplayName("Exception si on parse une plage contenant des String")
    @Test
    void numericRangeV0_fails_if_range_is_not_numeric() {
        // Si on essai de lire une valeur numérique dans une liste de String
        // avec getNumericCell, on se prend une exception.
        final IllegalStateException exc = assertThrows(IllegalStateException.class,
                () -> numericRangeV0(wb, "PrimaryProduct"));

        assertTrue(exc.getMessage().contains("Cannot get a NUMERIC value from a STRING cell"));
    }
}
