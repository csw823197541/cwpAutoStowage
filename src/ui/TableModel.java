package ui;

import javax.swing.table.DefaultTableModel;

/**
 * Created by csw on 2016/1/19.
 */
public class TableModel extends DefaultTableModel{

    public Class getColumnClass(int column) {
        Class returnValue;
        if ((column >= 0) && (column < getColumnCount())) {
            returnValue = getValueAt(0, column).getClass();
        } else {
            returnValue = Object.class;
        }
        return returnValue;
    }
}
