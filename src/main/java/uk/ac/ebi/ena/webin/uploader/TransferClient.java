/*
 * Copyright 2015 EMBL-EBI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package uk.ac.ebi.ena.webin.uploader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import javax.swing.*;

/**
 *
 * @author asenf
 */
public class TransferClient implements Runnable {
    private static final int numThreads = 1;
    private JProgressBar jProgressBar;
    private String[] filesA;
    private long[] sizesA;
    private int[] indicesA;
    private FileUploaderI fileUploader;
    private String path;
    private FTPClient ftpClient;
    private long totalSize;

    public TransferClient(String[] filesA, long[] sizesA, int[] indicesA, FileUploaderI fileUploader, String path, FTPClient ftpClient) {
        jProgressBar = fileUploader.getJProgressBar();
        jProgressBar.setMinimum(0);
        jProgressBar.setMaximum(1000);
        this.filesA = filesA;
        this.sizesA = sizesA;
        this.indicesA = indicesA;
        this.fileUploader = fileUploader;
        this.path = path.replace("\\", "/");
        this.ftpClient = ftpClient;
        // Calculate size of all files - for progress bar
        for (int i=0; i< this.sizesA.length; i++)
            totalSize += this.sizesA[i];
    }
    
    @Override
    public void run() {
        if (fileUploader != null)
            fileUploader.deactivate(); // deactivate GUI elements during upload
        long processed_size = 0;
        jProgressBar.setValue(0); // Initialize
        // Generate MD5 files in Parallel - numThreads parallel Threads
        TransferOneFileThread my_threads[] = new TransferOneFileThread[TransferClient.numThreads];
        try {
            int[] thread_index = new int[TransferClient.numThreads];
            int count = 0, savecount = 0, size = filesA.length;
            boolean alive = true;
            do {
                ArrayList indices = new ArrayList(); // indices of "free" threads
                alive = false;
                for (int i=0; i<numThreads; i++) { // find threads that have ended
                    if ( (my_threads[i] != null) && (my_threads[i].isAlive()) ) {
                        alive = true;
                    } else if (my_threads[i] != null) { // active thread has ended - update overall progress bar
                        // Upload the MD5 File
                        String MD5Path = my_threads[i].getMD5Path();
                        if (MD5Path.contains("\\"))
                            MD5Path = MD5Path.replace("\\", "/");
                        if (MD5Path.endsWith("/"))
                            MD5Path = MD5Path.substring(0, MD5Path.length()-1);
                        if (MD5Path.contains("/"))
                            MD5Path = MD5Path.substring(MD5Path.lastIndexOf("/")+1);
                        ftpClient.storeFile(MD5Path, new FileInputStream(my_threads[i].getMD5Path()));
                        // Update the Bar (increments of 0-1000)
                        processed_size += my_threads[i].get_size();
                        int val = (int) (((processed_size*1.0)/(totalSize *1.0)) * 1000);
                        jProgressBar.setValue(val);
                        indices.add(i);
                        if (my_threads[i] != null) {
                            my_threads[i] = null;
                            savecount++; // count completed threads
                        }
                        ftpClient.changeWorkingDirectory("/");
                        fileUploader.setStatusText("Completed File " + savecount + "/" + filesA.length);
                    } else // initial condition: no thread started yet
                        indices.add(i);                        
                }
                // Previous loop determined free threads; fill them in the next loop
                if (indices.size() > 0 && count < size) { // If there are open threads, then
                    for (int i=0; i<indices.size(); i++) { // Fill all open spaces
                        if (count < size) {
                            int tree = -1;
                            if (fileUploader.isTree())
                                tree = path.lastIndexOf("/");
                            if (!fileUploader.isOverwrite()) {
                                String filename = filesA[count].replace("\\", "/");
                                if (tree > 0)
                                    filename = filename.substring(tree);
                                else
                                    if (filename.contains("/")) filename = filename.substring(filename.lastIndexOf("/")+1);
                                FTPFile[] listFiles = ftpClient.listFiles(filename);
                                if (listFiles!=null && listFiles.length>0) {
                                    fileUploader.getTable().setValueAt("File Exists", indicesA[count], 4);
                                    count++;
                                    savecount++;
                                    processed_size += listFiles[0].getSize();
                                    int val = (int) (((processed_size*1.0)/(totalSize *1.0)) * 1000);
                                    jProgressBar.setValue(val);
                                    continue;
                                }
                            }
                            fileUploader.getTable().setValueAt("", indicesA[count], 4);
                            int index = Integer.parseInt(indices.get(i).toString());
                            my_threads[index] = new TransferOneFileThread(filesA[count], sizesA[count], indicesA[count], fileUploader.getTable(), ftpClient, tree, index);
                            my_threads[index].start(); // start the MD5 algorithm (thread)
                            fileUploader.setStatusText("Upload Process started. Please wait.");
                            thread_index[index] = count;
                            count++;
                        }
                    }
                }
            }  while ((savecount < size) || alive);
        } catch (Throwable e) {
            for (int i=0; i<numThreads; i++) {
                if (my_threads[i] != null) {
                    my_threads[i].stop();
                    my_threads[i] = null;
                }
            }
        }
        try {
            ftpClient.logout();
        } catch (IOException ex) {
            Logger.getLogger(TransferClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        fileUploader.setStatusText("File Upload Completed.");
        fileUploader.setButtonText("Upload", 0);
        if (fileUploader != null)
            fileUploader.activate(); // Re-activate elements on the applet form
    }
}
