package writer.csv;

import java.io.File;
import java.util.List;

import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;

public class CsvWrite {

    private static final CsvWriterSettings settings = new CsvWriterSettings();
    static {
        settings.setNullValue("");
        settings.getFormat().setComment('-');
        settings.setEmptyValue("");
        settings.setSkipEmptyLines(true);
    }

    public static void writeCsvFile(final File file, final String[] headers, final List<String[]> rows) {
        // Ecriture du fichier csv
        final CsvWriter writer = new CsvWriter(file, settings);
        writer.writeHeaders(headers);
        writer.writeRows(rows.toArray(new String[rows.size()][rows.get(0).length]));
        writer.flush();
        writer.close();
    }
}
