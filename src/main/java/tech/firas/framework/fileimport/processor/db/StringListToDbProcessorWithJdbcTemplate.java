package tech.firas.framework.fileimport.processor.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.jdbc.core.JdbcTemplate;

import tech.firas.framework.fileimport.DataFileContext;

/**
 * This class uses a JdbcTemplate to import data to DB
 */
public class StringListToDbProcessorWithJdbcTemplate extends AbstractStringListToDbProcessor {

    private static final Logger logger = Logger.getLogger(StringListToDbProcessorWithJdbcTemplate.class.getName());

    private JdbcTemplate jdbcTemplate;

    protected Map<String, int[]> sqlTypeCache = new HashMap<>();
    protected Map<String, List<Object[]>> rowParamCache = new HashMap<>();

    @Override
    public void beforeProcessFile(final String filePath) throws Exception {
        final List<DbDataType<?>> typeList = this.getColumnDataTypeList();
        if (null == typeList) {
            throw new IllegalStateException("columnDataTypeList is null");
        }
        if (typeList.isEmpty()) {
            throw new IllegalStateException("columnDataTypeList is empty");
        }

        final int[] types = new int[typeList.size()];
        int i = 0;
        for (DbDataType item : typeList) {
            types[i++] = item.getJavaSqlType();
        }
        this.sqlTypeCache.put(filePath, types);

        super.beforeProcessFile(filePath);
    }

    @Override
    public void afterProcessFile(final DataFileContext dataFileContext) throws Exception {
        super.afterProcessFile(dataFileContext);
        this.cleanResource(dataFileContext.getFilePath());
    }

    @Override
    protected void insertOneRowIntoBatch(final String filePath, final int rowNumber,
            final List<String> rowData) throws Exception {
        List<Object[]> params = this.rowParamCache.get(filePath);
        if (null == params) {
            params = new ArrayList<>(this.getBatchSize());
            this.rowParamCache.put(filePath, params);
        }

        Iterator<String> iterator = rowData.iterator();

        final List<DbDataType<?>> typeList = this.getColumnDataTypeList();
        final Object[] param = new Object[typeList.size()];
        int i = 0;
        for (final DbDataType<?> item : typeList) {
            if (!iterator.hasNext()) {
                logger.info('[' + filePath + "] The size of the " + rowNumber +
                        " row < the size of columnDataTypeList " + typeList.size() +
                        ", filePath: " + filePath);
                param[i] = item.fromString(null);
            } else {
                param[i] = item.fromString(iterator.next());
            }
            i += 1;
        }
        params.add(param);
    }

    @Override
    protected void insertBatch(final String filePath) throws Exception {
        if (null == this.jdbcTemplate) {
            throw new IllegalStateException("jdbcTemplate is null");
        }

        final List<Object[]> params = this.rowParamCache.get(filePath);
        this.jdbcTemplate.batchUpdate(this.getInsertSql(), params, this.sqlTypeCache.get(filePath));
        params.clear();
    }

    @Override
    protected void cleanResource(final String filePath) {
        this.sqlTypeCache.remove(filePath);
        logger.finer("sqlTypeCache clear for " + filePath);
        this.rowParamCache.remove(filePath);
        logger.finer("rowParamCache clear for " + filePath);
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
