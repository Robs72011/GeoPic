package ImplementazioniPostgresDAO;

import DAO.GalleriaDAO;

import java.sql.*;
import java.util.ArrayList;

public class GalleriaPostgresDAO implements GalleriaDAO {
    private final Connection connection;

    public GalleriaPostgresDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insertGalleria(String idGalleria, String nomeGalleria, boolean condivisione, String proprietario) {
        String statement = "INSERT INTO galleria.GALLERIA VALUES(?, ?, ?, ?)";

        try(PreparedStatement aggiuntaGalleria = connection.prepareStatement(statement)) {

            aggiuntaGalleria.setString(1, idGalleria);
            aggiuntaGalleria.setString(2, nomeGalleria);
            aggiuntaGalleria.setBoolean(3, condivisione);
            aggiuntaGalleria.setString(4, proprietario);

            aggiuntaGalleria.executeUpdate();
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    @Override
    public void deleteGalleria(String idGalleria) {
        String statement = "DELETE FROM galleria.GALLERIA WHERE IDGalleria = ?";

        try(PreparedStatement rimozioneGalleria = connection.prepareStatement(statement)) {

            rimozioneGalleria.setString(1, idGalleria);

            rimozioneGalleria.executeUpdate();

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
    }

    public void getAllGallerie(ArrayList<String> idGalleria, ArrayList<String> nomeGalleria,
                               ArrayList<Boolean> condivisa, ArrayList<String> proprietario) {

        String statement = "SELECT * FROM galleria.GALLERIA";

        try(PreparedStatement query = connection.prepareStatement(statement)) {

            ResultSet resultSet = query.executeQuery();

            while(resultSet.next()){
                idGalleria.add(resultSet.getString("IDGalleria"));
                nomeGalleria.add(resultSet.getString("nomeGalleria"));
                condivisa.add(resultSet.getBoolean("condivisione"));
                proprietario.add(resultSet.getString("proprietario"));
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }

    }
}
