package jdbc;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
//        otchetFrame.AskUserData();
        Connect myDbTest = new Connect(ViewFrame.getUserLogin(), otchetFrame.getUserPass());

        try {
//        	myDbTest.collectDbProperties();
//          otchetFrame.viewDBData(myDbTest.getDBConnData());

            String selectTableSQL = "SELECT suplID FROM dbo.brak WHERE tip_vozvr = 1";
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
//                System.out.println(Connect.getKlientName(4681));

//                System.out.println(Arrays.toString(suplID.toArray()));
//                System.out.println(suplIdToName.toString());

                Integer SuplIDServiceQty = new Integer(0);
                BigDecimal SuplIDServiceVolume = new BigDecimal(0);
                SimpleDateFormat format;
                format = new SimpleDateFormat("yyyy-MM-dd");
                Date LastCheckDate;
                LastCheckDate = format.parse("2000-01-01");

                Date LastServiceShipmentDate;
                Date LastServiceReceipDate;
                Date TheOldestServicePosition;
                Integer ReadyForShipmentQty = new Integer(0);

                Object queryResult;

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
                String supl;

                for (Integer suplId : suplID) {
                    supl = suplIdToName.get(suplId);
//                    if("po.ua".compareTo(supl) == 0 ||
//                            "Grand-X".compareTo(supl) == 0 ||
//                            "i-Smile".compareTo(supl) == 0 ||
//                            "novella_грн".compareTo(supl) == 0){
//                        continue;
//                    }
                    System.out.println(supl + "\t");

//                    border = BorderFactory.createTitledBorder(supl);
                    value += valueStep;
                    progressBar.setValue((int) value);

//                    selectTableSQL = "SELECT COUNT(*), SUM(cost2), MAX(d_otpr), MIN(d_otpr) FROM dbo.brak WHERE tip_vozvr = 1 AND suplID =" + suplId.toString();
//                    queryResult = Connect.getMetaData(selectTableSQL);
//                    ResultSet resultSet = statement.executeQuery();
//                    while (queryResult.next()) {
//                        employerId = (resultSet.getLong("id"));
//                    }


                    //SuplIDServiceQty
                    selectTableSQL = "SELECT COUNT(*) FROM dbo.brak WHERE tip_vozvr = 1 AND suplID = " + suplId.toString();
                    queryResult = Connect.getMetaData(selectTableSQL);
                    if (queryResult != null) {
                        SuplIDServiceQty = (Integer) queryResult;
                        System.out.println(queryResult.toString());
                    } else {
                        SuplIDServiceQty = 0;
                    }

                    //SuplIDServiceVolume
                    selectTableSQL = "SELECT SUM(cost2) FROM dbo.brak WHERE tip_vozvr = 1 AND suplID = " + suplId.toString();
                    queryResult = Connect.getMetaData(selectTableSQL);
                    if (queryResult != null) {
                        SuplIDServiceVolume = new BigDecimal(queryResult.toString());
                        System.out.println(queryResult.toString());
                    } else {
                        SuplIDServiceVolume = new BigDecimal(0);
                    }

                    //LastCheckDate
//                    System.out.print("онлайн сверка\t");

                    //LastServiceShipmentDate
                    selectTableSQL = "SELECT MAX(d_otpr) FROM dbo.brak WHERE tip_vozvr = 1 AND suplID = " + suplId.toString();
                    queryResult = Connect.getMetaData(selectTableSQL);
                    format = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        LastServiceShipmentDate = format.parse(queryResult.toString());
                    } catch (Exception e) {
                        LastServiceShipmentDate = format.parse("2000-01-01");
                    }
                    if (queryResult != null) {
                        System.out.println(queryResult.toString());
                    }

                    //TheOldestServicePosition
                    selectTableSQL = "SELECT MIN(d_otpr) FROM dbo.brak WHERE tip_vozvr = 1 AND suplID = " + suplId.toString();      //
                    queryResult = Connect.getMetaData(selectTableSQL);
                    format = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        TheOldestServicePosition = format.parse(queryResult.toString());
                    } catch (Exception e) {
                        TheOldestServicePosition = format.parse("2000-01-01");
                    }
                    if (queryResult != null) {
                        System.out.println(queryResult.toString());
                    }

                    //ReadyForShipmentQty
                    selectTableSQL = "SELECT COUNT(*) FROM dbo.brak WHERE tip_vozvr = 0 AND suplID = " + suplId.toString();
                    queryResult = Connect.getMetaData(selectTableSQL);
                    if (queryResult != null) {
                        ReadyForShipmentQty = new Integer(queryResult.toString());
                        System.out.println(queryResult.toString());
                    } else {
                        ReadyForShipmentQty = 0;
                    }

                    //LastServiceReceipDate
                    selectTableSQL = "SELECT MAX(d_vozvr) FROM dbo.brak WHERE (tip_vozvr = 2 OR tip_vozvr = 3 OR " +
                            "tip_vozvr = 4 OR tip_vozvr = 5) AND suplID = " + suplId.toString();
                    queryResult = Connect.getMetaData(selectTableSQL);
                    try {
                        LastServiceReceipDate = format.parse(queryResult.toString());
                    } catch (Exception e) {
                        LastServiceReceipDate = format.parse("2000-01-01");
                    }
                    if (queryResult == null) {
                        System.out.println("queryResult = null");
                        System.out.println(selectTableSQL);
                    } else {
                        System.out.println(queryResult.toString());
                    }

                    resultData.add(new DataHeap(suplId,
                            suplIdToName.get(suplId),
                            SuplIDServiceQty,
                            SuplIDServiceVolume,
                            LastCheckDate,
                            LastServiceShipmentDate,
                            LastServiceReceipDate,
                            TheOldestServicePosition,
                            ReadyForShipmentQty));
                    System.out.println();
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
        String[] fieldsToShow = {"Поставщик",
                "кол-во товара\nв браке (шт)",
                "кол-во товара\nв браке (уе)",
                "дата последней\nсверки",
                "дата последней\nотправки брака\nпоставщику",
                "дата последнего\nполучения брака\nот поставщика",
                "самая старая\nпозиция (по дате)",
                "готово\nк отправке"};

        if (!resultData.isEmpty()) {
            f.setVisible(false);
            otchetFrame.ShowDataInTable(resultData, fieldsToShow);
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