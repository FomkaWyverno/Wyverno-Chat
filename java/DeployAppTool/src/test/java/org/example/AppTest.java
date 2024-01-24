package org.example;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    private static final Logger logger = LoggerFactory.getLogger(AppTest.class);
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

    public static void main(String[] args) throws IOException {
        File file = Files.createTempFile("","").toFile();
        File file1 = new File("text.txt");

        try {
            boolean successTempFile = file.setReadable(false);
            boolean successFile = file1.setReadable(false);

            logger.info("Temp File({}) setReadable is success operation? - {} - Can read? - {}", file, successTempFile, file.canRead());
            logger.info("File({}) setReadable is success operation? - {} - Can read? - {}", file1, successFile, file1.canRead());
            if (file1.canRead()) {
                logger.info(new String(Files.readAllBytes(file1.toPath()), StandardCharsets.UTF_8));
            }

        } catch (Exception e) {
            logger.error("Error -> ",e);
        }
    }
}
