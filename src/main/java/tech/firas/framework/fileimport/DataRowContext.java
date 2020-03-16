package tech.firas.framework.fileimport;

import java.util.Objects;

public class DataRowContext<R> {

    private DataFileContext dataFileContext;

    /**
     * Count from 1
     */
    private int rowNumber;
    private R row;
    private RowType type;

    public DataRowContext() {}

    public DataRowContext(final DataFileContext dataFileContext, final int rowNumber, final R row, final RowType type) {
        setDataFileContext(dataFileContext);
        setRowNumber(rowNumber);
        setRow(row);
        setType(type);
    }

    public DataFileContext getDataFileContext() {
        return dataFileContext;
    }

    public void setDataFileContext(final DataFileContext dataFileContext) {
        this.dataFileContext = dataFileContext;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(final int rowNumber) {
        if (rowNumber <= 0) {
            throw new IllegalArgumentException("Row number must be a positive integer");
        }
        this.rowNumber = rowNumber;
    }

    public R getRow() {
        return row;
    }

    public void setRow(final R row) {
        if (null == row) {
            throw new IllegalArgumentException("Row must not be null");
        }
        this.row = row;
    }

    public RowType getType() {
        return type;
    }

    public void setType(RowType type) {
        if (null == type) {
            throw new IllegalArgumentException("Row type must not be null");
        }
        this.type = type;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataRowContext<?> that = (DataRowContext<?>) o;
        return rowNumber == that.rowNumber &&
                Objects.equals(row, that.row) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowNumber, row, type);
    }

    @Override
    public String toString() {
        return "DataRowContext{" +
                "rowNumber=" + rowNumber +
                ", row=" + row +
                ", type=" + type +
                '}';
    }
}
