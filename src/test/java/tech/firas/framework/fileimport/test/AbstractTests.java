package tech.firas.framework.fileimport.test;

import org.junit.Before;

public abstract class AbstractTests {

    @Before
    public void setupLogger() {
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    }
}
