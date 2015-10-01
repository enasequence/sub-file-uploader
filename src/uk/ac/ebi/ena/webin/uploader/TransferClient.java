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

/**
 *
 * @author asenf
 */
public class TransferClient implements Runnable {
    private static final int numThreads = 1;
    
    javax.swing.JProgressBar the_bar;
    String[] the_files;
    long[] the_sizes;
    int[] the_indeces;
    WebinFileUploader t;
    String path;
    FTPClient client;
    
    private final long total_size;    
    private final boolean overwrite, tree;
    
    public TransferClient(javax.swing.JProgressBar new_bar, 
                          String[] new_files, 
                          long[] the_sizes, 
                          int[] indices, 
                          WebinFileUploader t,
                          String pth,
                          FTPClient client) {
        this.the_bar = new_bar; // Overall Progress Bar of the Applet
        this.the_bar.setMinimum(0);
        this.the_bar.setMaximum(1000);
        this.the_files = new String[new_files.length]; // Filenames selected for Upload
        System.arraycopy(new_files, 0, this.the_files, 0, new_files.length);
        this.the_sizes = new long[the_sizes.length]; // Filenames selected for Upload
        System.arraycopy(the_sizes, 0, this.the_sizes, 0, the_sizes.length);
        this.the_indeces = new int[indices.length]; // Indices in the Applet table
        System.arraycopy(indices, 0, this.the_indeces, 0, indices.length);
        this.t = t; // The Applet itself
        this.path = pth.replace("\\", "/");
        this.client = client;
        
        // Calculate size of all files - for progress bar
        long tot = 0;
        for (int i=0; i<this.the_sizes.length; i++)
            tot += this.the_sizes[i];
        this.total_size = tot;
        this.overwrite = this.t.isOverwrite();
        this.tree = this.t.isTree();
    }
    
    @Override
    public void run() {
        if (t != null) t.deactivate(); // deactivate GUI elements during upload
        long processed_size = 0;
        this.the_bar.setValue(0); // Initialize

        // Generate MD5 files in Parallel - numThreads parallel Threads
        TransferOneFileThread my_threads[] = new TransferOneFileThread[TransferClient.numThreads];
        try {
            int[] thread_index = new int[TransferClient.numThreads];
            int count = 0, savecount = 0, size = this.the_files.length;
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
                        if (MD5Path.endsWith("/")) MD5Path = MD5Path.substring(0, MD5Path.length()-1);
                        if (MD5Path.contains("/")) MD5Path = MD5Path.substring(MD5Path.lastIndexOf("/")+1);
                        this.client.storeFile(MD5Path, new FileInputStream(my_threads[i].getMD5Path()));
                        
                        // Update the Bar (increments of 0-1000)
                        processed_size += my_threads[i].get_size();
                        int val = (int) (((processed_size*1.0)/(this.total_size*1.0)) * 1000);
                        this.the_bar.setValue(val);
                        
                        indices.add(i);
                        if (my_threads[i] != null) {
                            my_threads[i] = null;
                            savecount++; // count completed threads
                        }
                        this.client.changeWorkingDirectory("/");
                        
                        this.t.setStatusText("Completed File " + savecount + "/" + this.the_files.length);
                    } else { // initial condition: no thread started yet
                        indices.add(i);                        
                    }
                }

                // Previous loop determined free threads; fill them in the next loop
                if (indices.size() > 0 && count < size) { // If there are open threads, then
                    for (int i=0; i<indices.size(); i++) { // Fill all open spaces
                        if (count < size) { // Catch errors
                            int tree = -1;
                            if (this.tree) {
                                tree = this.path.lastIndexOf("/");
                            }
                            
                            if (!this.overwrite) {
                                String filename = this.the_files[count].replace("\\", "/");
                                if (tree > 0)
                                    filename = filename.substring(tree);
                                else
                                    if (filename.contains("/")) filename = filename.substring(filename.lastIndexOf("/")+1);
                                FTPFile[] listFiles = this.client.listFiles(filename);
                                if (listFiles!=null && listFiles.length>0) {
                                    this.t.getTable().setValueAt("File Exists", this.the_indeces[count], 4);
                                    count++;
                                    savecount++;
                                    processed_size += listFiles[0].getSize();
                                    int val = (int) (((processed_size*1.0)/(this.total_size*1.0)) * 1000);
                                    this.the_bar.setValue(val);
                                    continue;
                                }
                            }
                            this.t.getTable().setValueAt("", this.the_indeces[count], 4);
                            
                            
                            int index = Integer.parseInt(indices.get(i).toString());
                            // Start an MD5 Thread - which will MD5 and update the form data (progress bars)
                            my_threads[index] = new TransferOneFileThread(this.the_files[count],
                                                                     this.the_sizes[count], 
                                                                     this.the_indeces[count],
                                                                     this.t.getTable(), 
                                                                     this.client, 
                                                                     tree, 
                                                                     index);
//                            my_threads[index] = new TransferOneFileThickThread(this.the_files[count],
//                                                                     this.the_sizes[count], 
//                                                                     this.the_indeces[count],
//                                                                     this.t.getTable(), 
//                                                                     this.client,
//                                                                     tree, 
//                                                                     this.t.getU(),
//                                                                     new String(this.t.getP()),
//                                                                     index);
                                
                            my_threads[index].start(); // start the MD5 algorithm (thread)
                            this.t.setStatusText("Upload Process started. Please wait.");
                            thread_index[index] = count;
                            count++; // count started threads
                        }
                    }
                }

                // runs until the number of completed threads equals the number of files, and all threads completed (redundant)
            }  while ((savecount < size) || alive);
        } catch (Throwable e) {
            for (int i=0; i<numThreads; i++) {
                if (my_threads[i] != null) {
                    my_threads[i].stop();
                    my_threads[i] = null;
                }
            }
        }
        
        // Done - log out again
        try {
            this.client.logout();
        } catch (IOException ex) {
            Logger.getLogger(TransferClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.t.setStatusText("File Upload Completed.");
        this.t.setButtonText("Upload", 0);
        if (t != null) t.activate(); // Re-activate elements on the applet form
    }
}
