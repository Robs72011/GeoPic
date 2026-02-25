package Model.ImplementazioniPostgresDAO;

import Model.DAO.ComponeDAO;
import jdk.jshell.spi.SPIResolutionException;

import java.sql.*;
import java.util.ArrayList;

public class ComponePostgresDAO implements ComponeDAO {
    private Connection connection;

    public ComponePostgresDAO(Connection connection) {
        this.connection = connection;
    }
    @Override
    public void insertComposizione(String IDVideo, String IDFoto) {
        String statement = "INSERT INTO galleria.COMPONE VALUES(?,? )";

        try(PreparedStatement newComposizione = connection.prepareStatement(statement)){

            newComposizione.setString(1, IDVideo);
            newComposizione.setString(2, IDFoto);

            newComposizione.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    @Override
    public void deleteComposione(String IDVideo, String IDFoto) {
        String statement = "DELETE FROM galleria.COMPONE WHERE IDVIDEO LIKE ? AND IDFOTO LIKE ?";

        try(PreparedStatement deleteComposizioneStatement = connection.prepareStatement(statement)){

            deleteComposizioneStatement.setString(1, IDVideo);
            deleteComposizioneStatement.setString(2, IDFoto);

            deleteComposizioneStatement.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    @Override
    public void updateComposizione(String oldIDVideo, String oldIDFoto, String newIDVideo, String newIDFoto) {
        String statement = "UPDATE galleria.COMPONE SET IDVideo = ?, IDFoto = ? " +
                "WHERE IDVideo LIKE ? AND IDFoto  Like ?";

        try(PreparedStatement updateComposizioneStatement = connection.prepareStatement(statement)){

            updateComposizioneStatement.setString(1, newIDVideo);
            updateComposizioneStatement.setString(2, newIDFoto);
            updateComposizioneStatement.setString(3, oldIDVideo);
            updateComposizioneStatement.setString(4, oldIDFoto);

            updateComposizioneStatement.executeUpdate();

            connection.close();
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
    }

    public void getAllComposizioni(ArrayList<Integer> IdFoto, ArrayList<Integer> IdVideo){
        String query = "SELECT * FROM galleria.COMPONE";

        try(PreparedStatement getAllComposizioni = connection.prepareStatement(query)){

            ResultSet result = getAllComposizioni.executeQuery();

            while(result.next()){
                IdFoto.add(result.getInt("IDFOTO"));
                IdVideo.add(result.getInt("IDVIDEO"));
            }

            result.close();
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
