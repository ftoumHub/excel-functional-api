package util;

import io.vavr.control.Either;
import org.apache.poi.ss.usermodel.Cell;

import static io.vavr.API.Try;

/**
 * Classe représentant une cellule Excel et sa référence sous forme
 * d'une chaine de caractère de type Nom_de_la_feuille!Reference
 * (ex: Feuille1!B4)
 */
public class SafeCell_V0 {

    private final Cell cell;
    private final String reference;

    public SafeCell_V0(Cell cell) {
        this.cell = cell;
        this.reference = String.format("%s!%s", cell.getSheet().getSheetName(), cell.getAddress());
    }

    public Either<ParserErrorClass, Double> asDouble() {
        return Try(this.cell::getNumericCellValue)
                .fold(
                        e -> Either.left(new ParserErrorClass(this.reference, "Numeric", e.getMessage())),
                        Either::right
                );
    }

    public Either<ParserErrorClass, String> asString() {
        return Try(this.cell::getStringCellValue)
                .fold(
                        e -> Either.left(new ParserErrorClass(this.reference, "String", e.getMessage())),
                        Either::right
                );
    }
}
