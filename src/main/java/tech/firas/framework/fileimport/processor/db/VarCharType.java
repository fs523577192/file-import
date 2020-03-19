package tech.firas.framework.fileimport.processor.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * For String in Java, usually corresponds to VARCHAR type in DB
 */
public final class VarCharType extends DbDataType<String> {

    private final int length;

    /**
     * Creates a VarCharType
     * @param notNull    whether this column in DB cannot be null
     * @param length  the length of the String that can be stored
     */
    public VarCharType(final boolean notNull, final int length) {
        super(notNull);
        if (length <= 0) {
            throw new IllegalArgumentException("length should be a positive integer, " +
                    "but is " + length);
        }
        this.length = length;
    }

    /**
     * Creates a VarCharType that allow null
     * @param length  the length of the String that can be stored
     */
    public VarCharType(final int length) {
        this(false, length);
    }

    public int getLength() {
        return this.length;
    }

    @Override
    public String fromString(final String column) throws ValidationException {
        if (null == column) {
            if (isNotNull()) {
                throw new ValidationException("varchar.invalid.notNull");
            }
            return null;
        }
        if (column.length() > this.length) {
            throw new ValidationException("varchar.toolong.length: " +
                    column.length() + " > " + this.length);
        }
        return column;
    }

    @Override
    public int getJavaSqlType() {
        return Types.VARCHAR;
    }

    @Override
    public void setParameterForPreparedStatement0(final PreparedStatement ps, final int index, final String value)
            throws SQLException {
        ps.setString(index, value);
    }
}
