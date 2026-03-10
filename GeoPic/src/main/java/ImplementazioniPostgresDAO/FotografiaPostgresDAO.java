package ImplementazioniPostgresDAO;

import DAO.FotografiaDAO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Implementazione PostgreSQL per l'interfaccia {@link FotografiaDAO}.
 * Gestisce la persistenza, l'aggiornamento e il recupero delle entità {@link Model.Fotografia},
 * inclusa la gestione della visibilità e dei metadati temporali.
 */
public class FotografiaPostgresDAO implements FotografiaDAO {
    private final Connection connection;

    /**
     * Costruisce l'implementazione DAO con la connessione fornita.
     * @param connection La connessione attiva al database.
     */
    public FotografiaPostgresDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Inserisce una nuova fotografia nel database e ne restituisce l'ID generato.
     * @param device Il dispositivo di cattura.
     * @param dataScatto La data in cui è stata scattata la foto.
     * @param dataEliminazione La data programmata per l'eliminazione (può essere null).
     * @param visibilita Stato di visibilità della foto.
     * @param coordinate Coordinate geografiche del luogo.
     * @param autore ID dell'autore della foto.
     * @return L'ID univoco della fotografia appena creata, o null in caso di fallimento.
     */
    @Override
    public Integer insertFotografia(String device, LocalDate dataScatto, LocalDate dataEliminazione,
                                    boolean visibilita, String coordinate, int autore) {
        // Nota: Ho corretto l'ordine dei parametri nella query per matchare i tuoi setString/setInt
        String statement = "INSERT INTO galleria.FOTOGRAFIA (Dispositivo, Autore, Coordinate, Visibilita, DataScatto, DataEliminazione) VALUES(?, ?, ?, ?, ?, ?)";

        // 1. Aggiungiamo Statement.RETURN_GENERATED_KEYS
        try (PreparedStatement newFoto = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

            newFoto.setString(1, device);
            newFoto.setInt(2, autore);
            newFoto.setString(3, coordinate);
            newFoto.setBoolean(4, visibilita);
            newFoto.setDate(5, Date.valueOf(dataScatto));

            if (dataEliminazione == null) {
                newFoto.setNull(6, Types.DATE);
            } else {
                newFoto.setDate(6, Date.valueOf(dataEliminazione));
            }

            newFoto.executeUpdate();

            // 2. Recuperiamo l'ID generato
            try (ResultSet rs = newFoto.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Ritorna l'ID (prima colonna dei generated keys)
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return null; // Ritorna null se l'inserimento fallisce
    }

    /**
     * Elimina una fotografia dal database in base al suo identificativo.
     * @param IDFoto L'ID della fotografia da rimuovere.
     */
    @Override
    public void deleteFotografia(int IDFoto) {
        String statement = "DELETE FROM galleria.FOTOGRAFIA WHERE IDFoto = ?";

        try{
            PreparedStatement deleteFoto = connection.prepareStatement(statement);

            deleteFoto.setInt(1, IDFoto);

            deleteFoto.executeUpdate();
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    /**
     * Aggiorna lo stato di visibilità di una fotografia.
     * @param IDFoto L'ID della fotografia da aggiornare.
     * @param visibilita Il nuovo stato di visibilità.
     */
    @Override
    public void updateVisibilita(int IDFoto, boolean visibilita) {
        String statement = "UPDATE galleria.FOTOGRAFIA SET Visibilita = ? WHERE IDFoto = ?";

        try{
            PreparedStatement updateVisibilita = connection.prepareStatement(statement);

            updateVisibilita.setBoolean(1, visibilita);
            updateVisibilita.setInt(2, IDFoto);

            updateVisibilita.executeUpdate();
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    /**
     * Aggiorna la data di eliminazione programmata per una fotografia.
     * @param IDFoto L'ID della fotografia da aggiornare.
     * @param newDataEliminazione La nuova data di eliminazione.
     */
    @Override
    public void updateDataEliminazione(int IDFoto, LocalDate newDataEliminazione) {
        String statement = "UPDATE galleria.FOTOGRAFIA SET dataEliminazione = ? WHERE IDFoto = ?";

        try(PreparedStatement updateDataEliminazione = connection.prepareStatement(statement)){

            if(newDataEliminazione == null){
                updateDataEliminazione.setNull(1, Types.DATE);
            }else{
                updateDataEliminazione.setDate(1, Date.valueOf(newDataEliminazione));
            }
            updateDataEliminazione.setInt(2, IDFoto);

            updateDataEliminazione.executeUpdate();
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    /**
     * Recupera l'elenco completo delle fotografie, popolando le liste passate come riferimento.
     */
    public void getAllFotografie(ArrayList<Integer> idFoto, ArrayList<String> device, ArrayList<Integer> autore,
                                 ArrayList<String> coordinate, ArrayList<Boolean> visibilita,
                                 ArrayList<LocalDate> dataDiScatto, ArrayList<LocalDate> dataEliminazione) {
        String statement = "SELECT * FROM galleria.FOTOGRAFIA";

        try(PreparedStatement query = connection.prepareStatement(statement)) {

            ResultSet resultSet = query.executeQuery();

            while(resultSet.next()) {

                idFoto.add(resultSet.getInt("IDFoto"));
                device.add(resultSet.getString("Dispositivo"));
                autore.add(resultSet.getInt("Autore"));
                if (resultSet.getString("Coordinate") != null) {
                    coordinate.add(resultSet.getString("Coordinate"));
                } else {
                    coordinate.add(null);
                }

                visibilita.add(resultSet.getBoolean("Visibilita"));
                dataDiScatto.add(resultSet.getDate("DataScatto").toLocalDate());

                if (resultSet.getDate("DataEliminazione") != null) {
                    dataEliminazione.add(resultSet.getDate("DataEliminazione").toLocalDate());
                } else {
                    dataEliminazione.add(null);
                }

            }
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    /**
     * Recupera l'associazione tra fotografie e il luogo di scatto (relazione 'Raffigura').
     */
    public void getRaffigura(ArrayList<Integer> idFoto, ArrayList<String> luogo){

        String statement = "SELECT IDFOTO, COORDINATE FROM galleria.FOTOGRAFIA";

        try(PreparedStatement query = connection.prepareStatement(statement)) {
            ResultSet resultSet = query.executeQuery();

            while(resultSet.next()) {

                idFoto.add(resultSet.getInt("IDFoto"));
                luogo.add(resultSet.getString("Coordinate"));
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }

    }

    /**
     * Recupera l'associazione tra fotografie e il relativo autore (relazione 'Scatta').
     */
    public void getScatta(ArrayList<Integer> idFoto, ArrayList<Integer> autore) {
        String statement = "SELECT IDFOTO, AUTORE FROM galleria.FOTOGRAFIA";

        try(PreparedStatement query = connection.prepareStatement(statement)) {
            ResultSet resultSet = query.executeQuery();

            while(resultSet.next()) {
                idFoto.add(resultSet.getInt("IDFoto"));
                autore.add(resultSet.getInt("AUTORE"));
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
