package parser;

import io.vavr.API;
import io.vavr.control.Either;
import org.apache.poi.ss.util.AreaReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.SafeCell_V0;

import static io.vavr.API.List;
import static io.vavr.API.println;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class S03_Safe_Cell_V0 extends WithExampleWorkbook {

    private static final String OIL_PROD = "OilProd";

    @DisplayName("getName avec une référence de cellule erronnée lance une exception")
    @Test
    void cellulesNommeesExc() {
        // On récupère la réf à une plage de cellule à partir de son nom (ici "OliProd")
        var plageOilProd = wb.getName(OIL_PROD).getRefersToFormula();

        assertEquals("Sheet1!$B$6:$J$6", plageOilProd);

        // Si la référence n'existe pas, on se prend une exception -> getName est donc une méthode "partielle"
        assertThrows(NullPointerException.class, () -> wb.getName("???").getRefersToFormula());
    }

    @DisplayName("Identifier une plage de cellule nommée")
    @Test
    void cellulesNommeesOk() {
        var plageOilProd = wb.getName(OIL_PROD).getRefersToFormula();

        var area = new AreaReference(plageOilProd, wb.getSpreadsheetVersion());

        List(area.getAllReferencedCells()).forEach(API::println);

        // On vérifie que la plage de données fait bien 9 cellules
        assertEquals(9, area.getAllReferencedCells().length);
    }

    @DisplayName("Lire une valeur en tant que numérique ou string")
    @Test
    void getNumericCellValue_returns_value_as_double() {
        var premiereCellule = extraitPremiereCellule(wb, OIL_PROD);

        // Une fois, qu'on a la cellule, on peut récupérer sa valeur
        assertEquals(10.12, premiereCellule.getNumericCellValue(), 0);

        assertEquals(
                Double.valueOf(10.12),
                Double.valueOf(premiereCellule.getNumericCellValue())
        );

        // Mais exception si on essai d'extraire la valeur en tant que string
        assertThrows(IllegalStateException.class, () -> premiereCellule.getStringCellValue());
    }

    @DisplayName("Pas d'exception avec SafeCell_V0 !!!")
    @Test
    void no_more_exceptions_with_SafeCell_VO() {
        var safeCellV0 = new SafeCell_V0(extraitPremiereCellule(wb, OIL_PROD));

        assertEquals(Either.right(10.12), safeCellV0.asDouble());

        assertEquals(
                Either.left("Cannot get a STRING value from a NUMERIC cell").getLeft(),
                safeCellV0.asString()
                        .getLeft().getMessage()
        );
    }
}
