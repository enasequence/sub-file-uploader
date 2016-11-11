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
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author asenf
 */
public class TransferOneFileThread extends Thread {
    
    public int index; // Thread-specific information
    
    private final double barMax = 1000.0;
    
    private final File input_file;
    private final FileInputStream fis;
    private final long size;
    private final int idx;
    private final int click;
    
    private MyTableModel mm;
    private FTPClient client;
    private int tree;
    private MessageDigest md = null;
    private String MD5Path;
    
    public TransferOneFileThread(String path, 
                            long size, 
                            int index, 
                            MyTableModel mm,
                            FTPClient client,
                            int tree, 
                            int thread_idx) throws NoSuchAlgorithmException, FileNotFoundException {
        this.input_file = new File(path);
        this.fis = new FileInputStream(this.input_file);
        this.size = size;
        this.mm = mm;
        this.client = client;
        this.tree = tree;
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
        this.MD5Path = this.input_file.getAbsolutePath() + ".md5";
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(this.MD5Path));
        
            // MD5 calculating stream
            DigestInputStream dis_in = new DigestInputStream(this.fis, md);

            // Simply read file - this calculated the MD5. Update Bars during this process
            // Two-step reduction: FIlesize->val[0,1000] and val->[0%,100%]
            String filename = this.input_file.getCanonicalPath().replace("\\", "/");
            if (this.tree > -1) { // Build tree on remote server
                boolean changeRootDirectory = this.client.changeWorkingDirectory("/");
                String pth = filename.substring(this.tree+1, filename.lastIndexOf("/"));
                while (pth.length() > 0) {
                    String pth_ = pth.contains("/")?pth.substring(0,pth.indexOf("/")):pth;
                    boolean makeDirectory = this.client.makeDirectory(pth_);
                    boolean changeWorkingDirectory = this.client.changeWorkingDirectory(pth_);
                    if (pth.length() > pth_.length())
                        pth = pth.substring(pth_.length()+1);
                    else
                        pth = "";
                }
            }
            if (filename.contains("/")) filename = filename.substring(filename.lastIndexOf("/")+1);
            UploadOneFileThread uoft = new UploadOneFileThread(dis_in, filename, this.client, isBinaryFile(this.input_file));
            uoft.start();
            
            long read = 0;
            while ( uoft.isAlive() ) {
                read = uoft.getXferVal(); // Current total xferred bytes. in theory
                
                int val = (int) (((read*1.0)/(this.size*1.0)) * 100);
                String s_pct = String.valueOf(val) + "%";
                mm.setValueAt(s_pct, idx, 5);
                
                Thread.sleep(750);
            }

            // Done - file completely read, so close it
            dis_in.close();
            
            // Check Data Xfer
            System.out.println(uoft.getXferVal() + "  " + this.input_file.length() + "   " + uoft.getXferStat() + "   " + isBinaryFile(this.input_file));
            
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
            Logger.getLogger(TransferOneFileThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(TransferOneFileThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public long get_size() {
        return this.size;
    }
    
    public String getMD5Path() {
        return this.MD5Path;
    }

    private boolean isBinaryFile( File f ) throws FileNotFoundException, IOException {
        FileInputStream in = new FileInputStream(f);
        int size = in.available();
        if(size > 1024) size = 1024;
        byte[] data = new byte[size];
        in.read(data);
        in.close();

        int ascii = 0;
        int other = 0;

        for( int i = 0; i < data.length; i++ ) {
            byte b = data[i];
            if( b < 0x09 ) return true;

            if ( b == 0x09 || b == 0x0A || b == 0x0C || b == 0x0D ) 
                ascii++;
            else if( b >= 0x20  &&  b <= 0x7E ) 
                ascii++;
            else 
                other++;
        }

        if( other == 0 ) return false;

        return 100 * other / (ascii + other) > 95;
    }
}
