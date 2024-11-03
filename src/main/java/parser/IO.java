package parser;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.FileInputStream;
import java.io.IOException;

import static io.vavr.API.println;

public final class IO {

    public static Workbook load(String fileName) throws IOException, InvalidFormatException {
        println(String.format("Loading %s", fileName));

        return WorkbookFactory.create(new FileInputStream(fileName));
    }
}
