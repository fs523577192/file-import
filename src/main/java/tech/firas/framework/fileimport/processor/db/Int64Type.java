package tech.firas.framework.fileimport.processor.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * 64-bit integer type, corresponds to long / Long in Java,
 * usually corresponds to BIGINT type in DB
 */
public final class Int64Type extends DbDataType<Long> {

    /**
     * Creates a nullable Int64Type
     */
    public Int64Type() {
        this(false);
    }

    /**
     * Creates a Int64Type
     * @param notNull  whether this column in DB cannot be null
     */
    public Int64Type(final boolean notNull) {
        super(notNull);
    }

    @Override
    public Long fromString(final String column) throws ValidationException {
        if (null == column) {
            if (isNotNull()) {
                throw new ValidationException("int64.invalid.notnull");
            }
            return null;
        }
        try {
            return Long.parseLong(column);
        } catch (NumberFormatException ex) {
            throw new ValidationException("int64.invalid.format: " + column, ex);
        }
    }

    @Override
    public int getJavaSqlType() {
        return Types.BIGINT;
    }

    @Override
    public void setParameterForPreparedStatement0(final PreparedStatement ps, final int index, final Long value)
            throws SQLException {
        ps.setLong(index, value);
    }
}
