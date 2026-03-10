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
    public Integer insertGalleria(String nomeGalleria, boolean condivisione, int proprietario) {
        String statement = "INSERT INTO galleria.GALLERIA (NomeGalleria, Condivisa, Proprietario) VALUES(?, ?, ?)";

        // 1. Aggiungiamo il flag Statement.RETURN_GENERATED_KEYS
        try (PreparedStatement aggiuntaGalleria = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

            aggiuntaGalleria.setString(1, nomeGalleria);
            aggiuntaGalleria.setBoolean(2, condivisione);
            aggiuntaGalleria.setInt(3, proprietario);

            aggiuntaGalleria.executeUpdate();

            // 2. Recuperiamo l'ID generato dal database
            try (ResultSet rs = aggiuntaGalleria.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Restituisce l'ID appena creato
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return null; // Ritorna null se l'inserimento fallisce
    }

    @Override
    public void deleteGalleria(int idGalleria) {
        String statement = "DELETE FROM galleria.GALLERIA WHERE IDGalleria = ?";

        try(PreparedStatement rimozioneGalleria = connection.prepareStatement(statement)) {

            rimozioneGalleria.setInt(1, idGalleria);

            rimozioneGalleria.executeUpdate();

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
    }

    public void getAllGallerie(ArrayList<Integer> idGalleria, ArrayList<String> nomeGalleria,
                               ArrayList<Boolean> condivisa, ArrayList<Integer> proprietario) {

        String statement = "SELECT * FROM galleria.GALLERIA";

        try(PreparedStatement query = connection.prepareStatement(statement)) {

            ResultSet resultSet = query.executeQuery();

            while(resultSet.next()){
                idGalleria.add(resultSet.getInt("IDGalleria"));
                nomeGalleria.add(resultSet.getString("NomeGalleria"));
                condivisa.add(resultSet.getBoolean("Condivisa"));
                proprietario.add(resultSet.getInt("Proprietario"));
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }

    }

    @Override
    public void getOwner(ArrayList<Integer> idGalleria, ArrayList<Integer> proprietario, ArrayList<Boolean> condivisione) {
        String statement = "SELECT IDGalleria, Proprietario, Condivisa FROM galleria.GALLERIA";

        try(PreparedStatement query = connection.prepareStatement(statement)) {
            ResultSet resultSet = query.executeQuery();

            while(resultSet.next()) {
                idGalleria.add(resultSet.getInt("IDGalleria"));
                proprietario.add(resultSet.getInt("Proprietario"));
                condivisione.add(resultSet.getBoolean("Condivisa"));
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
