package ImplementazioniPostgresDAO;

import DAO.VideoDAO;

import java.sql.*;
import java.util.ArrayList;

public class VideoPostgresDAO implements VideoDAO {
    Connection connection;

    public VideoPostgresDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insertVideo(String idVideo, String titoloVideo, String descrizione, String galleria) {
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
    public void deleteVideo(String idVideo) {
        String statement = "DELETE FROM galleria.VIDEO WHERE IDVideo LIKE ?";

        try(PreparedStatement rimozioneVideo = connection.prepareStatement(statement)){

            rimozioneVideo.setString(1, idVideo);

            rimozioneVideo.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    public void getAllVideo(ArrayList<String> idVideo, ArrayList<String> titoloVideo, ArrayList<String> descrizione,
                            ArrayList<String> galleria) {

        String statement = "SELECT * FROM galleria.VIDEO";

        try(PreparedStatement query = connection.prepareStatement(statement)){

            ResultSet rs = query.executeQuery();

            while(rs.next()){
                idVideo.add(rs.getString("IDVideo"));
                titoloVideo.add(rs.getString("TitoloVideo"));

                if(rs.getString("descrizione") != null){
                    descrizione.add(rs.getString("Descrizione"));
                }else{
                    descrizione.add(null);
                }

                galleria.add(rs.getString("Galleria"));
            }

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }

    }
}
