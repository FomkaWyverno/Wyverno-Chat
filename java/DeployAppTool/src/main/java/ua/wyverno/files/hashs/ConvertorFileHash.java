package ua.wyverno.files.hashs;

public interface ConvertorFileHash {
    /**
     * Merge with other FileHash object
     * @param root point join where method must be find place in Tree FileHash<br/>
     *             Example: FileHash - path: ./master/src/example1<br/>
     *             This interface has path: src/example2<br/>
     *             If root param is ./master<br/>
     *             FileHash (path:"./") must be look as tree with children nodes: ./master/src/example1 and ./master/src/example2
     * @return FileHash with the current path, but already merged with the root path of the argument
     */
    FileHash mergeWithOtherFileHash(FileHash root);

    /**
     * Convert to FileHash <br/>
     *      Example:<br/>
     *      Path: src/java/example<br/>
     *      Should convert to FileHash with path: "src" and have child<br/>
     *      FileHash with path: java and have child<br/>
     *      FileHash with path: example<br/>
     * @return FileHash with the full path to the file, if we take an example, it should return the path to the file "example"
     */
    FileHash toFileHash();
}
