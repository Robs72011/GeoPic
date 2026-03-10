package ImplementazioniPostgresDAO;

import DAO.ComponeDAO;

import java.sql.*;
import java.util.ArrayList;

public class ComponePostgresDAO implements ComponeDAO {
    private Connection connection;

    public ComponePostgresDAO(Connection connection) {
        this.connection = connection;
    }
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
