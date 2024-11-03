package parser;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.poi.ss.usermodel.Cell;

import java.math.BigDecimal;

import static io.vavr.API.None;
import static io.vavr.API.Some;
import static java.math.BigDecimal.ZERO;

public class SafeCell {

    private Cell cell;
    private String reference;

    public SafeCell() {}

    public SafeCell(Cell cell) {
        this.cell = cell;
        this.reference = String.format("%s!%s", cell.getSheet().getSheetName(), cell.getAddress());
    }

    public Cell getCell() {
        return cell;
    }

    public String getReference() {
        return reference;
    }

    public Either<ParserError, Double> asDouble() {
        return Try.of(() -> this.cell.getNumericCellValue()).fold(
                e -> Either.left(new ParserError.InvalidFormat(this.reference, "Numeric", e.getMessage())),
                Either::right
        );
    }

    public Either<ParserError, Integer> asInteger() {
        return Try.of(() -> Double.valueOf(this.cell.getNumericCellValue()).intValue()).fold(
                e -> Either.left(new ParserError.InvalidFormat(this.reference, "Integer", e.getMessage())),
                Either::right
        );
    }

    public Either<ParserError, String> asString() {
        return Try.of(() -> this.cell.getStringCellValue()).fold(
                e -> Either.left(new ParserError.InvalidFormat(this.reference, "String", e.getMessage())),
                Either::right
        );
    }

    private Option<Integer> doubleToInt(Double d) {
        if (new BigDecimal(d).remainder(new BigDecimal(1)).equals(ZERO)){
            return Some(d.intValue());
        } else {
            return None();
        }
    }
}
