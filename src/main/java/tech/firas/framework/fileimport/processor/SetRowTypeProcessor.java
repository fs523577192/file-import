package tech.firas.framework.fileimport.processor;

import java.util.HashMap;
import java.util.Map;

import tech.firas.framework.fileimport.DataFileContext;
import tech.firas.framework.fileimport.DataRowContext;
import tech.firas.framework.fileimport.DataRowJudge;
import tech.firas.framework.fileimport.RowType;

/**
 * Set RowType and row count in DataRowContext and then pass the row to the next DataFileProcessor
 * @param <R>  the type of the input row
 */
public class SetRowTypeProcessor<R> extends AbstractChainedFileProcessor<R, R> {

    private Map<String, RowType> lastRowTypeCache = new HashMap<>();

    private DataRowJudge<R> dataRowJudge;

    public DataRowJudge<R> getDataRowJudge() {
        return dataRowJudge;
    }

    public void setDataRowJudge(final DataRowJudge<R> dataRowJudge) {
        this.dataRowJudge = dataRowJudge;
    }

    @Override
    public void afterProcessFile(final DataFileContext dataFileContext) throws Exception {
        this.lastRowTypeCache.remove(dataFileContext.getFilePath());
        this.getNextProcessor().afterProcessFile(dataFileContext);
    }

    @Override
    public DataRowContext<R> processRow(DataRowContext<R> dataRowContext) throws Exception {
        ensureNextProcessNotNull();
        final String filePath = dataRowContext.getDataFileContext().getFilePath();
        final RowType rowType = this.dataRowJudge.test(dataRowContext.getRowNumber(), dataRowContext.getRow(),
                this.lastRowTypeCache.get(filePath)); // for the first row, the previous row type is null
        dataRowContext.setType(rowType);
        this.lastRowTypeCache.put(filePath, rowType);
        setRowCount(dataRowContext);
        return this.getNextProcessor().processRow(dataRowContext);
    }

    private static void setRowCount(final DataRowContext<?> dataRowContext) {
        final DataFileContext dataFileContext = dataRowContext.getDataFileContext();
        switch (dataRowContext.getType()) {
            case DATA:
                dataFileContext.setDataRowCount(dataFileContext.getDataRowCount() + 1);
                break;
            case HEADER:
                dataFileContext.setHeaderRowCount(dataFileContext.getHeaderRowCount() + 1);
                break;
            case FOOTER:
                dataFileContext.setFooterRowCount(dataFileContext.getFooterRowCount() + 1);
                break;
        }
    }
}
