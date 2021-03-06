package tech.firas.framework.fileimport.processor.db;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.ParseException;

/**
 * For BigDecimal in Java, usually corresponds to DECIMAL / NUMERIC type in DB.
 *
 * This class has a String parameter to specify how to parse the date String to a BigDecimal,
 * mainly for handling thousand separators and the decimal point of different locale
 */
public final class DecimalType extends DbDataType<BigDecimal> {

    private final int precision, scale;

    private final RoundingMode roundingMode;

    private final String format;

    /**
     * Creates a DecimalType
     * @param notNull       whether this column in DB cannot be null
     * @param precision     how many decimal digits can be stored, should be a positive integer
     * @param scale         how many decimal digits after the decimal point can be stored,
     *                       should be a non-negative integer that not greater than {@link #precision}
     * @param roundingMode  specifies how to round when a column's scale is greater than {@link #scale},
     *                       if is null, the column's scale cannot be greater than {@link #scale}
     * @param format        for DecimalFormat to parse the String, null for a default DecimalFormat
     */
    public DecimalType(final boolean notNull, final int precision, final int scale,
            final RoundingMode roundingMode, final String format) {
        super(notNull);
        if (precision <= 0) {
            throw new IllegalArgumentException("precision should be a positive integer, " +
                    "but is " + precision);
        }
        this.precision = precision;

        if (scale < 0) {
            throw new IllegalArgumentException("scale should be a non-negative integer, " +
                    "but is " + scale);
        }
        if (scale > precision) {
            throw new IllegalArgumentException("scale should not be greater than the precision " +
                    precision + ", " + "but is " + scale);
        }
        this.scale = scale;

        this.roundingMode = roundingMode;

        this.format = format;
    }

    /**
     * Creates a DecimalType that allow null
     * @param precision     how many decimal digits can be stored, should be a positive integer
     * @param scale         how many decimal digits after the decimal point can be stored,
     *                       should be a non-negative integer that not greater than `precision`
     * @param roundingMode  specifies how to round when a column's scale is larger than {@link #scale},
     *                       if is null, the column's scale cannot be greater than {@link #scale}
     * @param format        for DecimalFormat to parse the String, null for a default DecimalFormat
     */
    public DecimalType(final int precision, final int scale, final RoundingMode roundingMode, final String format) {
        this(false, precision, scale, roundingMode, format);
    }

    public int getPrecision() {
        return precision;
    }

    public int getScale() {
        return scale;
    }

    public String getFormat() {
        return this.format;
    }

    @Override
    public BigDecimal fromString(final String column) throws ValidationException {
        if (null == column || column.trim().isEmpty()) {
            if (isNotNull()) {
                throw new ValidationException("decimal.invalid.notNull");
            }
            return null;
        }
        try {
            final DecimalFormat formatter = null == this.format ? new DecimalFormat() :
                    new DecimalFormat(this.format);
            formatter.setParseBigDecimal(true);
            final Number temp = formatter.parse(column);

            if (temp instanceof Double) {
                if (Double.isNaN(temp.doubleValue())) {
                    throw new ValidationException("decimal.invalid.nan");
                } else { // Double.isInfinite(temp.doubleValue())
                    throw new ValidationException("decimal.invalid.infinity");
                }
            }

            final BigDecimal bigDecimal = (BigDecimal) temp;
            final BigDecimal result;
            if (null == this.roundingMode) {
                if (-bigDecimal.scale() > this.scale) {
                    throw new ValidationException("decimal.tooBig.scale: " + -bigDecimal.scale());
                }
                result = bigDecimal;
            } else {
                try {
                    result = bigDecimal.setScale(this.scale, this.roundingMode);
                } catch (ArithmeticException ex) {
                    throw new ValidationException("decimal.tooBig.scale: " + -bigDecimal.scale());
                }
            }

            if (result.precision() > this.precision) {
                throw new ValidationException("decimal.tooBig.precision: " + result.precision());
            }
            return result;
        } catch (ParseException ex) {
            throw new ValidationException("decimal.invalid.format: " + column, ex);
        }
    }

    @Override
    public int getJavaSqlType() {
        return Types.DECIMAL;
    }

    @Override
    public void setParameterForPreparedStatement0(final PreparedStatement ps, final int index, final BigDecimal value)
            throws SQLException {
        ps.setBigDecimal(index, value);
    }
}
