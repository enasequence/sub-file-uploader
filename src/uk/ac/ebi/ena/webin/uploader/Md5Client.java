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

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author asenf
 */
public class Md5Client implements Runnable {
    private static final int numThreads = 10;
    
    javax.swing.JProgressBar the_bar;
    String[] the_files;
    long[] the_sizes;
    int[] the_indeces;
    WebinFileUploader t;
    
    private final long total_size;    
    private final boolean overwrite;
    
    public Md5Client(javax.swing.JProgressBar new_bar, String[] new_files, long[] the_sizes, int[] indices, WebinFileUploader t, String pth) {
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
        
        // Calculate size of all files - for progress bar
        long tot = 0;
        for (int i=0; i<this.the_sizes.length; i++)
            tot += this.the_sizes[i];
        this.total_size = tot;
        this.overwrite = this.t.isOverwrite();
    }
    
    @Override
    public void run() {
        if (t != null) t.deactivate(); // deactivate GUI elements during upload
        long processed_size = 0;
        this.the_bar.setValue(0); // Initialize

        // Generate MD5 files in Parallel - numThreads parallel Threads
        Md5OneFileThread my_threads[] = new Md5OneFileThread[Md5Client.numThreads];
        try {
            int[] thread_index = new int[Md5Client.numThreads];
            int count = 0, savecount = 0, size = this.the_files.length;
            boolean alive = true;
            do {
                ArrayList indices = new ArrayList(); // indices of "free" threads
                alive = false;
                for (int i=0; i<numThreads; i++) { // find threads that have ended
                    if ( (my_threads[i] != null) && (my_threads[i].isAlive()) ) {
                        alive = true;
                    } else if (my_threads[i] != null) { // active thread has ended - update overall progress bar
                        
                        // Update the Bar (increments of 0-1000)
                        processed_size += my_threads[i].get_size();
                        int val = (int) (((processed_size*1.0)/(this.total_size*1.0)) * 1000);
                        this.the_bar.setValue(val);

                        indices.add(i);
                        if (my_threads[i] != null) {
                            my_threads[i] = null;
                            savecount++; // count completed threads
                        }
                        
                        this.t.setStatusText("Completed File " + savecount + "/" + this.the_files.length);
                    } else { // initial condition: no thread started yet
                        indices.add(i);
                    }
                }

                // Previous loop determined free threads; fill them in the next loop
                if (indices.size() > 0 && count < size) { // If there are open threads, then
                    for (int i=0; i<indices.size(); i++) { // Fill all open spaces
                        if (count < size) { // Catch errors
                            if (!this.overwrite) {
                                File f = new File(this.the_files[count] + ".md5");
                                if (f.exists() && f.length()==32) {
                                    this.t.getTable().setValueAt("File Exists", this.the_indeces[count], 4);
                                    count++;
                                    savecount++;
                                    processed_size += (new File(this.the_files[count])).length();
                                    int val = (int) (((processed_size*1.0)/(this.total_size*1.0)) * 1000);
                                    this.the_bar.setValue(val);
                                    continue;
                                }
                            }
                            
                            int index = Integer.parseInt(indices.get(i).toString());
                            // Start an MD5 Thread - which will MD5 and update the form data (progress bars)
                            my_threads[index] = new Md5OneFileThread(this.the_files[count],
                                                                     this.the_sizes[count], 
                                                                     this.the_indeces[count],
                                                                     this.t.getTable(), 
                                                                     index);
                                
                            my_threads[index].start(); // start the MD5 algorithm (thread)
                            this.t.setStatusText("MD5 Calculation Process started. Please wait.");
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
        
        this.t.setStatusText("MD5 Calculation Completed.");
        this.t.setButtonText("Only Create MD5 Files", 1);
        if (t != null) t.activate(); // Re-activate elements on the applet form
    }
}
