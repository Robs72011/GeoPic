package Model.ImplementazioniPostgresDAO;

import Model.DAO.GalleriaDAO;

import java.sql.*;

public class GalleriaPostgresDAO implements GalleriaDAO {
    private final Connection connection;

    public GalleriaPostgresDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addGalleria(String idGalleria, String nomeGalleria, boolean condivisione, String proprietario) {
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
    public void removeGalleria(String idGalleria) {
        String statement = "DELETE FROM galleria.GALLERIA WHERE IDGalleria = ?";

        try(PreparedStatement rimozioneGalleria = connection.prepareStatement(statement)) {

            rimozioneGalleria.setString(1, idGalleria);

            rimozioneGalleria.executeUpdate();

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
