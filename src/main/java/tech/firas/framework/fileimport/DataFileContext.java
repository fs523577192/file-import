package tech.firas.framework.fileimport;

public class DataFileContext {

    private int headerRowCount;
    private int dataRowCount;
    private int footerRowCount;
    private Object attachment;

    public int getHeaderRowCount() {
        return headerRowCount;
    }

    public void setHeaderRowCount(final int headerRowCount) {
        this.headerRowCount = headerRowCount;
    }

    public int getDataRowCount() {
        return dataRowCount;
    }

    public void setDataRowCount(final int dataRowCount) {
        this.dataRowCount = dataRowCount;
    }

    public int getFooterRowCount() {
        return footerRowCount;
    }

    public void setFooterRowCount(final int footerRowCount) {
        this.footerRowCount = footerRowCount;
    }

    public Object getAttachment() {
        return attachment;
    }

    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        return "DataFileContext{" +
                "headerRowCount=" + headerRowCount +
                ", dataRowCount=" + dataRowCount +
                ", footerRowCount=" + footerRowCount +
                '}';
    }
}
