package rpu;

import io.vavr.API;
import io.vavr.collection.List;
import libs.ExcelUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parser.IO;

import java.io.IOException;
import java.util.Objects;

import static io.vavr.API.List;
import static libs.ExcelUtils.cell;
import static libs.ExcelUtils.getCell;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Excel_API {

    private static final String NOM_PRENOM = "NOM_PRENOM";

    static Workbook workbook; // Un classeur Excel

    @BeforeAll
    public static void setup() throws IOException, InvalidFormatException {
        workbook = IO.load("Rech-personne.xlsx");
    }

    @DisplayName("getName(\"???\") avec une référence de cellule eronnée lance une exception")
    @Test
    void identifyCellByName() {
        // On récupère la référence à une plage de cellule à partir d'un nom
        assertEquals("Feuil1!$I$2:$J$760", workbook.getName(NOM_PRENOM).getRefersToFormula());

        // Si la référence n'existe pas, on se prend une exception
        // getName est donc une méthode "partielle"
        assertThrows(NullPointerException.class, () -> workbook.getName("???").getRefersToFormula());
    }

    @DisplayName("Lister les personnes en rapprochant nom et prénom")
    @Test
    void listerPersonnes() {
        listerNomsPrenoms().forEach(API::println);
    }

    private List<String> listerNomsPrenoms() {
        AreaReference area = new AreaReference(workbook.getName(NOM_PRENOM).getRefersToFormula(),
                workbook.getSpreadsheetVersion());

        return List(area.getAllReferencedCells())
                .zipWithIndex()
                .map(t -> {
                    String nomPrenom = null;
                    if (t._2 % 2 == 0) {
                        nomPrenom = String.format("%s|%s",
                                cell(workbook, t._1).getStringCellValue(),
                                getCell(workbook, t._1, 1).getStringCellValue());

                    }
                    return nomPrenom;
                })
                .toList()
                .filter(Objects::nonNull);
    }
}
