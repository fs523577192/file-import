package tech.firas.framework.fileimport.processor.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

abstract class DbDataType<T> {

    private boolean notNull;

    protected DbDataType(final boolean notNull) {
        this.notNull = notNull;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public abstract T fromString(String column) throws ValidationException;

    /**
     *
     * @return  a constants that are used to identify generic SQL types (JDBC types)
     * @see java.sql.Types
     */
    public abstract int getJavaSqlType();

    public void setParameterForPreparedStatement(final PreparedStatement ps, final int index, final String column)
            throws SQLException, ValidationException {
        final T value = fromString(column);
        if (null == value) {
            ps.setNull(index, getJavaSqlType());
        } else {
            this.setParameterForPreparedStatement0(ps, index, value);
        }
    }

    protected abstract void setParameterForPreparedStatement0(
            PreparedStatement ps, int index, T value) throws SQLException;
}
