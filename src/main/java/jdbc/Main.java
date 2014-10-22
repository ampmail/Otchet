package jdbc;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Main {

    private static String[] selectedSuplIDFields = {"suplID"};
    private static java.sql.ResultSet rs;
    private static Set<Integer> suplID = new HashSet<Integer>();
    private static Map<Integer, String> suplIdToName = new HashMap<Integer, String>();
    public static List<DataHeap> resultData = new ArrayList<DataHeap>();
    private static JFrame f;

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

                f = new JFrame("Processing database request");
                f.setDefaultCloseOperation(JFrame.ICONIFIED);
                Container content = f.getContentPane();
                JProgressBar progressBar = new JProgressBar();
                progressBar.setValue(0);
                progressBar.setStringPainted(true);
                Border border = BorderFactory.createTitledBorder("...");
                progressBar.setBorder(border);
                content.add(progressBar, BorderLayout.NORTH);
                f.setSize(300, 100);
                f.setVisible(true);
                double valueStep = 100.0 / suplID.size();
                double value = 0.0;

                for (Integer suplId : suplID) {
//                    System.out.println(suplIdToName.get(suplId) + "\t");

                    border = BorderFactory.createTitledBorder(suplIdToName.get(suplId));
                    value += valueStep;
                    progressBar.setValue((int)value);

                    //SuplIDServiceQty
                    selectTableSQL = "SELECT COUNT(*) FROM dbo.brak WHERE tip_vozvr = 1 AND suplID = " + suplId.toString();
                    queryResult = Connect.getMetaData(selectTableSQL);
//                    SuplIDServiceQty = new Integer(queryResult.toString());
                    SuplIDServiceQty = (Integer)queryResult;
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
//                    System.out.print(LastServiceShipmentDate + "\t");

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
//                    System.out.print(TheOldestServicePosition + "\t");

                    //ReadyForShipmentQty
                    selectTableSQL = "SELECT COUNT(*) FROM dbo.brak WHERE tip_vozvr = 0 AND suplID = " + suplId.toString();
                    queryResult = Connect.getMetaData(selectTableSQL);
                    ReadyForShipmentQty = new Integer(queryResult.toString());
//                    System.out.print(ReadyForShipmentQty + "\t");

//                    System.out.println();

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
                "кол-во товара\nв браке (шт)",
                "кол-во товара\nв браке (уе)",
                "дата последней\nсверки",
                "дата последней\nотправки брака\nпоставщику",
                "дата последнего\nполучения брака\nот поставщика",
                "самая старая\nпозиция (по дате)",
                "готово\nк отправке"};

        if (!resultData.isEmpty()){
            f.setVisible(false);
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