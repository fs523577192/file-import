package tech.firas.framework.fileimport.processor;

import java.util.Arrays;
import java.util.List;

import com.opencsv.ICSVParser;

import tech.firas.framework.fileimport.DataRowContext;

public class CsvRowToStringListProcessor extends AbstractChainedFileProcessor<String, List<String>> {

    private ICSVParser csvParser;

    public ICSVParser getCsvParser() {
        return csvParser;
    }

    public void setCsvParser(final ICSVParser csvParser) {
        this.csvParser = csvParser;
    }

    @Override
    public DataRowContext<String> processRow(final DataRowContext<String> dataRowContext) throws Exception {
        if (null == this.csvParser) {
            throw new IllegalStateException("csvParser is null");
        }
        ensureNextProcessNotNull();

        final String[] columns = this.csvParser.parseLine(dataRowContext.getRow());

        final DataRowContext<List<String>> newContext = new DataRowContext<>(dataRowContext.getDataFileContext(),
                dataRowContext.getRowNumber(), Arrays.asList(columns), dataRowContext.getType());
        this.getNextProcessor().processRow(newContext);
        return dataRowContext;
    }
}
