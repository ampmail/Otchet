package jdbc;

import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;

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
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            //ВНИМАНИЕ! не перепутать Long и Integer
            case 0: return String.class;
            case 1: return BigDecimal.class;
            case 2: return BigDecimal.class;
            case 3: return String.class;
            case 4: return String.class;
            case 5: return String.class;
            case 6: return String.class;
            case 7: return BigDecimal.class;
        }
        return null;
//        res[0] = SuplIDName;
//        res[1] = SuplIDServiceQty;
//        res[2] = SuplIDServiceVolume.setScale(2, RoundingMode.FLOOR);
//        res[3] = new SimpleDateFormat("dd.MM.yyyy").format( LastCheckDate);
//        res[4] = new SimpleDateFormat("dd.MM.yyyy").format( LastServiceShipmentDate );
//        res[5] = new SimpleDateFormat("dd.MM.yyyy").format( LastServiceReceipDate);
//        res[6] = new SimpleDateFormat("dd.MM.yyyy").format( TheOldestServicePosition);
//        res[7] = ReadyForShipmentQty;
    }
}