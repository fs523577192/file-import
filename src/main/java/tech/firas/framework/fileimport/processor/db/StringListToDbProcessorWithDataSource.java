package tech.firas.framework.fileimport.processor.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import tech.firas.framework.fileimport.DataFileContext;

/**
 * This class gets a Connection for importing each file from {@code dataSource}
 */
public class StringListToDbProcessorWithDataSource extends AbstractStringListToDbProcessor {

    private static final Logger logger = Logger.getLogger(StringListToDbProcessorWithDataSource.class.getName());

    /**
     * Indicates whether this processor commits transaction for every batch insert.
     *
     * If the value is {@code false}, this processor only commits in afterProcessFile.
     *
     * If the value is {@code null}, this processor will not begin / commit / rollback
     * the transaction in case that the transaction is managed by a container.
     *
     * The default value is {@code true}.
     */
    private Boolean autoCommit = true;

    private DataSource dataSource;

    protected Map<String, PreparedStatement> statementCache = new HashMap<>();

    @Override
    public void beforeProcessFile(final String filePath) throws Exception {
        super.beforeProcessFile(filePath);

        validateProcessorParameters();

        final Connection connection = this.dataSource.getConnection();
        final Boolean autoCommit = this.autoCommit;
        if (null != autoCommit) {
            connection.setAutoCommit(autoCommit);
        }
        logger.finer("Connection created for " + filePath);

        try {
            final PreparedStatement ps = connection.prepareStatement(this.getInsertSql());
            this.statementCache.put(filePath, ps);
        } catch (Exception ex) {
            connection.close();
            throw ex;
        }
        logger.finer("PreparedStatement cached for " + filePath);
    }

    @Override
    public void afterProcessFile(final DataFileContext dataFileContext) throws Exception {
        super.afterProcessFile(dataFileContext);

        final String filePath = dataFileContext.getFilePath();
        final Connection connection;
        final PreparedStatement ps = this.statementCache.remove(filePath);
        logger.finer("statementCache clear for " + filePath);
        try {
            connection = ps.getConnection();
        } catch (SQLException ex) {
            logger.severe("Fail to get Connection for " + filePath);
            throw ex;
        }

        try {
            ps.close();
            logger.finer("PreparedStatement closed for " + filePath);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Fail to close PreparedStatement for " + filePath, ex);
        }

        final Boolean autoCommit = this.autoCommit;
        try {
            if (null != autoCommit) {
                connection.commit();
            }
        } catch (SQLException ex) {
            if (autoCommit) {
                logger.log(Level.SEVERE, "Fail to finally commit transaction for " + filePath, ex);
            } else {
                logger.severe("Fail to finally commit transaction for " + filePath);
                throw ex;
            }
        }

        try {
            connection.close();
            logger.finer("Connection closed for " + filePath);
        } catch (SQLException ex) {
            logger.severe("Fail to close Connection for " + filePath);
            throw ex;
        }
    }

    @Override
    protected void insertOneRowIntoBatch(final String filePath, final int rowNumber,
            final List<String> rowData) throws Exception {
        final PreparedStatement ps = this.statementCache.get(filePath);
        if (null == ps) {
            throw new IllegalStateException("No PreparedStatement is found for " + filePath);
        }

        Iterator<String> iterator = rowData.iterator();
        
        final List<DbDataType<?>> typeList = this.getColumnDataTypeList();
        int i = 1;
        for (final DbDataType<?> item : typeList) {
            if (!iterator.hasNext()) {
                logger.info("The size of the " + rowNumber +
                        " row < the size of columnDataTypeList " + typeList.size() +
                        ", filePath: " + filePath);
                item.setParameterForPreparedStatement(ps, i, null);
            } else {
                item.setParameterForPreparedStatement(ps, i, iterator.next());
            }
            i += 1;
        }
        ps.addBatch();
    }

    @Override
    protected void insertBatch(final String filePath) throws Exception {
        final PreparedStatement ps = this.statementCache.get(filePath);
        if (null == ps) {
            throw new IllegalStateException("No PreparedStatement is found for " + filePath);
        }
        ps.executeBatch();
    }

    @Override
    protected void cleanResource(final String filePath) {
        final PreparedStatement ps = this.statementCache.remove(filePath);
        logger.finer("statementCache clear for " + filePath);
        try {
            final Connection connection = ps.getConnection();

            try {
                ps.close();
                logger.finer("PreparedStatement closed for " + filePath);
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Fail to close PreparedStatement for " + filePath, ex);
            }

            try {
                connection.close();
                logger.finer("Connection closed for " + filePath);
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Fail to close PreparedStatement for " + filePath, ex);
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Fail to get Connection for " + filePath, ex);
        }
    }

    private void validateProcessorParameters() {
        if (null == this.getInsertSql()) {
            throw new IllegalStateException("insertSql is null");
        }
        if (this.getInsertSql().trim().isEmpty()) {
            throw new IllegalStateException("insertSql is blank");
        }
        if (null == this.dataSource) {
            throw new IllegalStateException("dataSource is null");
        }
    }

    public Boolean getAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(final Boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
