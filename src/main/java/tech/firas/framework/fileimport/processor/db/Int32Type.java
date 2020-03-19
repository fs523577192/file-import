package tech.firas.framework.fileimport.processor.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * 32-bit integer type, corresponds to int / Integer in Java,
 * usually corresponds to INTEGER type in DB
 */
public class Int32Type extends DbDataType<Integer> {

    /**
     * Creates a nullable Int32Type
     */
    public Int32Type() {
        this(false);
    }

    /**
     * Creates a Int32Type
     * @param notNull  whether this column in DB cannot be null
     */
    public Int32Type(final boolean notNull) {
        super(notNull);
    }

    @Override
    public Integer fromString(final String column) throws ValidationException {
        if (null == column || column.trim().isEmpty()) {
            if (isNotNull()) {
                throw new ValidationException("int32.invalid.notNull");
            }
            return null;
        }
        try {
            return Integer.parseInt(column);
        } catch (NumberFormatException ex) {
            throw new ValidationException("int32.invalid.format: " + column, ex);
        }
    }

    @Override
    public int getJavaSqlType() {
        return Types.INTEGER;
    }

    @Override
    public void setParameterForPreparedStatement0(final PreparedStatement ps, final int index, final Integer value)
            throws SQLException {
        ps.setInt(index, value);
    }
}
