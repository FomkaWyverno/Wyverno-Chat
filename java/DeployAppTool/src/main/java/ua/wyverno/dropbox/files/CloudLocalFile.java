package ua.wyverno.dropbox.files;

import java.nio.file.Path;

/**
 * Class needed for information storage about Local File and Cloud File path
 */
public record CloudLocalFile(Path localFile, Path cloudFile) {}
