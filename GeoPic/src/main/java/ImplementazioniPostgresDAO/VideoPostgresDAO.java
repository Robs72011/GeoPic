package ImplementazioniPostgresDAO;

import DAO.VideoDAO;

import java.sql.*;
import java.util.ArrayList;

/**
 * Implementazione PostgreSQL per l'interfaccia {@link VideoDAO}.
 * Gestisce le operazioni di persistenza, rimozione e recupero dei dati
 * relativi ai video caricati nel sistema.
 */
public class VideoPostgresDAO implements VideoDAO {
    Connection connection;

    /**
     * Costruisce l'implementazione DAO con la connessione fornita.
     * @param connection La connessione attiva al database.
     */
    public VideoPostgresDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Inserisce un nuovo video nel database.
     * @param titoloVideo Il titolo assegnato al video.
     * @param descrizione La descrizione testuale del contenuto.
     * @param galleria L'ID della galleria di appartenenza.
     * @return L'id del video appena inserito
     */
    @Override
    public Integer insertVideo(String titoloVideo, String descrizione, int galleria) {
        String statement = "INSERT INTO galleria.VIDEO (TitoloVideo, Descrizione, Galleria) VALUES (?, ?, ?) RETURNING IDVideo";

        try(PreparedStatement aggiuntaVideo = connection.prepareStatement(statement)){

            aggiuntaVideo.setString(1, titoloVideo);
            aggiuntaVideo.setString(2, descrizione);
            aggiuntaVideo.setInt(3, galleria);

            try(ResultSet rs = aggiuntaVideo.executeQuery()){
                if(rs.next()){
                    return rs.getInt("IDVideo");
                }
            }
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }

        return null;
    }

    /**
     * Elimina un video dal database in base al suo identificativo.
     * @param idVideo L'ID del video da rimuovere.
     */
    @Override
    public void deleteVideo(int idVideo) {
        String statement = "DELETE FROM galleria.VIDEO WHERE IDVideo = ?";

        try(PreparedStatement rimozioneVideo = connection.prepareStatement(statement)){

            rimozioneVideo.setInt(1, idVideo);

            rimozioneVideo.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    /**
     * Recupera l'elenco di tutti i video presenti, popolando le liste fornite.
     */
    @Override
    public void getAllVideo(ArrayList<Integer> idVideo, ArrayList<String> titoloVideo, ArrayList<String> descrizione,
                            ArrayList<Integer> galleria) {

        String statement = "SELECT * FROM galleria.VIDEO";

        try(PreparedStatement query = connection.prepareStatement(statement)){

            ResultSet rs = query.executeQuery();

            while(rs.next()){
                idVideo.add(rs.getInt("IDVideo"));
                titoloVideo.add(rs.getString("TitoloVideo"));

                if(rs.getString("descrizione") != null){
                    descrizione.add(rs.getString("Descrizione"));
                }else{
                    descrizione.add(null);
                }

                galleria.add(rs.getInt("Galleria"));
            }

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }

    }

    /**
     * Recupera le associazioni tra video e gallerie ("Salvato in").
     * @param idVideo Lista in cui saranno inseriti gli ID dei video.
     * @param idGalleria Lista in cui saranno inseriti gli ID delle gallerie.
     */
    @Override
    public void getSalvatoIn(ArrayList<Integer> idVideo, ArrayList<Integer> idGalleria) {
        String statement = "SELECT idvideo, galleria FROM galleria.VIDEO";

        try(PreparedStatement query = connection.prepareStatement(statement)){
            ResultSet rs = query.executeQuery();

            while(rs.next()){
                idVideo.add(rs.getInt("idvideo"));
                idGalleria.add(rs.getInt("galleria"));
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
