package parser;

import libs.ExcelUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;

import static io.vavr.API.List;

public class WithExampleWorkbook {

    static Workbook wb; // Un classeur Excel

    @BeforeAll
    public static void setup() throws IOException, InvalidFormatException {
        wb = IO.load("example.xlsx");
    }

    public static Cell getCellAt(CellReference cellRef, Workbook wb) {
        return wb
                .getSheet(cellRef.getSheetName())
                .getRow(cellRef.getRow())
                .getCell(cellRef.getCol());
    }


    Cell extraitPremiereCellule(Workbook wb, String rangeRef) {
        var area = new AreaReference(
                wb.getName(rangeRef).getRefersToFormula(),
                wb.getSpreadsheetVersion());

        var cellRef = List(area.getAllReferencedCells()).head();

        return ExcelUtils.cell(wb, cellRef);
    }
}
