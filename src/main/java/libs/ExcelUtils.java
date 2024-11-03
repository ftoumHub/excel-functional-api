package libs;

import io.vavr.control.Either;
import io.vavr.control.Try;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import parser.ParserError;
import parser.SafeCell;

import java.util.function.BiFunction;
import java.util.function.Function;

import static io.vavr.API.Try;
import static io.vavr.control.Either.left;

public class ExcelUtils {

    private ExcelUtils() {
    }

    public static Either<ParserError, AreaReference> getArea(Workbook wb, String name) {
        return Try(() -> new AreaReference(wb.getName(name).getRefersToFormula(), wb.getSpreadsheetVersion()))
                .fold(
                        e -> left(new ParserError.MissingName(name)),
                        Either::right
                );
    }

    public static Either<ParserError, SafeCell> getSafeCell(Workbook wb, CellReference cellRef) {
        return Try(() -> new SafeCell(cell(wb, cellRef)))
                .fold(
                        e -> left(new ParserError.MissingCell(cellRef.toString())),
                        Either::right
                );
    }

    public static Cell cell(Workbook workbook, CellReference cellRef) {
        // on a plusieurs accesseurs qui permettent, à partir d'une cellule de :
        return workbook
                .getSheet(cellRef.getSheetName())   // trouver un onglet
                .getRow(cellRef.getRow())           // trouver une colonne
                .getCell(cellRef.getCol());         // trouver une cellule
    }

    public static Cell getCell(Workbook workbook, CellReference cellRef, Integer colOffset) {
        // on a plusieurs accesseurs qui permettent, à partir d'une cellule de :
        return workbook
                .getSheet(cellRef.getSheetName())   // trouver un onglet
                .getRow(cellRef.getRow())           // trouver une colonne
                .getCell(cellRef.getCol() + colOffset);         // trouver une cellule
    }

    public static Cell getCellAt(java.util.List<CellReference> cells, int i, Workbook wb) {
        return wb.getSheet(cells.get(i).getSheetName())
                .getRow(cells.get(i).getRow())
                .getCell(cells.get(i).getCol());
    }

    static final BiFunction<Workbook, String, Either<ParserError, AreaReference>> getAreaFunc = (workbook, name) ->
            Try(() -> new AreaReference(workbook.getName(name).getRefersToFormula(), workbook.getSpreadsheetVersion()))
                    .fold(
                            e -> left(new ParserError.MissingName(name)),
                            Either::right
                    );

    static final Function<Workbook, Function<String, Either<ParserError, AreaReference>>> getAreaCurried = workbook -> name ->
            Try(() -> new AreaReference(workbook.getName(name).getRefersToFormula(), workbook.getSpreadsheetVersion()))
                    .fold(
                            e -> left(new ParserError.MissingName(name)),
                            Either::right
                    );
}
