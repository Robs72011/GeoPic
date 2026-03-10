package ImplementazioniPostgresDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestisce la connessione al database PostgreSQL utilizzando il design pattern Singleton.
 * Assicura che venga mantenuta una singola istanza attiva della connessione,
 * ottimizzando le risorse di sistema.
 */
public class ConnessioneDataBasePostgres{
    private static ConnessioneDataBasePostgres istanza = null;
    private Connection connessione = null;
    private final String username = "postgres";
    private final String password = "9462";
    private final String url = "jdbc:postgresql://localhost:5432/postgres";

    private final String driver = "org.postgresql.Driver";

    /**
     * Costruttore privato che inizializza il driver JDBC e stabilisce la connessione.
     */
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

    /**
     * Restituisce l'oggetto connessione attivo.
     * @return L'istanza di {@link Connection} verso il database.
     */
    public Connection getConnesione(){
        return connessione;
    }

    /**
     * Recupera l'istanza unica della classe (Singleton).
     * Se l'istanza non è stata creata o la connessione è stata chiusa, ne istanzia una nuova.
     * @return L'unica istanza di {@link ConnessioneDataBasePostgres}.
     * @throws SQLException Se si verifica un errore durante la connessione al database.
     */
    public static ConnessioneDataBasePostgres getIstanza() throws SQLException{
        if(istanza == null || istanza.getConnesione().isClosed())
            istanza = new ConnessioneDataBasePostgres();

        return istanza;
    }
}
