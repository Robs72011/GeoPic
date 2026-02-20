package Model.ImplementazioniPostgresDAO;

import Model.DAO.ContieneDAO;
import java.sql.*;

public class ContienePostgresDAO implements ContieneDAO {
    private Connection connection;

    public ContienePostgresDAO(Connection connection) {
        this.connection = connection;
    }
    @Override
    public void addFotoAGalleria(String IDGalleria, String IDFoto) {
        String statement = "INSERT INTO galleria.COMPONE VALUES (?,?)";

        try(PreparedStatement newFotoInGalleria = connection.prepareStatement(statement)){

            newFotoInGalleria.setString(1, IDGalleria);
            newFotoInGalleria.setString(2, IDFoto);

            newFotoInGalleria.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    @Override
    public void removeFotoDAGalleria(String IDGalleria, String IDFoto) {
        String statement = "DELETE FROM galleria.CONTIENE WHERE IDGalleria LIKE ? AND IDFoto LIKE ?";

        try(PreparedStatement newFotoInGalleria = connection.prepareStatement(statement)){

            newFotoInGalleria.setString(1, IDGalleria);
            newFotoInGalleria.setString(2, IDFoto);

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
