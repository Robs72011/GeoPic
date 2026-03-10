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
    public void insertVideo(String titoloVideo, String descrizione, int galleria) {
        String statement = "INSERT INTO galleria.VIDEO (TitoloVideo, Descrizione, Galleria) VALUES (?, ?, ?)";

        try(PreparedStatement aggiuntaVideo = connection.prepareStatement(statement)){

            aggiuntaVideo.setString(1, titoloVideo);
            aggiuntaVideo.setString(2, descrizione);
            aggiuntaVideo.setInt(3, galleria);

            aggiuntaVideo.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

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
