package ImplementazioniPostgresDAO;

import DAO.ComponeDAO;

import java.sql.*;
import java.util.ArrayList;

/**
 * Implementazione PostgreSQL per l'interfaccia {@link ComponeDAO}.
 * Gestisce le operazioni di persistenza per la relazione tra Video e Fotografia.
 */
public class ComponePostgresDAO implements ComponeDAO {
    private Connection connection;

    public ComponePostgresDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Inserisce una nuova associazione nel database.
     * @param IDVideo Identificativo del video.
     * @param IDFoto Identificativo della foto.
     */
    @Override
    public void insertComposizione(int IDVideo, int IDFoto) {
        String statement = "INSERT INTO galleria.COMPONE VALUES(?, ?)";

        try(PreparedStatement newComposizione = connection.prepareStatement(statement)){

            newComposizione.setInt(1, IDVideo);
            newComposizione.setInt(2, IDFoto);

            newComposizione.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    /**
     * Elimina l'associazione tra un video e una foto.
     * @param IDVideo Identificativo del video.
     * @param IDFoto Identificativo della foto.
     */
    @Override
    public void deleteComposione(int IDVideo, int IDFoto) {
        String statement = "DELETE FROM galleria.COMPONE WHERE IDVIDEO = ? AND IDFOTO = ?";

        try(PreparedStatement deleteComposizioneStatement = connection.prepareStatement(statement)){

            deleteComposizioneStatement.setInt(1, IDVideo);
            deleteComposizioneStatement.setInt(2, IDFoto);

            deleteComposizioneStatement.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    /**
     * Aggiorna una coppia IDVideo-IDFoto con nuovi valori.
     * @param oldIDVideo ID del video originale.
     * @param oldIDFoto ID della foto originale.
     * @param newIDVideo Nuovo ID del video.
     * @param newIDFoto Nuovo ID della foto.
     */
    @Override
    public void updateComposizione(int oldIDVideo, int oldIDFoto, int newIDVideo, int newIDFoto) {
        String statement = "UPDATE galleria.COMPONE SET IDVideo = ?, IDFoto = ? " +
                "WHERE IDVideo = ? AND IDFoto  = ?";

        try(PreparedStatement updateComposizioneStatement = connection.prepareStatement(statement)){

            updateComposizioneStatement.setInt(2, newIDFoto);
            updateComposizioneStatement.setInt(1, newIDVideo);
            updateComposizioneStatement.setInt(3, oldIDVideo);
            updateComposizioneStatement.setInt(4, oldIDFoto);

            updateComposizioneStatement.executeUpdate();
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
    }

    /**
     * Recupera tutte le associazioni dal database.
     * @param idVideo Lista popolata con gli ID dei video.
     * @param idFoto Lista popolata con gli ID delle foto.
     */
    @Override
    public void getAllComposizioni(ArrayList<Integer> idVideo, ArrayList<Integer> idFoto){
        String statement = "SELECT * FROM galleria.COMPONE";

        try(PreparedStatement query = connection.prepareStatement(statement)) {

            ResultSet resultSet = query.executeQuery();

            while(resultSet.next()) {

                idVideo.add(resultSet.getInt("IDVideo"));
                idFoto.add(resultSet.getInt("IDFoto"));

            }
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
