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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asenf
 */
public class Md5OneFileThread extends Thread {
    
    public int index; // Thread-specific information
    
    private final double barMax = 1000.0;
    
    private final File input_file;
    private final FileInputStream fis;
    private final long size;
    private final int idx;
    private final int click;
    
    private MyTableModel mm;
    private MessageDigest md = null;

    public Md5OneFileThread(String path, 
                            long size, 
                            int index, 
                            MyTableModel mm, 
                            int thread_idx) throws NoSuchAlgorithmException, FileNotFoundException {
        this.input_file = new File(path);
        this.fis = new FileInputStream(this.input_file);
        this.size = size;
        this.mm = mm;
        this.idx = index;

        // Calculate increments to the bar
        if (this.size > barMax)
            this.click = (int) Math.ceil(this.size/barMax);
        else
            this.click = (int) Math.ceil(barMax/this.size);
        
        this.index = thread_idx;
        this.md = MessageDigest.getInstance("MD5");
    }
    
    @Override
    public void run() {
        // Initialize Progress Bar to 0%
        this.mm.setValueAt("0%", idx, 5);
        
        // Output File for the MD5 hash
        String out_path = this.input_file.getAbsolutePath() + ".md5";
        PrintWriter pw = null;
        try {
             pw = new PrintWriter(new File(out_path));
        
            // MD5 calculating stream
            DigestInputStream dis_in = new DigestInputStream(this.fis, md);        

            // Simply read file - this calculated the MD5. Update Bars during this process
            // Two-step reduction: FIlesize->val[0,1000] and val->[0%,100%]
            byte[] buffer = new byte[1024*1024];
            int read = 0, read_temp = 0, increment = 0, current = 0;
            while ( (read = dis_in.read(buffer)) > -1 ) {
                read_temp += read;
                if (read_temp > this.click) {
                    // Increment Bar - translate 1000 to 0-90%
                    increment += read_temp / this.click; // int division
                    while (increment >= 11) {
                        current = (current < 91)? current+1:current; // Limit 10 100%
                        increment -= 11;
                        String s_pct = String.valueOf(current) + "%";
                        mm.setValueAt(s_pct, idx, 5);
                    }
                    
                    read_temp = read_temp % this.click; 
                }
            }

            // Done - file completely read, so close it
            dis_in.close();
            
            // File Read - Write MD5 File
            byte[] digest = md.digest();
            BigInteger bigInt = new BigInteger(1,digest);
            String hashtext = bigInt.toString(16);
            while(hashtext.length() < 32 ){
              hashtext = "0"+hashtext;
            }
            mm.setValueAt(hashtext, idx, 4);
            pw.write(hashtext);
            pw.close();

            // Complete - set progress bar to 100% to finish out things (last 10%)
            this.mm.setValueAt("100%", idx, 5);
        } catch (IOException ex) {
            Logger.getLogger(Md5OneFileThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public long get_size() {
        return this.size;
    }
}
