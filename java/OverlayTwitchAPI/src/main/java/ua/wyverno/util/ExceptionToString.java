package ua.wyverno.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionToString {

    public static String getString(Exception e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
