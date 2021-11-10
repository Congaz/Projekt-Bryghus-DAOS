package model;

import java.sql.*;

public class Opgaver_06
{

    public static void connect()
    {
        String serverName = "LEGION5-WINPRO\\SQLEXPRESS";
        String dbName = "02_Klub";
        String userName = "sa"; // Systemadministrator.
        String password = "1234";
        String connect = "jdbc:sqlserver://" + serverName + ";" +
                "databaseName=" + dbName + ";" +
                "user=" + userName + ";" +
                "password=" + password + ";";


        try {
            Connection conn = DriverManager.getConnection(connect);
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            ResultSet res = stmt.executeQuery("select * from Medlem");
            while (res.next()) {
                System.out.println(
                        res.getString(1) + "\t" +
                                res.getString(2) + " \t " +
                                res.getString(3)
                );
            }

            if (res != null) res.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (
                SQLException sqlE) {
            System.out.println("SQL Exception: " + sqlE.getMessage());
            System.out.println("SQL Error code: " + sqlE.getErrorCode());
        } catch (Exception e) {
            System.out.println("fejl:  " + e.getMessage());
        }
    }
}
