package jdbc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataHeap{
    private String SuplIDName;
    private int SuplID;
    private Integer SuplIDServiceQty;
    private BigDecimal SuplIDServiceVolume;
    private Date LastCheckDate;
    private Date LastServiceShipmentDate;
    private Date LastServiceReceipDate;
    private Date TheOldestServicePosition;
    private Integer ReadyForShipmentQty;

    DataHeap(int SuplID, String SuplIDName,
             int SuplIDServiceQty,
             BigDecimal SuplIDServiceVolume,
             Date LastCheckDate,
             Date LastServiceShipmentDate,
             Date LastServiceReceipDate,
             Date TheOldestServicePosition,
             Integer ReadyForShipmentQty){
        this.SuplID = SuplID;
        this.SuplIDName = SuplIDName;
        this.SuplIDServiceQty = SuplIDServiceQty;
        this.LastCheckDate = LastCheckDate;
        this.SuplIDServiceVolume = SuplIDServiceVolume;
        this.LastServiceShipmentDate = LastServiceShipmentDate;
        this.LastServiceReceipDate = LastServiceReceipDate;
        this.TheOldestServicePosition = TheOldestServicePosition;
        this.ReadyForShipmentQty = ReadyForShipmentQty;
    }
    public String[] toArray(){
        String [] res = new String [8];
        res[0] = SuplIDName;
        res[1] = SuplIDServiceQty.toString();
        res[2] = SuplIDServiceVolume.setScale(2, RoundingMode.FLOOR).toString();
        res[3] = new SimpleDateFormat("dd.MM.yyyy").format( LastCheckDate);
        res[4] = new SimpleDateFormat("dd.MM.yyyy").format( LastServiceShipmentDate );
        res[5] = new SimpleDateFormat("dd.MM.yyyy").format( LastServiceReceipDate);
        res[6] = new SimpleDateFormat("dd.MM.yyyy").format( TheOldestServicePosition);
        res[7] = ReadyForShipmentQty.toString();
        return res;
    }
}