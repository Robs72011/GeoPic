package ImplementazioniPostgresDAO;

import DAO.PartecipaDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Implementazione PostgreSQL per l'interfaccia {@link PartecipaDAO}.
 * Gestisce la persistenza delle associazioni tra utenti e gallerie,
 * regolando i permessi di accesso per le gallerie condivise.
 */
public class PartecipaPostgresDAO implements PartecipaDAO {
    Connection connection;

    /**
     * Costruisce l'implementazione DAO con la connessione fornita.
     * @param connection La connessione attiva al database.
     */
    public PartecipaPostgresDAO(Connection connection){
        this.connection = connection;
    }

    /**
     * Associa un utente a una galleria per consentirne la visualizzazione.
     * @param idGalleria L'ID della galleria.
     * @param idUtente L'ID dell'utente da aggiungere come partecipante.
     */
    @Override
    public void insertPartecipante(int idGalleria, int idUtente) {
        String statement = "INSERT INTO galleria.PARTECIPA VALUES(?,?)";

        try(PreparedStatement aggiuntaPartecipanteAGalleria = connection.prepareStatement(statement)){

            aggiuntaPartecipanteAGalleria.setInt(1, idGalleria);
            aggiuntaPartecipanteAGalleria.setInt(2, idUtente);

            aggiuntaPartecipanteAGalleria.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    /**
     * Rimuove un utente dall'elenco dei partecipanti di una galleria.
     * @param idGalleria L'ID della galleria.
     * @param idUtente L'ID dell'utente da rimuovere.
     */
    @Override
    public void deletePartecipante(int idGalleria, int idUtente) {
        String statement = "DELETE FROM galleria.PARTECIPA WHERE IDGalleria = ? AND IDUtente = ?";

        try(PreparedStatement rimozioneUtenteDaGalleriaCondivisa = connection.prepareStatement(statement)){

            rimozioneUtenteDaGalleriaCondivisa.setInt(1, idGalleria);
            rimozioneUtenteDaGalleriaCondivisa.setInt(2, idUtente);

            rimozioneUtenteDaGalleriaCondivisa.executeUpdate();
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    /**
     * Recupera tutte le associazioni utente-galleria presenti nel sistema.
     * @param idGalleria Lista in cui verranno inseriti gli ID delle gallerie.
     * @param idUtente Lista in cui verranno inseriti gli ID degli utenti.
     */
    public void getAllPartecipanti(ArrayList<Integer> idGalleria, ArrayList<Integer> idUtente){
        String statement = "SELECT * FROM galleria.PARTECIPA";

        try(PreparedStatement query = connection.prepareStatement(statement)) {

            ResultSet resultSet = query.executeQuery();

            while(resultSet.next()){
                idGalleria.add(resultSet.getInt("IDGalleria"));
                idUtente.add(resultSet.getInt("IDUtente"));
            }

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
