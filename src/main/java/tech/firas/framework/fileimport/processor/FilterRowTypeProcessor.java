package tech.firas.framework.fileimport.processor;

import java.util.HashSet;
import java.util.Set;

import tech.firas.framework.fileimport.DataRowContext;
import tech.firas.framework.fileimport.RowType;

/**
 * Filter the rows passing to the next DataFileProcessor.
 *
 * Notice: this processor should be chained after SetRowTypeProcessor.
 *
 * @param <R>  the type of the row
 */
public class FilterRowTypeProcessor<R> extends AbstractChainedFileProcessor<R, R> {

    private Set<RowType> allowRowTypes;

    public Set<RowType> getAllowRowTypes() {
        return allowRowTypes;
    }

    public void setAllowRowTypes(final Set<RowType> allowRowTypes) {
        this.allowRowTypes = allowRowTypes;
    }

    /**
     * Set the RowTypes that can pass to the next DataFileProcessor.
     *
     * Notice: null means passing all rows to the next DataFileProcessor.
     *
     * @param allowRowTypes  an Iterable of the RowTypes that can pass this filter
     */
    public void setAllowRowTypes(final Iterable<RowType> allowRowTypes) {
        if (null == allowRowTypes || allowRowTypes instanceof Set) {
            this.setAllowRowTypes((Set<RowType>) allowRowTypes);
        } else {
            this.allowRowTypes = new HashSet<>(4, 1f);
            for (final RowType rowType : allowRowTypes) {
                this.allowRowTypes.add(rowType);
            }
        }
    }

    @Override
    public DataRowContext<R> processRow(DataRowContext<R> dataRowContext) throws Exception {
        ensureNextProcessNotNull();
        if (null == this.allowRowTypes || this.allowRowTypes.contains(dataRowContext.getType())) {
            return this.getNextProcessor().processRow(dataRowContext);
        } else {
            return dataRowContext;
        }
    }
}
