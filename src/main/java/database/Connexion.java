package database;

import java.sql.*;

public class Connexion {
    private static String url = "jdbc:mysql://localhost:3306/tfarhida1";
    private static String user = "root";
    private static String mdp = "";
    public static Connection cnx = null;

    public static Connection getInstance() {
        try {
            if(cnx==null)
                cnx=DriverManager.getConnection(url, user, mdp);
            System.out.println("connexion etablie");
        }
        catch(SQLException e) {
            System.out.println("erreur pendant la connexion " + e.getMessage());
        }
        return cnx;
    }

}
