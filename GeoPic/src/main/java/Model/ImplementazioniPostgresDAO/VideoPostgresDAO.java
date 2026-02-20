package Model.ImplementazioniPostgresDAO;

import Model.DAO.VideoDAO;

import java.sql.*;

public class VideoPostgresDAO implements VideoDAO {
    Connection connection;

    public VideoPostgresDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void createVideo(String idVideo, String titoloVideo, String descrizione, String galleria) {
        String statement = "INSERT INTO galleria.VIDEO VALUES (?, ?, ?, ?)";

        try(PreparedStatement aggiuntaVideo = connection.prepareStatement(statement)){

            aggiuntaVideo.setString(1, idVideo);
            aggiuntaVideo.setString(2, titoloVideo);
            aggiuntaVideo.setString(3, descrizione);
            aggiuntaVideo.setString(4, galleria);

            aggiuntaVideo.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    @Override
    public void removeVideo(String idVideo) {
        String statement = "DELETE FROM galleria.VIDEO WHERE IDVideo LIKE ?";

        try(PreparedStatement rimozioneVideo = connection.prepareStatement(statement)){

            rimozioneVideo.setString(1, idVideo);

            rimozioneVideo.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
