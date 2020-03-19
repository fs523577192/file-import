package tech.firas.framework.fileimport.processor.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * 16-bit integer type, corresponds to short / Short in Java,
 * usually corresponds to SMALLINT type in DB
 */
public final class Int16Type extends DbDataType<Short> {

    /**
     * Creates a nullable Int16Type
     */
    public Int16Type() {
        this(false);
    }

    /**
     * Creates a Int16Type
     * @param notNull  whether this column in DB cannot be null
     */
    public Int16Type(final boolean notNull) {
        super(notNull);
    }

    @Override
    public Short fromString(final String column) throws ValidationException {
        if (null == column || column.trim().isEmpty()) {
            if (isNotNull()) {
                throw new ValidationException("int16.invalid.notNull");
            }
            return null;
        }
        try {
            return Short.parseShort(column);
        } catch (NumberFormatException ex) {
            throw new ValidationException("int16.invalid.format: " + column, ex);
        }
    }

    @Override
    public int getJavaSqlType() {
        return Types.SMALLINT;
    }

    @Override
    public void setParameterForPreparedStatement0(final PreparedStatement ps, final int index, final Short value)
            throws SQLException {
        ps.setShort(index, value);
    }
}
