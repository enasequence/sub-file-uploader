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

import com.google.common.io.CountingInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author asenf
 */
public class UploadOneFileThread extends Thread {
    
    private CountingInputStream c_in;
    private FTPClient client;
    private final String filename;
    private final boolean binary;
    
    private boolean storeFile;
    
    public UploadOneFileThread(InputStream in, String filename, FTPClient client, boolean binary) {
        this.c_in = new CountingInputStream (in);
        this.filename = filename;
        this.client = client;
        this.binary = binary;
    }
    
    @Override
    public void run() {
        try {
//            if (binary)
                this.client.setFileType(FTP.BINARY_FILE_TYPE);
//            else
//                this.ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
            storeFile = this.client.storeFile(filename, c_in);
            c_in.close();
        } catch (IOException ex) {
            Logger.getLogger(UploadOneFileThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public long getXferVal() {
        return this.c_in.getCount();
    }
    
    public boolean getXferStat() {
        return this.storeFile;
    }
}
