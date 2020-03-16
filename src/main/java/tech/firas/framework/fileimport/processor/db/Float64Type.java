package tech.firas.framework.fileimport.processor.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.ParseException;

/**
 * 64-bit floating point type, corresponds to double / Double in Java,
 * usually corresponds to DOUBLE / REAL type in DB
 *
 * This class has a String parameter to specify how to parse the date String to a Double,
 * mainly for handling thousand separators and the decimal point of different locale
 */
public final class Float64Type extends DbDataType<Double> {

    private final String format;

    /**
     * Creates a nullable Float64Type
     */
    public Float64Type(final String format) {
        this(false, format);
    }

    /**
     * Creates a Float64Type
     * @param notNull  whether this column in DB cannot be null
     * @param format   for DecimalFormat to parse the String, null for a default DecimalFormat
     */
    public Float64Type(final boolean notNull, final String format) {
        super(notNull);
        this.format = format;
    }

    @Override
    public Double fromString(final String column) throws ValidationException {
        if (null == column) {
            if (isNotNull()) {
                throw new ValidationException("float64.invalid.notnull");
            }
            return null;
        }
        try {
            final DecimalFormat formatter = null == this.format ? new DecimalFormat() :
                    new DecimalFormat(this.format);
            final Number result = formatter.parse(column);
            return (null == result || result instanceof Double) ? (Double) result : result.doubleValue();
        } catch (ParseException ex) {
            throw new ValidationException("float64.invalid.format: " + column, ex);
        }
    }

    @Override
    public int getJavaSqlType() {
        return Types.DOUBLE;
    }

    @Override
    public void setParameterForPreparedStatement0(final PreparedStatement ps, final int index, final Double value)
            throws SQLException {
        ps.setDouble(index, value);
    }
}
