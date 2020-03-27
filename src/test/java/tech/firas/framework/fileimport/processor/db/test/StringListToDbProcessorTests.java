package tech.firas.framework.fileimport.processor.db.test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import tech.firas.framework.fileimport.AbstractDataFileReader;
import tech.firas.framework.fileimport.DefaultPlainTextDataFileReader;
import tech.firas.framework.fileimport.test.AbstractTests;

public class StringListToDbProcessorTests extends AbstractTests {

    private static final String querySql = "SELECT aa, bb, cc, dd, ee, ff, gg, hh, ii " +
            "FROM t_test ORDER BY pk";

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private ApplicationContext applicationContext = null;
    private DataSource dataSource = null;

    @Override
    public void setup() throws Exception {
        super.setup();

        this.applicationContext = new ClassPathXmlApplicationContext("application-context.xml");
        this.dataSource = this.applicationContext.getBean("testDataSource", DriverManagerDataSource.class);
        try (final Connection connection = this.dataSource.getConnection()) {
            try (final Statement statement = connection.createStatement()) {
                statement.executeUpdate("DROP TABLE t_test IF EXISTS");
            }
            logger.info("t_test does not exist now");

            try (final Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE t_test(" +
                        "pk INT AUTO_INCREMENT," +
                        "aa VARCHAR (5) NOT NULL," +
                        "bb VARCHAR (100)," +
                        "cc VARCHAR (5)," +
                        "dd INT NOT NULL," +
                        "ee INT," +
                        "ff DECIMAL(11,3) NOT NULL," +
                        "gg DECIMAL(10,2)," +
                        "hh VARCHAR(100)," +
                        "ii TIMESTAMP," +
                        "PRIMARY KEY (pk)" +
                        ")");
            }
            logger.info("t_test is created");
            connection.commit();
        }
    }

    @Test
    public void testWithDataSource() throws Exception {
        logger.info("BEGIN");
        this.truncateTable();
        final AbstractDataFileReader<String> dataFileReader = this.applicationContext.getBean(
                "plainTextDataFileReaderWithDataSource", DefaultPlainTextDataFileReader.class);
        testTxt(dataFileReader);
        this.truncateTable();
        testCsv(dataFileReader);
        logger.info("END");
    }

    @Test
    public void testWithJdbcTemplate() throws Exception {
        logger.info("BEGIN");
        this.truncateTable();
        final AbstractDataFileReader<String> dataFileReader = this.applicationContext.getBean(
                "plainTextDataFileReaderWithJdbcTemplate", DefaultPlainTextDataFileReader.class);
        testTxt(dataFileReader);
        this.truncateTable();
        testCsv(dataFileReader);
        logger.info("END");
    }

    private void testTxt(final AbstractDataFileReader<String> dataFileReader) throws Exception {
        dataFileReader.readDataFile("src/test/resources/default_plain_text_data_file.txt", null);

        try (final Connection connection = this.dataSource.getConnection()) {
            try (final Statement statement = connection.createStatement()) {
                try (final ResultSet resultSet = statement.executeQuery(querySql)) {
                    commonTests(resultSet);
                    Assert.assertFalse(resultSet.next());
                }
            }
        }
    }

    private void testCsv(final AbstractDataFileReader<String> dataFileReader) throws Exception {
        dataFileReader.readDataFile("src/test/resources/default_plain_text_data_file.csv", null);

        try (final Connection connection = this.dataSource.getConnection()) {
            try (final Statement statement = connection.createStatement()) {
                try (final ResultSet resultSet = statement.executeQuery(querySql)) {
                    commonTests(resultSet);
                    test5thOr6thRow(resultSet);
                    test5thOr6thRow(resultSet);
                    test7thRow(resultSet);
                    test4thOr8thRow(resultSet);
                    Assert.assertFalse(resultSet.next());
                }
            }
        }
    }

    private void commonTests(final ResultSet resultSet) throws SQLException {
        testFirst2Rows(resultSet);
        testFirst2Rows(resultSet);
        test3rdRow(resultSet);
        test4thOr8thRow(resultSet);
    }

    private void testFirst2Rows(final ResultSet resultSet) throws SQLException {
        Assert.assertTrue(resultSet.next());
        Assert.assertEquals("true", resultSet.getString("aa"));
        Assert.assertEquals("true", resultSet.getString("cc"));
        Assert.assertEquals(Integer.MIN_VALUE, resultSet.getInt("dd"));
        Assert.assertEquals(Integer.MAX_VALUE, resultSet.getInt("ee"));
        Assert.assertEquals(new BigDecimal("12345678.090"), resultSet.getBigDecimal("ff"));
        Assert.assertEquals(new BigDecimal("-12345678.09"), resultSet.getBigDecimal("gg"));
        Assert.assertEquals("cdeCDE 123!@#$%^&*()-=_+[]\\{}|;':\"<>?./", resultSet.getString("hh"));
        Assert.assertEquals("1970-01-01 00:00:00", this.formatter.format(resultSet.getTimestamp("ii")));
    }

    private void test3rdRow(final ResultSet resultSet) throws SQLException {
        Assert.assertTrue(resultSet.next());

        Assert.assertEquals("false", resultSet.getString("aa"));

        Assert.assertEquals("", resultSet.getString("cc"));

        Assert.assertEquals(0, resultSet.getInt("dd"));
        Assert.assertFalse(resultSet.wasNull());

        Assert.assertEquals(0, resultSet.getInt("ee"));
        Assert.assertTrue(resultSet.wasNull());

        Assert.assertEquals(new BigDecimal("0.001"), resultSet.getBigDecimal("ff"));

        Assert.assertNull(resultSet.getBigDecimal("gg"));

        Assert.assertEquals("cdeCDE 098!@#$%^&*()-=_+[]\\{}|;':\"<>?./", resultSet.getString("hh"));

        Assert.assertEquals("2000-02-29 23:59:59", this.formatter.format(resultSet.getTimestamp("ii")));
    }

    private void test4thOr8thRow(final ResultSet resultSet) throws SQLException {
        Assert.assertTrue(resultSet.next());

        Assert.assertEquals("false", resultSet.getString("aa"));

        Assert.assertEquals("false", resultSet.getString("cc"));

        Assert.assertEquals(0, resultSet.getInt("dd"));
        Assert.assertFalse(resultSet.wasNull());

        Assert.assertEquals(0, resultSet.getInt("ee"));
        Assert.assertFalse(resultSet.wasNull());

        Assert.assertEquals(new BigDecimal("-0.001"), resultSet.getBigDecimal("ff"));

        Assert.assertNull(resultSet.getBigDecimal("gg"));
        Assert.assertNull(resultSet.getString("hh"));
        Assert.assertNull(resultSet.getTimestamp("ii"));
    }

    private void test5thOr6thRow(final ResultSet resultSet) throws SQLException {
        Assert.assertTrue(resultSet.next());
        Assert.assertEquals("true", resultSet.getString("aa"));
        Assert.assertEquals("true", resultSet.getString("cc"));
        Assert.assertEquals(Integer.MIN_VALUE, resultSet.getInt("dd"));
        Assert.assertEquals(Integer.MAX_VALUE, resultSet.getInt("ee"));
        Assert.assertEquals(new BigDecimal("12345678.090"), resultSet.getBigDecimal("ff"));
        Assert.assertEquals(new BigDecimal("-12345678.09"), resultSet.getBigDecimal("gg"));
        Assert.assertEquals("cdeCDE 123!@#$%^&*()-=_+[]\\{}|;':\"<>?,./", resultSet.getString("hh"));
        Assert.assertEquals("2020-02-29 12:30:30", this.formatter.format(resultSet.getTimestamp("ii")));
    }

    private void test7thRow(final ResultSet resultSet) throws SQLException {
        Assert.assertTrue(resultSet.next());

        Assert.assertEquals("false", resultSet.getString("aa"));

        Assert.assertEquals("", resultSet.getString("cc"));

        Assert.assertEquals(0, resultSet.getInt("dd"));
        Assert.assertFalse(resultSet.wasNull());

        Assert.assertEquals(0, resultSet.getInt("ee"));
        Assert.assertTrue(resultSet.wasNull());

        Assert.assertEquals(new BigDecimal("0.001"), resultSet.getBigDecimal("ff"));

        Assert.assertNull(resultSet.getBigDecimal("gg"));

        Assert.assertEquals("cdeCDE 098!@#$%^&*()-=_+[]\\{}|;':\"<>?,./", resultSet.getString("hh"));

        Assert.assertEquals("1999-12-31 23:59:59", this.formatter.format(resultSet.getTimestamp("ii")));
    }

    private void truncateTable() throws SQLException {
        try (final Connection connection = this.dataSource.getConnection()) {
            try (final Statement statement = connection.createStatement()) {
                statement.executeUpdate("TRUNCATE TABLE t_test");
            }
        }
        logger.info("t_test is truncated");
    }
}
