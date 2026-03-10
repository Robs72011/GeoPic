package ImplementazioniPostgresDAO;

import DAO.SoggettoDAO;

import java.sql.*;
import java.util.ArrayList;

/**
 * Implementazione PostgreSQL per l'interfaccia {@link SoggettoDAO}.
 * Gestisce la persistenza e il recupero dell'entità {@link Model.Soggetto},
 * includendo la gestione dell'associazione opzionale con l'entità {@link Model.Utente}.
 */
public class SoggettoPostgresDAO implements SoggettoDAO {
    Connection connection;

    /**
     * Costruisce l'implementazione DAO con la connessione fornita.
     * @param connection La connessione attiva al database.
     */
    public SoggettoPostgresDAO(Connection connection){
        this.connection =  connection;
    }

    /**
     * Inserisce un nuovo soggetto nel sistema senza associazione ad un utente.
     * @param nomeSoggetto Nome del soggetto.
     * @param categoria Categoria di appartenenza del soggetto.
     */
    @Override
    public void insertSoggetto(String nomeSoggetto, String categoria) {
        String statement = "INSERT INTO galleria.SOGGETTO VALUES(?, ?, ?)";

        try(PreparedStatement aggiuntaSoggetto = connection.prepareStatement(statement)){

            aggiuntaSoggetto.setString(1, nomeSoggetto);
            aggiuntaSoggetto.setString(2, categoria);
            aggiuntaSoggetto.setNull(3, Types.INTEGER);

            aggiuntaSoggetto.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    /**
     * Elimina un soggetto dal sistema.
     * @param nomeSoggetto Nome del soggetto da rimuovere.
     */
    @Override
    public void deleteSoggetto(String nomeSoggetto) {
        String statement = "DELETE FROM galleria.SOGGETTO WHERE NomeSoggetto LIKE ?";

        try(PreparedStatement rimozioneSoggetto = connection.prepareStatement(statement)){

            rimozioneSoggetto.setString(1, nomeSoggetto);

            rimozioneSoggetto.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    /**
     * Inserisce un nuovo soggetto associandolo a un utente esistente.
     * @param nomeUtente Nome del soggetto.
     * @param categoria Categoria di appartenenza.
     * @param idUtente ID dell'utente associato.
     */
    @Override
    public void insertUtenteAsSoggetto(String nomeUtente, String categoria, int idUtente) {
        String statement = "INSERT INTO galleria.SOGGETTO VALUES(?, ?, ?)";

        try(PreparedStatement aggiuntaUtenteComeSoggetto = connection.prepareStatement(statement)){

            aggiuntaUtenteComeSoggetto.setString(1, nomeUtente);
            aggiuntaUtenteComeSoggetto.setString(2, categoria);
            aggiuntaUtenteComeSoggetto.setInt(3, idUtente);

            aggiuntaUtenteComeSoggetto.executeUpdate();
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    /**
     * Elimina un soggetto associato a un utente.
     * @param nomeUtente Nome del soggetto da rimuovere.
     */
    @Override
    public void deleteUtenteAsSoggetto(String nomeUtente) {
        String statement = "DELETE FROM galleria.SOGGETTO WHERE  NomeSoggetto = ?";

        try(PreparedStatement rimozioneSoggettoConUtente = connection.prepareStatement(statement)){

            rimozioneSoggettoConUtente.setString(1, nomeUtente);

            rimozioneSoggettoConUtente.executeUpdate();
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    /**
     * Recupera tutti i soggetti censiti e le rispettive categorie.
     */
    public void getAllSoggetti(ArrayList<String> nomeSoggetto, ArrayList<String> categoria){
        String statement = "SELECT * FROM galleria.SOGGETTO";

        try(PreparedStatement query = connection.prepareStatement(statement)) {

            ResultSet resultSet = query.executeQuery();

            while(resultSet.next()){
                nomeSoggetto.add(resultSet.getString("NomeSoggetto"));
                categoria.add(resultSet.getString("Categoria"));
            }

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    /**
     * Recupera le associazioni tra i soggetti e gli utenti ('Rappresenta').
     */
    public void getRappresenta(ArrayList<String> nomeSoggetto, ArrayList<Integer> utente){
        String statement = "SELECT NOMESOGGETTO, IDUTENTE FROM galleria.SOGGETTO";

        try(PreparedStatement query = connection.prepareStatement(statement)){
            ResultSet resultSet = query.executeQuery();

            while(resultSet.next()){
                nomeSoggetto.add(resultSet.getString("NomeSoggetto"));
                int u = resultSet.getInt("IDUTENTE");
                if(resultSet.wasNull()){
                    utente.add(null);
                }else{
                    utente.add(u);
                }
            }

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
