package uk.ac.ebi.ena.webin.uploader;

import javax.swing.*;

public interface FileUploaderI {
    void activate();
    void deactivate();
    void setStatusText(String text);
    JProgressBar getJProgressBar();
    MyTableModel getTable();
    void setButtonText(String text, int idx);
    boolean isTree();
    boolean isOverwrite();
}
