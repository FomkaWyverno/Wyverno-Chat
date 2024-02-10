package ua.wyverno.files.hashs;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class FileHashTest {

    private static final Logger logger = LoggerFactory.getLogger(FileHashTest.class);

    static FileMetadataNode src;
    static FileMetadataNode main;
    static FileMetadataNode java;
    static FileMetadataNode example;

    @BeforeAll
    static void init() {
        FileMetadataNode src = new FileMetadataNode("src", false);
        FileMetadataNode main = new FileMetadataNode(src,"main", false);
        FileMetadataNode java = new FileMetadataNode(main,"java", false);
        FileMetadataNode example = new FileMetadataNode(java,"example",false);

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