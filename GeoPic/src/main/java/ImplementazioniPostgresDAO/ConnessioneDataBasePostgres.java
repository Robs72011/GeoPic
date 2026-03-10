package ImplementazioniPostgresDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnessioneDataBasePostgres{
    private static ConnessioneDataBasePostgres istanza = null;
    private Connection connessione = null;
    private final String username = "postgres";
    private final String password = "9462";
    private final String url = "jdbc:postgresql://localhost:5432/postgres";

    private final String driver = "org.postgresql.Driver";

    private ConnessioneDataBasePostgres(){
        try{
            Class.forName(driver);
            connessione = DriverManager.getConnection(url, username, password);
        }
        catch(ClassNotFoundException classNotFoundException){
            System.out.println("Driver per il DB non trovato!");
            classNotFoundException.printStackTrace();
        }
        catch(SQLException sqlException){
            System.out.println("Connessione al DB non riuscita");
            sqlException.printStackTrace();
        }
    }

    public Connection getConnesione(){
        return connessione;
    }

    public static ConnessioneDataBasePostgres getIstanza() throws SQLException{
        if(istanza == null || istanza.getConnesione().isClosed())
            istanza = new ConnessioneDataBasePostgres();

        return istanza;
    }
}
