package ua.wyverno.files.hashs;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class FileHashTest {

    private static final Logger logger = LoggerFactory.getLogger(FileHashTest.class);

    static FileHash src;
    static FileHash main;
    static FileHash java;
    static FileHash example;

    @BeforeAll
    static void init() {
        FileHash src = new FileHash("src", false);
        FileHash main = new FileHash(src,"main", false);
        FileHash java = new FileHash(main,"java", false);
        FileHash example = new FileHash(java,"example",false);

        src.addChild(main);
        main.addChild(java);
        java.addChild(example);

        FileHashTest.src = src;
        FileHashTest.main = main;
        FileHashTest.java = java;
        FileHashTest.example = example;
    }
    @Test
    void getName() {
        assertEquals("example", example.getName());
    }

    @Test
    void getPath() {
        assertEquals("src/main/java/example", example.getPath());
    }

    @Test
    void getChildrenSrc() {
        assertEquals(1, src.getChildren().size());
        assertEquals(main, src.getChildren().get(0));
    }

    @Test
    void getChildrenMain() {
        assertEquals(1, main.getChildren().size());
        assertEquals(java, main.getChildren().get(0));
    }

    @Test
    void getChildrenJava() {
        assertEquals(1, java.getChildren().size());
        assertEquals(example, java.getChildren().get(0));
    }

    @Test
    void getChildrenExample() {
        assertEquals(0, example.getChildren().size());
    }


    @Test
    void getParent() {
        assertNull(src.getParent());
        assertEquals(src, main.getParent());
        assertEquals(main, java.getParent());
        assertEquals(java, example.getParent());
    }

    @Test
    void toStringTest(){
        logger.info(java.toString());
    }
}