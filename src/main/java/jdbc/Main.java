package jdbc;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    private static String[] selectedSuplIDFields = {"suplID"};
    private static java.sql.ResultSet rs;
    private static Set<Integer> suplID = new HashSet<Integer>();
    private static Map<Integer, String> suplIdToName = new HashMap<Integer, String>();
    public static List<DataHeap> resultData = new ArrayList<DataHeap>();

    public static void main(String[] args) {

        ViewFrame otchetFrame = new ViewFrame();
        otchetFrame.AskUserData();
        Connect myDbTest = new Connect(ViewFrame.getUserLogin(), otchetFrame.getUserPass());

        try {
//        	myDbTest.collectDbProperties();
//          otchetFrame.viewDBData(myDbTest.getDBConnData());

            String selectTableSQL = "SELECT" + createSelectLine(selectedSuplIDFields) + " FROM dbo.brak WHERE tip_vozvr = 1";
            rs = myDbTest.getDBData(selectTableSQL);
            if (rs != null) {
                String tempString = "";
                while (rs.next()) {
                    for (String field : selectedSuplIDFields) {
                        tempString = rs.getString(field);
                        if (field.compareTo("suplID") == 0) {
                            Integer suplId = new Integer(tempString);
                            tempString = Connect.getKlientName(suplId);
                            if (suplID.add(suplId)) {
                                suplIdToName.put(suplId, tempString);
                            }
                        }
                    }
                }
//                System.out.println(Arrays.toString(suplID.toArray()));
//                System.out.println(suplIdToName.toString());

                Integer SuplIDServiceQty = null;
                BigDecimal SuplIDServiceVolume = null;
                Date LastCheckDate = null;
                Date LastServiceShipmentDate = null;
                Date LastServiceReceipDate = null;
                Date TheOldestServicePosition = null;
                Integer ReadyForShipmentQty = null;
                SimpleDateFormat format;
                Object queryResult = null;

                for (Integer suplId : suplID) {
                    System.out.print(suplIdToName.get(suplId) + "\t");

                    //SuplIDServiceQty
                    selectTableSQL = "SELECT COUNT(*) FROM dbo.brak WHERE tip_vozvr = 1 AND suplID = " + suplId.toString();
                    queryResult = Connect.getMetaData(selectTableSQL);
                    SuplIDServiceQty = new Integer(queryResult.toString());
//                    System.out.print(SuplIDServiceQty + "\t");

                    //SuplIDServiceVolume
                    selectTableSQL = "SELECT SUM(cost2) FROM dbo.brak WHERE tip_vozvr = 1 AND suplID = " + suplId.toString();
                    queryResult = Connect.getMetaData(selectTableSQL);
                    SuplIDServiceVolume = new BigDecimal(queryResult.toString());
//                    System.out.print(SuplIDServiceVolume.setScale(2, RoundingMode.FLOOR) + "\t");

                    //LastCheckDate
//                    System.out.print("онлайн сверка\t");

                    //LastServiceShipmentDate
                    selectTableSQL = "SELECT MAX(d_otpr) FROM dbo.brak WHERE tip_vozvr = 1 AND suplID = " + suplId.toString();
                    queryResult = Connect.getMetaData(selectTableSQL);
                    format = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        LastServiceShipmentDate = format.parse( queryResult.toString() );
                    } catch (Exception e) {
//                        e.printStackTrace();
                        LastServiceShipmentDate = format.parse( "2000-01-01" );
                    }
//                    LastServiceShipmentDate = new Date(Connect.getMetaData(selectTableSQL).toString());
//                    System.out.print(LastServiceShipmentDate + "\t");
//                    System.out.print(Connect.getMetaData(selectTableSQL).toString() + "\t");

                    //LastServiceReceipDate
                    selectTableSQL = "SELECT MAX(d_vozvr) FROM dbo.brak WHERE (tip_vozvr = 2 OR tip_vozvr = 3 OR " +
                            "tip_vozvr = 4 OR tip_vozvr = 5) AND suplID = " + suplId.toString();
                    queryResult = Connect.getMetaData(selectTableSQL);
                    format = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        LastServiceReceipDate = format.parse( queryResult.toString() );
                    } catch (Exception e) {
//                        e.printStackTrace();
                        LastServiceReceipDate = format.parse( "2000-01-01" );
                    }
//                    LastServiceReceipDate = new Date (Connect.getMetaData(selectTableSQL).toString());
//                    System.out.print(LastServiceReceipDate + "\t");

                    //TheOldestServicePosition
                    selectTableSQL = "SELECT MIN(d_otpr) FROM dbo.brak WHERE tip_vozvr = 1 AND suplID = " + suplId.toString();      //
                    queryResult = Connect.getMetaData(selectTableSQL);
                    format = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        TheOldestServicePosition = format.parse( queryResult.toString() );
                    } catch (Exception e) {
//                        e.printStackTrace();
                        TheOldestServicePosition = format.parse( "2000-01-01" );
                    }
//                    TheOldestServicePosition = new Date (Connect.getMetaData(selectTableSQL).toString());
//                    System.out.print(TheOldestServicePosition + "\t");

                    //ReadyForShipmentQty
                    selectTableSQL = "SELECT COUNT(*) FROM dbo.brak WHERE tip_vozvr = 0 AND suplID = " + suplId.toString();
                    queryResult = Connect.getMetaData(selectTableSQL);
                    ReadyForShipmentQty = new Integer(queryResult.toString());
//                    System.out.print(ReadyForShipmentQty + "\t");

                    System.out.println();

                    LastCheckDate = format.parse( "2000-01-01" );

                    DataHeap dh = new DataHeap(suplId, suplIdToName.get(suplId),
                            SuplIDServiceQty,
                            SuplIDServiceVolume,
                            LastCheckDate,
                            LastServiceShipmentDate,
                            LastServiceReceipDate,
                            TheOldestServicePosition,
                            ReadyForShipmentQty);
                    resultData.add(dh);
                }
            }
            rs.close();
            rs = null;
        } catch (Exception e) {
            otchetFrame.viewErrData(e.toString());
        } finally {
            try {
                myDbTest.closeConnection();
            } catch (SQLException e) {
                otchetFrame.viewErrData(e.toString());
            }
            System.out.println("Connection closed");
        }
        String [] fieldsToShow = {"Поставщик",
                "кол-во товара в браке (шт)",
                "кол-во товара в браке (уе)",
                "дата последней сверки",
                "дата последней отправки брака поставщику",
                "дата последнего получения брака от поставщика",
                "самая старая позиция (по дате)",
                "готово к отправке"};

        if (!resultData.isEmpty()){
            otchetFrame.ShowDataInTable( resultData, fieldsToShow);
        }
//        System.exit(0);
    }

    private static String createSelectLine(String[] inputData) {
        StringBuilder selectLine = new StringBuilder(" ");
        for (String data : inputData) {
            selectLine.append(data);
            selectLine.append(",");
        }
        selectLine.deleteCharAt(selectLine.length() - 1);
        return selectLine.toString();
    }
}