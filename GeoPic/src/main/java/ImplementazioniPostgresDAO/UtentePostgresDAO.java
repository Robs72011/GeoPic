package ImplementazioniPostgresDAO;

import DAO.UtenteDAO;
import java.sql.*;
import java.util.ArrayList;

/**
 * Implementazione PostgreSQL per l'interfaccia {@link UtenteDAO}.
 * Gestisce la persistenza e le operazioni di recupero per le entità {@link Model.Utente},
 * inclusa la logica di verifica durante il login e la gestione dei vincoli di integrità.
 */
public class UtentePostgresDAO implements UtenteDAO {

    Connection connection;

    /**
     * Costruisce l'implementazione DAO con la connessione fornita.
     * @param connection La connessione attiva al database.
     */
    public  UtentePostgresDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Registra un nuovo utente nel sistema.
     * @param username Nome utente.
     * @param password Password associata.
     * @param isAdmin Flag per privilegi amministrativi.
     * @param isSoggetto Flag per indicare se l'utente funge anche da soggetto.
     * @return true se l'utente è stato inserito con successo, false in caso di errore o username duplicato.
     */
    @Override
    public boolean insertUtente(String username, String password, boolean isAdmin, boolean isSoggetto){
        String statement = "INSERT INTO galleria.UTENTE (Username, Password, IsAdmin, IsSoggetto) VALUES(?, ?, ?, ?)";

        try(PreparedStatement aggiuntaUtente = connection.prepareStatement(statement)) {

            aggiuntaUtente.setString(1, username);
            aggiuntaUtente.setString(2, password);
            aggiuntaUtente.setBoolean(3, isAdmin);
            aggiuntaUtente.setBoolean(4, isSoggetto);

            aggiuntaUtente.executeUpdate();

            return true;
        }catch(SQLException sqle){
            //23505 e' il codice di errore per la violazione di un attributo unique
            if (sqle.getSQLState().equals("23505"))
                System.err.println("Errore: lo username '" + username + "' è già esistente.");
            else
                sqle.printStackTrace();

            sqle.printStackTrace();
            return false;
        }
    }

    /**
     * Rimuove un utente dal sistema in base al suo ID.
     * @param idUtente L'ID dell'utente da eliminare.
     */
    @Override
    public void deleteUtente(int idUtente) {
        String statement = "DELETE FROM galleria.UTENTE WHERE IDUtente = ?";

        try(PreparedStatement rimozioneUtente = connection.prepareStatement(statement)){

            rimozioneUtente.setInt(1, idUtente);

            rimozioneUtente.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }

    }

    /**
     * Recupera l'elenco di tutti gli utenti registrati, popolando le liste fornite.
     */
    @Override
    public void getAllUtenti(ArrayList<Integer> idUtente, ArrayList<String> username, ArrayList<String> password,
                             ArrayList<Boolean> isAdmin, ArrayList<Boolean> isSoggetto){
        String statement = "SELECT * FROM galleria.UTENTE";

        try(PreparedStatement query = connection.prepareStatement(statement)){

            ResultSet rs = query.executeQuery();

            while(rs.next()){
                idUtente.add(rs.getInt("idUtente"));
                username.add(rs.getString("username"));
                password.add(rs.getString("password"));
                isAdmin.add(rs.getBoolean("isAdmin"));
                isSoggetto.add(rs.getBoolean("isSoggetto"));
            }


        }catch (SQLException sqle){
            sqle.printStackTrace();
        }

    }

    /**
     * Recupera l'ID dell'utente associato allo username fornito per gestire la sessione di login.
     * @param username Lo username da ricercare.
     * @return L'ID dell'utente, o null se non trovato.
     */
    @Override
    public Integer getLoggedInUtente(String username){
        String statement = "SELECT IDUtente FROM galleria.UTENTE WHERE username = ?";

        try(PreparedStatement query = connection.prepareStatement(statement)){

            query.setString(1, username);

            Integer idUtente = null;

            ResultSet rs = query.executeQuery();

            while(rs.next()){
                idUtente =  rs.getInt("idUtente");
            }

            return idUtente;
        }catch (SQLException sqle){
            sqle.printStackTrace();

            return null;
        }
    }
}
