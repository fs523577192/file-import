package tech.firas.framework.fileimport.processor.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import tech.firas.framework.fileimport.DataFileContext;
import tech.firas.framework.fileimport.DataRowContext;
import tech.firas.framework.fileimport.processor.DataFileProcessor;

public abstract class AbstractStringListToDbProcessor implements DataFileProcessor<List<String>> {

    private static final Logger logger = Logger.getLogger(AbstractStringListToDbProcessor.class.getName());

    private Map<String, Integer> rowCountCache = new HashMap<>();

    /**
     * The SQL used to insert a row to DB
     */
    private String insertSql;

    /**
     * The number of rows to insert in one batch, default is 100
     */
    private int batchSize = 100;

    /**
     * Whether this processor allow the number of the columns in one row
     * is less than the size of {@code columnDataTypeList}.
     *
     * If allow, {@link #processRow(DataRowContext)} will set null for
     * the remaining parameter of something like a {@code PreparedStatement}.
     *
     * If not allow, {@link #processRow(DataRowContext)} will throw
     * {@code IllegalArgumentException} if the number of columns in a
     * row is less.
     */
    private boolean allowLessColumns = true;

    private List<DbDataType<?>> columnDataTypeList;

    @Override
    public void beforeProcessFile(String filePath) throws Exception {
        this.rowCountCache.put(filePath, 0);
        logger.finer("rowCountCache initialized for " + filePath);
    }

    @Override
    public DataRowContext<List<String>> processRow(final DataRowContext<List<String>> dataRowContext) throws Exception {
        final String filePath = dataRowContext.getDataFileContext().getFilePath();
        try {
            final List<DbDataType<?>> typeList = this.getColumnDataTypeList();
            validateProcessorParameters(typeList);

            final List<String> row = dataRowContext.getRow();
            validateRow(typeList, row);

            int rowNumber = this.rowCountCache.get(filePath);

            this.insertOneRowIntoBatch(filePath, rowNumber, row);

            rowNumber += 1;
            this.rowCountCache.put(filePath, rowNumber);

            if (rowNumber % this.batchSize == 0) {
                logger.finer("insertBatch, rowNumber: " + rowNumber);
                this.insertBatch(filePath);
                logger.finer("insertBatch done, rowNumber: " + rowNumber);
            }

            return dataRowContext;
        } catch (Exception ex) {
            cleanResource(filePath);
            throw ex;
        }
    }

    @Override
    public void afterProcessFile(DataFileContext dataFileContext) throws Exception {
        int rowNumber = this.rowCountCache.remove(dataFileContext.getFilePath());
        logger.finer("rowCountCache clear for " + dataFileContext.getFilePath());
        if (rowNumber % this.batchSize != 0) {
            logger.finer("The last batch has not been inserted, insertBatch, rowNumber: " + rowNumber);
            this.insertBatch(dataFileContext.getFilePath());
            logger.finer("insertBatch done, rowNumber: " + rowNumber);
        }
    }

    protected abstract void insertOneRowIntoBatch(String filePath, int rowNumber, List<String> rowData) throws Exception;
    protected abstract void insertBatch(String filePath) throws Exception;
    protected abstract void cleanResource(String filePath);

    private void validateProcessorParameters(final List<DbDataType<?>> typeList) {
        if (null == typeList) {
            throw new IllegalStateException("columnDataTypeList is null");
        }
        if (typeList.isEmpty()) {
            throw new IllegalStateException("columnDataTypeList is empty");
        }
        if (null == this.insertSql) {
            throw new IllegalStateException("insertSql is null");
        }
        if (this.insertSql.trim().isEmpty()) {
            throw new IllegalStateException("insertSql is blank");
        }
    }

    private void validateRow(final List<DbDataType<?>> typeList, final List<String> row) {
        if (null == row) {
            throw new IllegalArgumentException("row is null");
        }
        if (row.isEmpty()) {
            throw new IllegalArgumentException("row is empty");
        }

        if (!this.allowLessColumns && row.size() < typeList.size()) {
            throw new IllegalArgumentException("The size of row " + row.size() + " < " +
                    " the size of columnDataTypeList " + typeList.size());
        }
    }

    public String getInsertSql() {
        return insertSql;
    }

    public void setInsertSql(final String insertSql) {
        this.insertSql = insertSql;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(final int batchSize) {
        if (batchSize < 1) {
            throw new IllegalArgumentException("batchSize must be a positive integer");
        }
        this.batchSize = batchSize;
    }

    public boolean isAllowLessColumns() {
        return allowLessColumns;
    }

    public void setAllowLessColumns(final boolean allowLessColumns) {
        this.allowLessColumns = allowLessColumns;
    }

    public List<DbDataType<?>> getColumnDataTypeList() {
        return columnDataTypeList;
    }

    public void setColumnDataTypeList(final List<DbDataType<?>> columnDataTypeList) {
        this.columnDataTypeList = columnDataTypeList;
    }
}
