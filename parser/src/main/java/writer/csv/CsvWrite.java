package writer.csv;

import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;

import java.io.File;
import java.util.List;

public class CsvWrite {

    public static void writeCsvFile(File file, String[] headers, List<String[]> rows) {
        final CsvWriterSettings settings = new CsvWriterSettings();
        settings.setNullValue("");
        settings.getFormat().setComment('-');
        settings.setEmptyValue("");
        settings.setSkipEmptyLines(true);

        String[][] allRows = new String[rows.size()][rows.get(0).length];
        allRows = rows.toArray(allRows);

           // Ecriture du fichier csv
        final CsvWriter writer = new CsvWriter(file, settings);
        writer.writeHeaders(headers);
        writer.writeRows(allRows);
        writer.flush();
        writer.close();
    }
}
