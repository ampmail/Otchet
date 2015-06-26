package jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class Connect {
    private static java.sql.Connection con = null;
    private String userName;
    private String password;
    private StringBuilder Sout;

    public Connect(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    private java.sql.Connection getConnection() throws SQLException, ClassNotFoundException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            ResourceBundle rb = ResourceBundle.getBundle("db");
            String connectionString = new StringBuilder("")
                    .append("jdbc:sqlserver://")
                    .append(rb.getString("db.host"))
                    .append('\\')
                    .append(rb.getString("db.serverName"))
                    .append(":")
                    .append(rb.getString("db.port"))
                    .append(";databaseName=")
                    .append(rb.getString("db.name"))
                    .append(";selectMethod=")
                    .append(rb.getString("db.selectMethod"))
                    .append(";")
                    .toString();

            con = java.sql.DriverManager.getConnection(connectionString,
                    rb.getString("db.user"), rb.getString("db.password"));
            if (con != null) System.out.println("Connection Successful!");
        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//            System.out.println("Error Trace in getConnection() : " + e.getMessage());
            throw e;
        } catch (SQLException e) {
//            e.printStackTrace();
            throw e;
        }
        return con;
    }

    private static Statement statement = null;
    private static java.sql.ResultSet rs = null;

    public java.sql.ResultSet getDBData(String selectTableSQL) throws SQLException, ClassNotFoundException {

        try {
            if (con == null) {
                con = this.getConnection();
            }
            statement = con.createStatement();
            if (con != null) {
                rs = statement.executeQuery(selectTableSQL);
//                rs.close();
//                rs = null;
            } else System.out.println("Error: No active Connection");
        } catch (ClassNotFoundException e) {
            throw e;
        } catch (SQLException e) {
//            System.out.println(e.getMessage());
            throw e;
        }
        return rs;
    }

    public static Object getMetaData(String sqlQeury) throws SQLException {
        Object result = null;
        Statement s;
        try {
            s = con.createStatement();
            if (s.execute(sqlQeury)) {
                ResultSet r = s.getResultSet();
                ResultSetMetaData meta = r.getMetaData();
//                int cols = meta.getColumnCount();
//                int rownum = 0;

                while (r.next()) {
//                    rownum++;
//                    System.out.println("Crpокa: " + rownum);
//                    for (int i = 0; i < cols; i++) {
//                        System.out.print(meta.getColumnLabel(i + 1) + ": " + r.getObject(i + 1) + ", ");
//                    }
//                    System.out.println("");
                    result = r.getObject(1);
                }
            } else {
                System.err.println(s.getUpdateCount() + " строк обработано.");
            }
        } catch (SQLException e) {
//            e.printStackTrace();
            throw e;
        }
        return result;
    }

    public static String getKlientName(int KlientID) throws SQLException {
        Integer _KlientID = new Integer(KlientID);
        String SQLstring = "select Название from dbo.клиенты where КодПоставщика = " + _KlientID.toString();
        String KlientName = null;

        try {
            if (con != null) {
                java.sql.ResultSet rs;
                Statement statement;
                statement = con.createStatement();
                rs = statement.executeQuery(SQLstring);
                if (rs.next()) {
                    KlientName = rs.getString(1);
                }
            } else {
                System.out.println("Error: No active Connection");
            }
        } catch (SQLException e) {
//            e.printStackTrace();
            throw e;
        }
        return KlientName;
    }

    public void collectDbProperties() throws SQLException, ClassNotFoundException {
        java.sql.DatabaseMetaData dm;
        java.sql.ResultSet rs;
        try {
            con = this.getConnection();
            if (con != null) {
                dm = con.getMetaData();

                Sout = new StringBuilder("Driver Information\n");
                Sout.append("\tDriver Name: " + dm.getDriverName() + "\n");
                Sout.append("\tDriver Version: " + dm.getDriverVersion() + "\n");
                Sout.append("\nDatabase Information \n");
                Sout.append("\tDatabase Name: " + dm.getDatabaseProductName() + "\n");
                Sout.append("\tDatabase Version: " + dm.getDatabaseProductVersion() + "\n");
                Sout.append("Avalilable Catalogs \n");
                rs = dm.getCatalogs();
                while (rs.next()) {
                    Sout.append("\tcatalog: " + rs.getString(1) + "\n");
                }

                rs.close();
                rs = null;
                closeConnection();
            } else System.out.println("Error: No active Connection");
        } catch (SQLException e) {
            throw e;
        } catch (ClassNotFoundException e) {
            throw e;
        }
    }

    public void closeConnection() throws SQLException {
        try {
            if (con != null)
                con.close();
            con = null;
        } catch (SQLException e) {
//            e.printStackTrace();
            throw e;
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDBConnData() {
        return Sout.toString();
    }
}