package model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {

//		opgave_6A();
//		opgave_6B();
		opgave_6C();

	}
    
    
    public static String getConnectString()
    {
        String serverName = "localhost\\SQLEXPRESS"; // IntelliJ (port 61869)
        String dbName = "AarhusBryghus";
        String userName = "sa"; // Systemadministrator.
        String password = "1234";
        String connect = "jdbc:sqlserver://" + serverName + ";" +
                "databaseName=" + dbName + ";" +
                "user=" + userName + ";" +
                "password=" + password + ";";
        return connect;
    }


    public static void opgave_6A()
    {
        try {

            BufferedReader inLine = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("--- Opret produkt ---");
            System.out.print("Navn: ");
            String navn = inLine.readLine().trim();
            System.out.print("Lagerbeholdning (heltal): ");
            int antal = Integer.parseInt(inLine.readLine().trim());
            System.out.print("Angiv min. lagerbeholdning for advarsel (heltal): ");
            int minAntal = Integer.parseInt(inLine.readLine().trim());

            System.out.print("Eksisterende produktgrupper: ");
            for (int i = 0; i < Main.getKategorier().size(); i++) {
                System.out.print(Main.getKategorier().get(i));
                if (i < Main.getKategorier().size() - 1) System.out.print(", ");
            }
            System.out.println();
            System.out.print("Angiv produktgruppe: ");
            String kategoriNavn = inLine.readLine();

            String sql = "INSERT INTO Produkt " +
                        "(navn, lagerbeholdning, advarselMinAntal, kategoriNavn) " +
                        "VALUES (?, ?, ?, ?)";

            Connection conn = DriverManager.getConnection(Main.getConnectString());
            PreparedStatement prestmt = conn.prepareStatement(sql);
            prestmt.clearParameters();
            prestmt.setString(1, navn);
            prestmt.setInt(2, antal);
            prestmt.setInt(3, minAntal);
            prestmt.setString(4, kategoriNavn);

            // For INSERT, UPDATE og DELETE:
            prestmt.executeUpdate();

            System.out.println("Produkt oprettet.");

            if (prestmt != null) prestmt.close();
            if (conn != null) conn.close();
        }
        catch (SQLException sqlE) {
            switch (sqlE.getErrorCode()) {
                case 547:
                    System.out.println("Fejl: Den angivede produktgruppe eksisterer ikke.");
                    break;
                 case 8152:
                    System.out.println("Fejl: Produktnavn overstiger 255 karakterer.");
                    break;
                default:
                    System.out.println("SQL Exception: " + sqlE.getMessage());
                    System.out.println("SQL Error code: " + sqlE.getErrorCode());
            }

            System.out.println("Produkt er ikke oprettet.");

        }
        catch (NumberFormatException ne) {
            System.out.println("Fejl: Kun heltal er gyldigt input.");
            System.out.println("Produkt er ikke oprettet.");
        }
        catch (Exception e) {
            System.out.println("fejl:  " + e.getMessage());
            System.out.println("fejl:  " + e.getCause());
        }
    }

    public static void opgave_6B()
    {
        try {

            BufferedReader inLine = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("--- Vis samlet salg i kr. på et givent produkt på en given dato ---");
            System.out.print("Dato: ");
            String dato = inLine.readLine().trim();
            System.out.print("Produktnavn: ");
            String navn = inLine.readLine().trim();
            System.out.print("Produktgruppe: ");
            String kategori = inLine.readLine().trim();

           String sql = "SELECT " +
                   "SUM(ISNULL(Ol.aftaltPris, (Ol.antal * P.pris) - ((Ol.antal * P.pris) / 100 * Ol.rabat))) as 'Samlet (inkl. rabat)' " +
                   "FROM Ordre O " +
                   "inner join Ordrelinje Ol " +
                   "on O.id = Ol.ordreId " +
                   "inner join Pris P " +
                   "on Ol.prisId = P.id " +
                   "inner join Produkt Prod " +
                   "on P.produktId = Prod.id " +
                   "WHERE O.dato = ? " +
                   " AND Prod.id = (SELECT id FROM Produkt WHERE navn = ? AND kategoriNavn = ?)";

           // dato, produktnavn, kategori

            Connection conn = DriverManager.getConnection(Main.getConnectString());
            PreparedStatement prestmt = conn.prepareStatement(sql);
            prestmt.clearParameters();
            prestmt.setString(1, dato);
            prestmt.setString(2, navn);
            prestmt.setString(3, kategori);
            ResultSet res = prestmt.executeQuery();

             while (res.next()) {
                 if (res.getString(1) != null) {
                    System.out.println("Kr. " + res.getString(1));
                 }
                 else {
                    System.out.println("Der eksistere ingen data for indtastede værdier.");
                 }
             }


            if (res != null) res.close();
            if (prestmt != null) prestmt.close();
            if (conn != null) conn.close();
        }
        catch (SQLException sqlE) {
            switch (sqlE.getErrorCode()) {
                case 547:
                    System.out.println("Fejl: Den angivede produktgruppe eksisterer ikke.");
                    break;
                 case 8152:
                    System.out.println("Fejl: Produktnavn overstiger 255 karakterer.");
                    break;
                default:
                    System.out.println("SQL Exception: " + sqlE.getMessage());
                    System.out.println("SQL Error code: " + sqlE.getErrorCode());
            }
        }
        catch (Exception e) {
            System.out.println("fejl:  " + e.getMessage());
            System.out.println("fejl:  " + e.getCause());
        }

    }


    public static void opgave_6C()
    {
        try {
            // --- Opret ordrelinje ---------------------------------------------------------------
            BufferedReader inLine = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("--- Opret ordrelinje ---");
            System.out.print("Ordre Id (ordrelinjen tilføjes denne ordre): ");
            int ordreId = Integer.parseInt(inLine.readLine().trim());
            System.out.print("Salgssituation: ");
            String prislisteNavn = inLine.readLine().trim();
            System.out.print("Produktgruppe: ");
            String kategori = inLine.readLine().trim();
            System.out.print("Produktnavn: ");
            String produktNavn = inLine.readLine().trim();
            System.out.print("Antal: ");
            int antal = Integer.parseInt(inLine.readLine().trim());
            System.out.print("Rabat (procent): ");
            int rabat = Integer.parseInt(inLine.readLine().trim());
            System.out.print("Evt. aftalt pris: (0 for ingen): ");
            int aftaltPris = Integer.parseInt(inLine.readLine().trim());

            String sql = "INSERT INTO Ordrelinje	(ordreId, prisId, antal, rabat, aftaltPris) VALUES " +
                    "(?, (SELECT id FROM Pris WHERE prislisteNavn = ? AND produktId = " +
                    "(SELECT id FROM Produkt WHERE navn = ? AND kategoriNavn = ?)), ?, ?, ?)";

            // 1: ordreId, 2: prislisteNavn, 3: produktnavn, 4: kategori, 5: antal, 6: rabat, 7: aftaltPris

            Connection conn = DriverManager.getConnection(Main.getConnectString());
            PreparedStatement prestmt = conn.prepareStatement(sql);
            prestmt.clearParameters();
            prestmt.setInt(1, ordreId);
            prestmt.setString(2, prislisteNavn);
            prestmt.setString(3, produktNavn);
            prestmt.setString(4, kategori);
            prestmt.setInt(5, antal);
            prestmt.setInt(6, rabat);
            if (aftaltPris != 0) {
                prestmt.setInt(7, aftaltPris);
            }
            else {
                prestmt.setNull(7, Types.INTEGER);
            }
            int succes = prestmt.executeUpdate();
            // Hvorfor bliver succes altid 1, selvom der ikke indsættes en række?
            //System.out.println("Returned: " + succes);
            //System.out.println("getUpdateCount(): " + prestmt.getUpdateCount());

            // --- Tjek lagerbeholdning for produkt i ordrelinje ---------------------------------------------------
            sql = "SELECT lagerbeholdning, advarselMinAntal FROM Produkt Prod WHERE Prod.id = " +
                    "(SELECT id FROM Produkt WHERE navn = ? AND kategoriNavn = ?)";

            prestmt.clearParameters();
            prestmt = conn.prepareStatement(sql);
            prestmt.setString(1, produktNavn);
            prestmt.setString(2, kategori);
            ResultSet res = prestmt.executeQuery();

             while (res.next()) {
                int lagerbeholdning = res.getInt(1);
                int advarselMinAntal = res.getInt(2);
                if (lagerbeholdning <= advarselMinAntal) {
                    System.out.println("*** Advarsel! ***");
                    System.out.println("Lagerbeholdning for " + produktNavn + " (" + kategori + ") er på "
                        + lagerbeholdning + " stk.");
                }
             }


            if (prestmt != null) prestmt.close();
            if (conn != null) conn.close();
        }
        catch (SQLException sqlE) {
            switch (sqlE.getErrorCode()) {
                case 547:
                    System.out.println("Fejl: Den angivede produktgruppe eksisterer ikke.");
                    break;
                 case 8152:
                    System.out.println("Fejl: Produktnavn overstiger 255 karakterer.");
                    break;
                default:
                    System.out.println("SQL Exception: " + sqlE.getMessage());
                    System.out.println("SQL Error code: " + sqlE.getErrorCode());
            }
        }
         catch (NumberFormatException ne) {
            System.out.println("Fejl: Kun heltal er gyldigt input.");
            System.out.println("Ordrelinjen er ikke oprettet.");
        }
        catch (Exception e) {
            System.out.println("fejl:  " + e.getMessage());
            System.out.println("fejl:  " + e.getCause());
        }
    }


    private static ArrayList<String> getKategorier()
    {
        ArrayList<String> kategorier = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(Main.getConnectString());
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            ResultSet res = stmt.executeQuery("select navn from Kategori");
            while (res.next()) {
                kategorier.add(res.getString(1));
            }

            if (res != null) res.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();

        }
        catch (SQLException sqlE) {
                System.out.println("SQL Exception: " + sqlE.getMessage());
                System.out.println("SQL Error code: " + sqlE.getErrorCode());
        }
        catch (Exception e) {
            System.out.println("Fejl:  " + e.getMessage());
        }

        return kategorier;

    }
}
