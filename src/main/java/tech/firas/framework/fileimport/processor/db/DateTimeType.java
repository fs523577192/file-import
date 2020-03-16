package tech.firas.framework.fileimport.processor.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * For java.sql.Timestamp in Java, usually corresponds to TIMESTAMP / DATETIME type in DB.
 *
 * This class has a String parameter to specify how to parse the date-time String to a Timestamp.
 */
public final class DateTimeType extends DbDataType<Timestamp> {

    private final String format;

    public DateTimeType(final boolean notNull, final String format) {
        super(notNull);
        if (null == format) {
            throw new IllegalArgumentException("format should NOT be null");
        }
        this.format = format;
    }

    public String getFormat() {
        return this.format;
    }

    @Override
    public Timestamp fromString(final String column) throws ValidationException {
        if (null == column || column.trim().isEmpty()) {
            if (isNotNull()) {
                throw new ValidationException("datetime.invalid.notnull");
            }
            return null;
        }
        try {
            final java.util.Date utilDate = new SimpleDateFormat(this.format).parse(column);
            return new Timestamp(utilDate.getTime());
        } catch (ParseException ex) {
            throw new ValidationException("datetime.invalid.format: " + column, ex);
        }
    }

    @Override
    public int getJavaSqlType() {
        return Types.TIMESTAMP;
    }

    @Override
    public void setParameterForPreparedStatement0(final PreparedStatement ps, final int index, final Timestamp value)
            throws SQLException {
        ps.setTimestamp(index, value);
    }
}
