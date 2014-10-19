package jdbc;

import javax.swing.table.DefaultTableModel;

public class TableModel extends DefaultTableModel{

    private static final long serialVersionUID = 1L;

    public TableModel (String[] fields){
        for(String field : fields) this.addColumn(field);
    }
    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == 0) return false;
        return true;
    };


}