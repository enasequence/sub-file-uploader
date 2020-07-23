package uk.ac.ebi.ena.webin.uploader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPSClient;

public class FileUploaderWebStart extends JFrame implements FileUploaderI {
    private final static String MODE_UPLOAD = "upload";
    private final static String MODE_MD5 = "md5";
    private String server = "webin2.ebi.ac.uk";
    private int port = 21;
    private String mode = MODE_UPLOAD;
    private MyTableModel myTableModel = null;
    private FTPSClient ftpClient = null;
    private List<String> nameL = new ArrayList();
    private List<String> sizeL = new ArrayList();
    private List<String> dateL = new ArrayList();
    private TransferClient transferClient = null;
    private Thread trThread;
    private Thread md5Thread;
    private JButton jButton1;
    private JButton jButton2;
    private JButton jButtonUpload;
    private JCheckBox jCheckBoxOverwrite;
    private JCheckBox jCheckBoxUploadTree;
    private JLabel jLabelUsername;
    private JLabel jLabelPassword;
    private JLabel jLabelUploadDir;
    private JLabel jLabelStatus;
    private JPasswordField jPasswordField1;
    private JProgressBar jProgressBar;
    private JScrollPane jScrollPane1;
    private JTable jTable1;
    private JTextField jTextField1;
    private JTextField jTextField2;

    public FileUploaderWebStart() {
        init();
    }

    public FileUploaderWebStart(String server, String port, String mode) {
        this.server = server;
        this.port = Integer.valueOf(port);
        this.mode = mode;
        init();
    }

    public static void main(String... args) {
        FileUploaderWebStart ex = null;
        if (args.length == 3)
            ex = new FileUploaderWebStart(args[0], args[1], args[2]);
        else
            ex = new FileUploaderWebStart();
        ex.setVisible(true);
    }

    public void init() {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            for (UIManager.LookAndFeelInfo info: UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            Logger.getLogger(FileUploaderApplet.class.getName()).log(Level.SEVERE, null, e);
        }
        try {
            setTitle("Upload File(s)");
            setSize(1000, 600);
            initComponents();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        jTable1.setModel(new MyTableModel());
        myTableModel = (MyTableModel)jTable1.getModel(); // prepare, initialize table model
        jTable1.getColumnModel().getColumn(5).setCellRenderer(new ProgressCellRenderer());
        ftpClient = new FTPSClient();
        jLabelStatus.setText("");
        jCheckBoxOverwrite.setSelected(true);
        if (port == 8021)
            jCheckBoxOverwrite.setVisible(false);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabelUsername = new JLabel("Username");
        jTextField1 = new JTextField();
        jLabelPassword = new JLabel("Password");
        jPasswordField1 = new JPasswordField();
        jLabelUploadDir = new JLabel("Upload Directory");
        jTextField2 = new JTextField();
        jButton1 = new JButton();
        jScrollPane1 = new JScrollPane();
        jTable1 = new JTable();
        jButton2 = new JButton();
        jCheckBoxOverwrite = new JCheckBox();
        jCheckBoxUploadTree = new JCheckBox();
        jProgressBar = new JProgressBar();
        jButtonUpload = new JButton();
        jLabelStatus = new JLabel();
        jButton1.setText("...");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });
        jTable1.setModel(new DefaultTableModel(
                new Object[][]{
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null}
                },
                new String[]{
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
        jCheckBoxOverwrite.setText("Overwrite");
        jCheckBoxUploadTree.setText("Upload Tree");
        jCheckBoxUploadTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCheckBox2MouseClicked(evt);
            }
        });
        jButtonUpload.setBackground(java.awt.Color.orange);
        jButtonUpload.setText("Upload");
        jButtonUpload.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton3MouseClicked(evt);
            }
        });
        jLabelStatus.setText("Status");
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(jProgressBar, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jScrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
                                        .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addComponent(jLabelUploadDir)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTextField2)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabelStatus, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jCheckBoxOverwrite)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jCheckBoxUploadTree)
                                                .addGap(2, 2, 2))
                                        .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addComponent(jLabelUsername)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTextField1)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabelPassword)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jPasswordField1, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jButtonUpload, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabelUsername)
                                        .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabelPassword)
                                        .addComponent(jPasswordField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabelUploadDir)
                                        .addComponent(jTextField2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton1))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton2)
                                        .addComponent(jCheckBoxOverwrite)
                                        .addComponent(jCheckBoxUploadTree)
                                        .addComponent(jLabelStatus))
                                .addGap(5, 5, 5)
                                .addComponent(jProgressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButtonUpload))
                                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        final JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showOpenDialog(new JFrame());
        File sel = fc.getSelectedFile();
        if (sel == null)
            return;
        try {
            jTextField2.setFocusable(false);
            jTextField2.setText(sel.getAbsolutePath());
            jTextField2.setFocusable(true);
            dirChanged();
            jButtonUpload.requestFocus();
        } catch (Throwable t) {
            System.out.println("Error jButton1Click: " + t.toString());
        }
    }//GEN-LAST:event_jButton1MouseClicked

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // Size Display
        ArrayList selected = new ArrayList();
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            if (Boolean.valueOf(myTableModel.getValueAt(i, 0).toString()) == true) {
                try {
                    selected.add(sizeL.get(i).toString());
                } catch (Throwable th) {
                    selected.add("0");
                }
            }
        }
        // Select button
        if (selected.size() == jTable1.getRowCount()) {
            jButton2.setText("Select None");
        } else {
            jButton2.setText("Select All");
        }
        int[] num = new int[selected.size()];
        long totsize = 0;
        for (int i = 0; i < num.length; i++)
            totsize += Long.parseLong(selected.get(i).toString());
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        // Select All
        boolean select = jButton2.getText().contains("Select All");
        for (int i = 0; i < jTable1.getRowCount(); i++) { // Prevent 'double' decryption
            String dl = myTableModel.getValueAt(i, 4).toString();
            if (select)
                myTableModel.setValueAt(true, i, 0);
            else
                myTableModel.setValueAt(false, i, 0);
        }
        if (select)
            jButton2.setText("Select None");
        else
            jButton2.setText("Select All");
        // Size Display
        ArrayList selected = new ArrayList();
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            if (Boolean.valueOf(myTableModel.getValueAt(i, 0).toString()) == true)
                selected.add(sizeL.get(i).toString());
        }
        int[] num = new int[selected.size()];
        long totsize = 0;
        for (int i = 0; i < num.length; i++)
            totsize += Long.parseLong(selected.get(i).toString());
    }//GEN-LAST:event_jButton2MouseClicked

    private void jButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseClicked
        // Handle 'cancel' mode:
        if (jButtonUpload.getText().equalsIgnoreCase("Cancel Upload")) {
            trThread.stop();
            trThread = null;
            jButtonUpload.setText("Upload");
            return;
        }
        jLabelUploadDir.setText("Upload Selected");
        // Upload Code
        if (jTextField1.getText().length() == 0 || jPasswordField1.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Provide Username and Password!", "User Credentials Missing", JOptionPane.ERROR_MESSAGE);
            return;
        }
        jLabelStatus.setText("Upload Selected");
        boolean login = false;
        String username = null;
        String pwd = null;
        String msg = new String();
        username = jTextField1.getText();
        pwd = new String(jPasswordField1.getPassword());
        try {
            username = new AuthClient().getWebinAccount(username, pwd);
            if(username != null) {
                ftpClient.setRemoteVerificationEnabled(true);
                ftpClient.connect(server, port);

                login = ftpClient.login(username, pwd);
            }
        } catch (IOException ex) {
            msg = ex.getMessage()+"\n";
            for(StackTraceElement e :ex.getStackTrace()) {
                msg+=e.toString()+"\n";
            }
            Logger.getLogger(FileUploaderApplet.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        if (!login) {
            JOptionPane.showMessageDialog(this, msg+"\n\n<br/><br/>upd1: Login Incorrect!", "User Credentials Incorrect", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ftpClient.enterLocalPassiveMode();
        try {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (IOException ex) {
            Logger.getLogger(FileUploaderApplet.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Get files to be uploaded (this with check box)
        String path = jTextField2.getText();
        File f = new File(path);
        try {
            path = f.getCanonicalPath();
            path = path.substring(0, path.length());
        } catch (IOException ex) {
            Logger.getLogger(FileUploaderApplet.class.getName()).log(Level.SEVERE, null, ex);
        }
        ArrayList selectedForUpload = new ArrayList(), dlSizes = new ArrayList(), sIn = new ArrayList();
        for (int i = 0; i < myTableModel.getRowCount(); i++) {
            if (Boolean.valueOf(myTableModel.getValueAt(i, 0).toString()) == true) { // If selected ...
                String name = nameL.get(i).toString();
                if (jCheckBoxUploadTree.isSelected())
                    name = name.substring(name.indexOf(File.separator) + 1);
                selectedForUpload.add(path + File.separator + name); //nameL.get(i).toString()); // Entire Path!
                dlSizes.add(sizeL.get(i).toString()); // File Size of selected file
                sIn.add(String.valueOf(i)); // Index of selected file
            }
        }
        if (selectedForUpload.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select at least 1 file for upload.", "Entry Missing", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Upload each file, by spawning a transfer ftpClient
        String[] to_download = new String[selectedForUpload.size()];
        long[] dl_size = new long[dlSizes.size()];
        int[] selectedIndices = new int[sIn.size()];
        for (int i = 0; i < selectedForUpload.size(); i++) { // download each file individually
            to_download[i] = selectedForUpload.get(i).toString();
            dl_size[i] = Long.parseLong(dlSizes.get(i).toString());
            selectedIndices[i] = Integer.parseInt(sIn.get(i).toString());
        }
        // Now that upload is about to commence, display overall progress bar
        jProgressBar.setVisible(true);
        jLabelUsername.setVisible(true);
        // Hand off upload information to Transfer Client thread
        transferClient = new TransferClient(to_download, dl_size, selectedIndices, this, path, ftpClient);
        // And run...
        trThread = new Thread(transferClient);
        trThread.start();
        jLabelStatus.setText("Upload Process Started.");
        // Add 'Cancel' Option
        jButtonUpload.setText("Cancel Upload");
        jButtonUpload.setEnabled(true);
    }//GEN-LAST:event_jButton3MouseClicked

    private void jCheckBox2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckBox2MouseClicked
        if (jTextField2.getText().length() > 0)
            dirChanged();
    }//GEN-LAST:event_jCheckBox2MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private void dirChanged() { // Update table content upon directory change
        boolean dirs = jCheckBoxUploadTree.isSelected(); // Upload dir structure -- display all subfolders + partial path
        String dir_text = jTextField2.getText();
        nameL = new ArrayList(); sizeL = new ArrayList(); dateL = new ArrayList();
        if (dir_text.length() <= 0)
            return; // No dir selected
        File dir = new File(dir_text);
        if (!dir.exists()) {
            Object[][] the_data = {{false, "", "", "", "", ""}};
            myTableModel.setData(the_data);
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
                    // match path nameL extension
                    if(!str.equals(".md5") && !str.endsWith("~"))
                        return true;
                } else
                    return true;
                return false;
            }
        };
        File[] allfiles = listFilesAsArray(dir, ff, dirs);
        if (allfiles.length == 0) {
            Object[][] the_data = {{false, "", "", "", "", ""}};
            myTableModel.setData(the_data);
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
            } else // Display just file nameL
                tmp_name = allfiles[i].getName();
            if (!allfiles[i].isDirectory()) { // don'fileUploader actually add plain directories to the list
                nameL.add(tmp_name);
                sizeL.add(String.valueOf(allfiles[i].length()));
                dateL.add(String.valueOf(allfiles[i].lastModified()));
            }
        }
        // List files in table
        DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        df.setTimeZone(TimeZone.getTimeZone("Europe/London"));
        Object[][] the_data = new Object[nameL.size()][6];
        for (int i=0; i<nameL.size(); i++) { // val, row, col
            the_data[i][0] = false;
            the_data[i][1] = nameL.get(i).toString();
            the_data[i][2] = size_display(Long.parseLong(sizeL.get(i).toString()));
            the_data[i][3] = df.format(Long.parseLong(dateL.get(i).toString()));
            the_data[i][4] = "";
            the_data[i][5] = "0%";
        }
        myTableModel.setData(the_data);
        jButton2.setText("Select All");
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
        for (File entry : entries) {
            if (filter == null || filter.accept(directory, entry.getName())) {
                if (!entry.isHidden())
                    files.add(entry);
            }
            if (recurse && entry.isDirectory() && !entry.isHidden())
                files.addAll(listFiles(entry, filter, recurse));
        }
        // Return collection of files
        return files;
    }

    private String size_display(long in) {
        String result = "";
        DecimalFormat df = new DecimalFormat("#,##0.00");
        double in_format = 0;
        if (in < 1024)
            result = in + " Bytes";
        else if (in < Math.pow(1024, 2)) {
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

    @Override
    public void deactivate() {
        jLabelUsername.setEnabled(false);
        jTextField1.setEnabled(false);
        jLabelPassword.setEnabled(false);
        jPasswordField1.setEnabled(false);
        jLabelUploadDir.setEnabled(false);
        jTextField2.setEnabled(false);
        jButton1.setEnabled(false);
        jTable1.setEnabled(false);
        jButton2.setEnabled(false);
        jLabelStatus.setEnabled(false);
        jCheckBoxOverwrite.setEnabled(false);
        jCheckBoxUploadTree.setEnabled(false);
        if (!jButtonUpload.getText().contains("Cancel"))
            jButtonUpload.setEnabled(false);
    }

    @Override
    public void activate() {
        jLabelUsername.setEnabled(true);
        jTextField1.setEnabled(true);
        jLabelPassword.setEnabled(true);
        jPasswordField1.setEnabled(true);
        jLabelUploadDir.setEnabled(true);
        jTextField2.setEnabled(true);
        jButton1.setEnabled(true);
        jTable1.setEnabled(true);
        jButton2.setEnabled(true);
        jLabelStatus.setEnabled(true);
        jCheckBoxOverwrite.setEnabled(true);
        jCheckBoxUploadTree.setEnabled(true);
        jButtonUpload.setEnabled(true);
    }

    @Override
    public boolean isTree() {
        return jCheckBoxUploadTree.isSelected();
    }

    @Override
    public boolean isOverwrite() {
        return jCheckBoxOverwrite.isSelected();
    }

    @Override
    public void setButtonText(String text, int idx) {
        if (idx == 0)
            jButtonUpload.setText(text);
    }

    @Override
    public MyTableModel getTable() {
        return myTableModel;
    }

    @Override
    public void setStatusText(String text) {
        jLabelStatus.setText(text);
    }

    @Override
    public JProgressBar getJProgressBar() {
        return jProgressBar;
    }
}
