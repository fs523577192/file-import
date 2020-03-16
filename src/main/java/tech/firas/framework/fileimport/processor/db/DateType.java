package tech.firas.framework.fileimport.processor.db;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * For java.sql.Date in Java, usually corresponds to DATE type in DB.
 *
 * This class has a String parameter to specify how to parse the date String to a Date.
 */
public final class DateType extends DbDataType<Date> {

    private final String format;

    public DateType(final boolean notNull, final String format) {
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
    public Date fromString(final String source) throws ValidationException {
        if (null == source) {
            if (isNotNull()) {
                throw new ValidationException("date.invalid.notnull");
            }
            return null;
        }
        try {
            final java.util.Date utilDate = new SimpleDateFormat(this.format).parse(source);
            return new Date(utilDate.getTime());
        } catch (ParseException ex) {
            throw new ValidationException("date.invalid.format: " + source, ex);
        }
    }

    @Override
    public int getJavaSqlType() {
        return Types.DATE;
    }

    @Override
    public void setParameterForPreparedStatement0(final PreparedStatement ps, final int index, final Date value)
            throws SQLException {
        ps.setDate(index, value);
    }
}
