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
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TimeZone;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author Alexander Senf
 */
public class WebinFileUploader extends javax.swing.JApplet {
    private final String version = "0.9.5";
    
    private String the_addr = "webin.ebi.ac.uk";
    private int the_port = 21;
    
    private MyTableModel the_model = null;
    private FTPClient the_client = null;

    private ArrayList name = new ArrayList();
    private ArrayList size = new ArrayList();
    private ArrayList date = new ArrayList();
    
    private TransferClient tr = null;
    private Thread tr_t = null;
    private Md5Client m5 = null;
    private Thread m5_t = null;

    public static void main(String [ ] args) {
        WebinFileUploader downloader = new WebinFileUploader();
        downloader.init();
        downloader.start();
    }

    /**
     * Initializes the applet WebinFileUploader
     */
    @Override
    public void init() {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(WebinFileUploader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(WebinFileUploader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(WebinFileUploader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(WebinFileUploader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the applet */
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    initComponents();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        /* More Initialization */
        this.jTable1.setModel(new MyTableModel());
        this.the_model = (MyTableModel)this.jTable1.getModel(); // prepare, initialize table model
        this.jTable1.getColumnModel().getColumn(5).setCellRenderer(new ProgressCellRenderer());
        this.the_client = new FTPClient();
        this.jLabel4.setText("");
        
        /* Read Parameters, if there are any: server, Port, Buttons */
        String serv = getParameter("server");
        if (serv!=null && serv.length()>0) this.the_addr = serv.trim();
        String por = getParameter("port");
        if (por!=null && por.length()>0) {
            try {
                int iPor = Integer.parseInt(por.trim());
                this.the_port = iPor;
            } catch (Throwable t) {
                ;
            }
        }
        String mode = getParameter("mode");
        if (mode!=null && mode.length()>0) {
            if (mode.trim().equalsIgnoreCase("upload")) {
                this.jButton4.setVisible(false);
            } else if (mode.trim().equalsIgnoreCase("md5")) {
                this.jLabel1.setVisible(false);
                this.jTextField1.setVisible(false);
                this.jLabel2.setVisible(false);
                this.jPasswordField1.setVisible(false);
                this.jButton3.setVisible(false);
            }
        }

        String user = getParameter("user");
        if (null != user)
            user.trim();

        if (mode!=null && mode.length()>0) {
            this.jTextField1.setText(user);
        }
        
        // Hack for old server
        this.jCheckBox1.setSelected(true);
        if (this.the_port==8021) {
            this.jCheckBox1.setVisible(false);
        }
        
        this.jLabel4.setText("Applet Version " + this.version);
    }

    /**
     * This method is called from within the init() method to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jProgressBar1 = new javax.swing.JProgressBar();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        jLabel1.setText("Username");

        jLabel2.setText("Password");

        jLabel3.setText("Upload Directory");

        jButton1.setText("...");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jButton2.setText("Select All");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });

        jCheckBox1.setText("Overwrite");

        jCheckBox2.setText("Upload Tree");
        jCheckBox2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCheckBox2MouseClicked(evt);
            }
        });

        jButton3.setBackground(java.awt.Color.orange);
        jButton3.setText("Upload");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton3MouseClicked(evt);
            }
        });

        jButton4.setText("Only Create MD5 Files");
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton4MouseClicked(evt);
            }
        });

        jLabel4.setText("Status");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox2)
                        .addGap(2, 2, 2))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jCheckBox1)
                    .addComponent(jCheckBox2)
                    .addComponent(jLabel4))
                .addGap(5, 5, 5)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked

        final JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnVal = fc.showOpenDialog(new JFrame());
        File sel = fc.getSelectedFile();
        if (sel == null) return;
        try {
            this.jTextField2.setFocusable(false);
            this.jTextField2.setText(sel.getAbsolutePath());
            this.jTextField2.setFocusable(true);

            dirChanged();
            this.jButton3.requestFocus();
        } catch (Throwable t) {
            System.out.println("Error jButton1Click: " + t.toString());
        }
    }//GEN-LAST:event_jButton1MouseClicked

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // Size Display
        ArrayList selected = new ArrayList();
        for (int i = 0; i < this.jTable1.getRowCount(); i++) {
            if (Boolean.valueOf(the_model.getValueAt(i, 0).toString()) == true) {
                try {
                    selected.add(this.size.get(i).toString());
                } catch (Throwable th) {
                    selected.add("0");
                }
            }
        }

        // Select button
        if (selected.size() == this.jTable1.getRowCount()) {
            this.jButton2.setText("Select None");
        } else {
            this.jButton2.setText("Select All");
        }

        int[] num = new int[selected.size()];
        long totsize = 0;
        for (int i = 0; i < num.length; i++) {
            totsize += Long.parseLong(selected.get(i).toString());
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        // Select All
        boolean select = this.jButton2.getText().contains("Select All");
        for (int i = 0; i < this.jTable1.getRowCount(); i++) { // Prevent 'double' decryption
            String dl = the_model.getValueAt(i, 4).toString();
            if (select) {
                this.the_model.setValueAt(true, i, 0);
            } else {
                this.the_model.setValueAt(false, i, 0);
            }
        }
        if (select) {
            this.jButton2.setText("Select None");
        } else {
            this.jButton2.setText("Select All");
        }

        // Size Display
        ArrayList selected = new ArrayList();
        for (int i = 0; i < this.jTable1.getRowCount(); i++) {
            if (Boolean.valueOf(the_model.getValueAt(i, 0).toString()) == true) {
                selected.add(this.size.get(i).toString());
            }
        }

        int[] num = new int[selected.size()];
        long totsize = 0;
        for (int i = 0; i < num.length; i++) {
            totsize += Long.parseLong(selected.get(i).toString());
        }
    }//GEN-LAST:event_jButton2MouseClicked

    private void jButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseClicked
        // Handle 'cancel' mode:
        if (this.jButton3.getText().equalsIgnoreCase("Cancel Upload")) {
            this.tr_t.stop();
            this.tr_t = null;
            this.jButton3.setText("Upload");
            return;
        }
        
       this.jLabel3.setText("Upload Selected");        
        
        // Upload Code
        if (this.jTextField1.getText().length() == 0 || this.jPasswordField1.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Provide Username and Password!", "User Credentials Missing", JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.jLabel4.setText("Upload Selected");

        boolean login = false;
        try {
            this.the_client.connect(the_addr, the_port);
            login = this.the_client.login(this.jTextField1.getText(), new String(this.jPasswordField1.getPassword()));
        } catch (IOException ex) {
            Logger.getLogger(WebinFileUploader.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!login) {
            JOptionPane.showMessageDialog(this, "Login Incorrect!", "User Credentials Incorrect", JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.the_client.enterLocalPassiveMode();
        try {
            this.the_client.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (IOException ex) {
            Logger.getLogger(WebinFileUploader.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Get files to be uploaded (this with check box)
        String path = this.jTextField2.getText();
        File f = new File(path);
        try {
            path = f.getCanonicalPath();
            path = path.substring(0, path.length());
        } catch (IOException ex) {
            Logger.getLogger(WebinFileUploader.class.getName()).log(Level.SEVERE, null, ex);
        }

        ArrayList selectedForUpload = new ArrayList(), dlSizes = new ArrayList(), sIn = new ArrayList();
        for (int i = 0; i < the_model.getRowCount(); i++) {
            if (Boolean.valueOf(the_model.getValueAt(i, 0).toString()) == true) { // If selected ...
                String name = this.name.get(i).toString();
                if (this.jCheckBox2.isSelected()) {
                    name = name.substring(name.indexOf(File.separator) + 1);
                }
                selectedForUpload.add(path + File.separator + name); //this.name.get(i).toString()); // Entire Path!
                dlSizes.add(this.size.get(i).toString()); // File Size of selected file
                sIn.add(String.valueOf(i)); // Index of selected file
            }
        }
        if (selectedForUpload.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select at least 1 file for upload.", "Entry Missing", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Upload each file, by spawning a transfer client
        String[] to_download = new String[selectedForUpload.size()];
        long[] dl_size = new long[dlSizes.size()];
        int[] selectedIndices = new int[sIn.size()];
        for (int i = 0; i < selectedForUpload.size(); i++) { // download each file individually
            to_download[i] = selectedForUpload.get(i).toString();
            dl_size[i] = Long.parseLong(dlSizes.get(i).toString());
            selectedIndices[i] = Integer.parseInt(sIn.get(i).toString());
        }

        // Now that upload is about to commence, display overall progress bar
        this.jProgressBar1.setVisible(true);
        this.jLabel1.setVisible(true);

        // Hand off upload information to Transfer Client thread
        this.tr = new TransferClient(this.jProgressBar1, to_download, dl_size, selectedIndices, this, path, this.the_client);

        // And run...
        this.tr_t = new Thread(this.tr);
        this.tr_t.start();
        this.jLabel4.setText("Upload Process Started.");

        // Add 'Cancel' Option
        this.jButton3.setText("Cancel Upload");
        this.jButton3.setEnabled(true);
    }//GEN-LAST:event_jButton3MouseClicked

    private void jButton4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseClicked
         // Handle 'cancel' mode:
        if (this.jButton4.getText().equalsIgnoreCase("Cancel Calculation")) {
            this.m5_t.stop();
            this.m5_t = null;
            this.jButton4.setText("Only Create MD5 Files");
            return;
        }
        
       this.jLabel4.setText("MD5 Calculation Selected");
        
        // Create obly MD5 files (e.g. to upload via Aspera later)
        // Get files to be uploaded (this with check box)
        String path = this.jTextField2.getText();
        File f = new File(path);
        try {
            path = f.getCanonicalPath();
            path = path.substring(0, path.length());
        } catch (IOException ex) {
            Logger.getLogger(WebinFileUploader.class.getName()).log(Level.SEVERE, null, ex);
        }

        ArrayList selectedForUpload = new ArrayList(), dlSizes = new ArrayList(), sIn = new ArrayList();
        for (int i = 0; i < the_model.getRowCount(); i++) {
            if (Boolean.valueOf(the_model.getValueAt(i, 0).toString()) == true) { // If selected ...
                String name = this.name.get(i).toString();
                if (this.jCheckBox2.isSelected()) {
                    name = name.substring(name.indexOf(File.separator) + 1);
                }
                selectedForUpload.add(path + File.separator + name); // Entire Path!
                dlSizes.add(this.size.get(i).toString()); // File Size of selected file
                sIn.add(String.valueOf(i)); // Index of selected file
            }
        }
        if (selectedForUpload.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select at least 1 file to generate MD5 files.", "Entry Missing", JOptionPane.ERROR_MESSAGE);
            this.jLabel4.setText("");
            return;
        }

        // Upload each file, by spawning a transfer client
        String[] to_download = new String[selectedForUpload.size()];
        long[] dl_size = new long[dlSizes.size()];
        int[] selectedIndices = new int[sIn.size()];
        for (int i = 0; i < selectedForUpload.size(); i++) { // download each file individually
            to_download[i] = selectedForUpload.get(i).toString();
            dl_size[i] = Long.parseLong(dlSizes.get(i).toString());
            selectedIndices[i] = Integer.parseInt(sIn.get(i).toString());
        }

        // Now that upload is about to commence, display overall progress bar
        this.jProgressBar1.setVisible(true);

        // Hand off upload information to Transfer Client thread
        this.m5 = new Md5Client(this.jProgressBar1, to_download, dl_size, selectedIndices, this, path);

        // And run...
        this.m5_t = new Thread(this.m5);
        this.m5_t.start();
        this.jLabel4.setText("MD5 Calculation Process Started.");
        
        // Add 'Cancel' Option
        this.jButton4.setText("Cancel Calculation");
        this.jButton4.setEnabled(true);
    }//GEN-LAST:event_jButton4MouseClicked

    private void jCheckBox2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckBox2MouseClicked
        if (this.jTextField2.getText().length() > 0) {
            dirChanged();
        }
    }//GEN-LAST:event_jCheckBox2MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables

    private void dirChanged() { // Update table content upon directory change
        
        boolean dirs = this.jCheckBox2.isSelected(); // Upload dir structure -- display all subfolders + partial path
        
        String dir_text = this.jTextField2.getText();
        this.name = new ArrayList(); this.size = new ArrayList(); this.date = new ArrayList();
        if (dir_text.length() <= 0) {
            return; // No dir selected
        }
        
        File dir = new File(dir_text);
        if (!dir.exists()) {
            Object[][] the_data = {{false, "", "", "", "", ""}};
            this.the_model.setData(the_data);
            JOptionPane.showMessageDialog(this, dir_text + " does not exist.", "File Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //File[] allfiles = dir.listFiles();
        FilenameFilter ff = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
               if(name.lastIndexOf('.')>0)
               {
                  // get last index for '.' char
                  int lastIndex = name.lastIndexOf('.');
                  // get extension
                  String str = name.substring(lastIndex);
                  // match path name extension
                  if(!str.equals(".md5") && !str.endsWith("~"))
                  {
                     return true;
                  }
               } else
                   return true;
               return false;
            }
        };        File[] allfiles = listFilesAsArray(dir, ff, dirs);
        if (allfiles.length == 0) {
            Object[][] the_data = {{false, "", "", "", "", ""}};
            this.the_model.setData(the_data);
            JOptionPane.showMessageDialog(this, dir_text + " is empty.", "File Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String loc_path = dir.getAbsolutePath();
        int idx = loc_path.lastIndexOf(File.separatorChar);
        if (idx >= 0)
            loc_path = loc_path.substring(idx+1);
        for (int i=0; i<allfiles.length; i++) {
            String tmp_name = "";
            if (dirs) { // Display last dir, plus sub-dirs
                tmp_name = allfiles[i].getAbsolutePath();
                idx = tmp_name.indexOf(loc_path);
                tmp_name = tmp_name.substring(idx);
            } else { // Display just file name
                tmp_name = allfiles[i].getName();
            }

            if (!allfiles[i].isDirectory()) { // don't actually add plain directories to the list
                this.name.add(tmp_name);
                this.size.add(String.valueOf(allfiles[i].length()));
                this.date.add(String.valueOf(allfiles[i].lastModified()));
            }
        }
        
        // List files in table
        DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        df.setTimeZone(TimeZone.getTimeZone("Europe/London"));
        Object[][] the_data = new Object[this.name.size()][6];
        for (int i=0; i<this.name.size(); i++) { // val, row, col
            the_data[i][0] = false;
            the_data[i][1] = this.name.get(i).toString();
            the_data[i][2] = size_display(Long.parseLong(this.size.get(i).toString()));
            the_data[i][3] = df.format(Long.parseLong(this.date.get(i).toString()));
            the_data[i][4] = "";
            the_data[i][5] = "0%";
        }

        this.the_model.setData(the_data);
        
        this.jButton2.setText("Select All");
    }

    public File[] listFilesAsArray(File directory, FilenameFilter filter, boolean recurse) {
	Collection<File> files = listFiles(directory, filter, recurse);

	File[] arr = new File[files.size()];
	return files.toArray(arr);
    }

    public Collection<File> listFiles(File directory, FilenameFilter filter, boolean recurse) {
	// List of files / directories
	Vector<File> files = new Vector<File>();
	
	// Get files / directories in the directory
	File[] entries = directory.listFiles();
	
	// Go over entries
	for (File entry : entries)
	{
		if (filter == null || filter.accept(directory, entry.getName()))
		{
                        if (!entry.isHidden())
                            files.add(entry);
		}
		
		if (recurse && entry.isDirectory() && !entry.isHidden())
		{
			files.addAll(listFiles(entry, filter, recurse));
		}
	}
	
	// Return collection of files
	return files;		
    }
    
    private String size_display(long in) {
        String result = "";
        DecimalFormat df = new DecimalFormat("#,##0.00");
        
        double in_format = 0;
        if (in < 1024) {
            result = in + " Bytes";
        } else if (in < Math.pow(1024, 2)) {
            in_format = (in/Math.pow(1024, 1));
            result = df.format(in_format) + " KB";
        } else if (in < Math.pow(1024, 3)) {
            in_format = (in/Math.pow(1024, 2));
            result = df.format(in_format) + " MB";
        } else if (in < Math.pow(1024, 4)) {
            in_format = (in/Math.pow(1024, 3));
            result = df.format(in_format) + " GB";
        } else if (in < Math.pow(1024, 5)) {
            in_format = (in/Math.pow(1024, 4));
            result = df.format(in_format) + " TB";
        } else if (in < Math.pow(1024, 6)) {
            in_format = (in/Math.pow(1024, 5));
            result = df.format(in_format) + " PB";
        }
        
        return result;
    }
    
    public void deactivate() {
        this.jLabel1.setEnabled(false);
        this.jTextField1.setEnabled(false);
        this.jLabel2.setEnabled(false);
        this.jPasswordField1.setEnabled(false);
        this.jLabel3.setEnabled(false);
        this.jTextField2.setEnabled(false);
        this.jButton1.setEnabled(false);
        this.jTable1.setEnabled(false);
        this.jButton2.setEnabled(false);
        this.jLabel4.setEnabled(false);
        this.jCheckBox1.setEnabled(false);
        this.jCheckBox2.setEnabled(false);
        if (!this.jButton3.getText().contains("Cancel")) this.jButton3.setEnabled(false);
        if (!this.jButton4.getText().contains("Cancel")) this.jButton4.setEnabled(false);
    }
    public void activate() {
        this.jLabel1.setEnabled(true);
        this.jTextField1.setEnabled(true);
        this.jLabel2.setEnabled(true);
        this.jPasswordField1.setEnabled(true);
        this.jLabel3.setEnabled(true);
        this.jTextField2.setEnabled(true);
        this.jButton1.setEnabled(true);
        this.jTable1.setEnabled(true);
        this.jButton2.setEnabled(true);
        this.jLabel4.setEnabled(true);
        this.jCheckBox1.setEnabled(true);
        this.jCheckBox2.setEnabled(true);
        this.jButton3.setEnabled(true);
        this.jButton4.setEnabled(true);
    }
    
    public boolean isTree() {
        return this.jCheckBox2.isSelected();
    }
    
    public boolean isOverwrite() {
        return this.jCheckBox1.isSelected();
    }
    
    public void setButtonText(String text, int idx) {
        if (idx == 0)
            this.jButton3.setText(text);
        else if (idx == 1)
            this.jButton4.setText(text);
    }
    
    public MyTableModel getTable() {
        return this.the_model;
    }
    
    public void setStatusText(String text) {
        this.jLabel4.setText(text);
    }
    
    public String getU() {
        return this.jTextField1.getText();
    }
    
    public char[] getP() {
        return this.jPasswordField1.getPassword();
    }
}
