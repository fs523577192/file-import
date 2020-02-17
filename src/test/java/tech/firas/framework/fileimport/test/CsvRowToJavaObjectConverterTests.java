package tech.firas.framework.fileimport.test;

import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import tech.firas.framework.fileimport.CsvRowToJavaObjectConverter;

public class CsvRowToJavaObjectConverterTests {

    private static final Random random = new Random();

    private static String escape(final String column) {
        if (column.contains(",") || column.startsWith("\"")) {
            return '"' + column.replace("\"", "\"\"") + '"';
        }
        return column;
    }

    @Test
    public void test() throws NoSuchMethodException, ClassNotFoundException {
        final CsvRowToJavaObjectConverter<BeanForTest> converter =
                new CsvRowToJavaObjectConverter<>(BeanForTest.class.getName());
        converter.setFieldNames(Arrays.asList("aaAa", "bbBb", "ccCc", "ddDd", "eeEe", "ffFf", "ggGg", "hhHh"));

        for (int i = 65536; i > 0; i -= 1) {
            final BeanForTest a1 = getAForTest();
            final String row = a1.isAaAa() + "," + escape(getRandomColumnValue()) + "," +
                    a1.getCcCc() + "," + a1.getDdDd() + "," + a1.getEeEe() + "," + a1.getFfFf() + "," +
                    a1.getGgGg() + "," + escape(a1.getHhHh()) + "," + escape(getRandomColumnValue());
            final BeanForTest a2 = converter.convert(row);
            Assert.assertEquals(a1, a2);

            final BeanForTest a3 = converter.convert(a1.isAaAa() + "," + escape(getRandomColumnValue()) + "," +
                    a1.getCcCc() + "," + a1.getDdDd() + "," + a1.getEeEe() + "," + a1.getFfFf());
            Assert.assertNotNull(a3);
            Assert.assertNull(a3.getGgGg());
            Assert.assertNull(a3.getHhHh());
            a3.setGgGg(a1.getGgGg());
            a3.setHhHh(a1.getHhHh());
            Assert.assertEquals(a1, a3);
        }
    }

    private BeanForTest getAForTest() {
        final BeanForTest result = new BeanForTest();
        result.setAaAa(random.nextBoolean());
        result.setCcCc(random.nextBoolean());
        result.setDdDd(random.nextInt());
        result.setEeEe(random.nextInt());
        result.setFfFf(random.nextDouble());
        result.setGgGg(random.nextDouble());
        result.setHhHh(getRandomColumnValue());
        return result;
    }

    private String getRandomColumnValue() {
        final StringBuilder builder = new StringBuilder();
        for (int i = random.nextInt(10 + 1); i > 0; i -= 1) {
            int c = random.nextInt(127 - (int) ' ') + (int) ' ';
            builder.append((char) c);
        }
        return builder.toString();
    }

}
