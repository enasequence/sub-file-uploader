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

import java.awt.Component;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class MyTableModel extends AbstractTableModel implements TableModel{
        private int cur;
        private long size;
        private String[] columnNames = {"Upload",
                                        "Name",
                                        "Size",
                                        "Date",
                                        "MD5 Checksum",
                                        "Progress"};
        private Object[][] data = {
                {false, "", "", "", "", ""} 
            };

        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }
        
        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data[rowIndex][columnIndex];
        }
        
        public void setData(Object[][] new_data) {
            data = new Object[new_data.length][6];
            for (int i=0; i<new_data.length; i++) {
                System.arraycopy(new_data[i], 0, data[i], 0, 6);
            }
            fireTableDataChanged();
        }
 
        @Override
        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            //fireTableDataChanged();
            this.fireTableCellUpdated(row, col);
        }
        
        public void setCur(int i) {
            this.cur = i;
        }
        public int getCur() {
            return this.cur;
        }
        public void setSize(long l) {
            this.size = l;
        }
        public long getSize() {
            return this.size;
        }
        
        @Override
        public boolean isCellEditable(int row, int col) {
            if (col == 0 || col == 4) {
                return true;
            } else {
                return false;
            }
        }
};



// *************************************************************************
class ProgressCellRenderer extends JProgressBar implements TableCellRenderer {

      /**
       * Creates a JProgressBar with the range 0,100.
       */
      public ProgressCellRenderer(){
        super(0, 100);
        setValue(0);
        setString("0%");
        setStringPainted(true);
      }

      public Component getTableCellRendererComponent(
                                        JTable table,
                                        Object value,
                                        boolean isSelected,
                                        boolean hasFocus,
                                        int row,
                                        int column) {
          
        //value is a percentage e.g. 95%
        final String sValue = value.toString();
        int index = sValue.indexOf('%');
        if (index != -1) {
              int p = 0;
              try{
                p = Integer.parseInt(sValue.substring(0, index));
              } catch(NumberFormatException e){ ; }
              setValue(p);
              setString(sValue);
        } else {
              setValue(0); // This prevents double progress bar updates
              setString(sValue);
        }
        
        return this;
      }
};
