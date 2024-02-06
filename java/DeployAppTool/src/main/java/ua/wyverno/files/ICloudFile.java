package ua.wyverno.files;

public interface ICloudFile {
    boolean isCloud();
    boolean isCloudDirectory();
    boolean isCloudFile();
    void setCloudFile(boolean cloudFile);
    void setCloudDirectory(boolean cloudDirectory);
}
