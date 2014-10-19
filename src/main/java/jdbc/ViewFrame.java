package jdbc;

import java.awt.*;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class ViewFrame {

    private int columnFreeSpace = 20;
    private final int frameWidth = 1400;
    private final int frameHeigth = 800;

    void ShowDataInTable(List<DataHeap> dataHeap, String[] fieldsToShow) {

        JFrame jfrm = new JFrame("Отчет");
        jfrm.setSize(frameWidth, frameHeigth);
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(3, 3, 3, 3));
        contentPane.setLayout(new BorderLayout(0, 0));

        TableModel MyTableModel = new TableModel(fieldsToShow);
        JTable table = new JTable(MyTableModel);

        JScrollPane scrollPane = new JScrollPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);
        scrollPane.setViewportView(table);

        table.setAutoCreateRowSorter(true);
        table.setGridColor(new Color(0x66, 0x66, 0x66, 0x66));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        jfrm.add(contentPane);

        for (DataHeap dh : dataHeap) {
            MyTableModel.addRow(dh.toArray());
        }

//        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        String hv;
        TableColumn column = null;
        JTableHeader th;
        FontMetrics fm;
        int columnsWidth = 0;
        int stringWidth;
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            column = table.getColumnModel().getColumn(i);
            hv = column.getHeaderValue().toString();
            th = table.getTableHeader();
            fm = th.getFontMetrics(th.getFont());
            stringWidth = fm.stringWidth(hv) + columnFreeSpace;
            column.setPreferredWidth(stringWidth);
            columnsWidth += stringWidth;
        }
//        column.setPreferredWidth(frameWidth - columnsWidth);

        jfrm.setVisible(true);
    }

    void viewDBData(String msg) {
        makeViewFrame (msg, "DbProperties", 600, 400);
    }

    void viewErrData(String msg) {
        makeViewFrame (msg, "Error", 400, 300);
    }

    void makeViewFrame (String msg, String titul, int width, int height){
        JFrame jfrm = new JFrame(titul);
        jfrm.setSize(width, height);
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTextArea textArea = new JTextArea(50, 50);
        textArea.setText(msg);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Serif", Font.ITALIC, 16));
        jfrm.add(new JScrollPane(textArea));
        jfrm.setVisible(true);
    }

    private static String UserPass = "";
    private static String UserLogin = "";

    public static void AskUserData() {
        JFrame jfrm0 = new JFrame("Input Login");
        do {
            UserLogin = JOptionPane.showInputDialog( jfrm0, "Input Login: ", new String(""));
        } while (UserLogin.isEmpty());
        JFrame jfrm1 = new JFrame("Input password");
        do {
            UserPass = JOptionPane.showInputDialog( jfrm1, "Input password for user " + UserLogin + ": ", new String(""));
        } while (UserPass.isEmpty());
    }

    public static String getUserLogin() {
        return UserLogin;
    }

    public static String getUserPass() {
        return UserPass;
    }
}