package tech.firas.framework.fileimport.processor.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * For java.sql.Time in Java, usually corresponds to TIME type in DB.
 *
 * This class has a String parameter to specify how to parse the time String to a Time.
 */
public final class TimeType extends DbDataType<Time> {

    private final String format;

    public TimeType(final boolean notNull, final String format) {
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
    public Time fromString(final String source) throws ValidationException {
        if (null == source) {
            if (isNotNull()) {
                throw new ValidationException("time.invalid.notnull");
            }
            return null;
        }
        try {
            final java.util.Date utilDate = new SimpleDateFormat(this.format).parse(source);
            return new Time(utilDate.getTime());
        } catch (ParseException ex) {
            throw new ValidationException("time.invalid.format: " + source, ex);
        }
    }

    @Override
    public int getJavaSqlType() {
        return Types.TIME;
    }

    @Override
    public void setParameterForPreparedStatement0(final PreparedStatement ps, final int index, final Time value)
            throws SQLException {
        ps.setTime(index, value);
    }
}
