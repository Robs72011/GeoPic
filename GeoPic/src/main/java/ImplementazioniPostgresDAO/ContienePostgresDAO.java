package ImplementazioniPostgresDAO;

import DAO.ContieneDAO;
import java.sql.*;
import java.util.ArrayList;

public class ContienePostgresDAO implements ContieneDAO {
    private Connection connection;

    public ContienePostgresDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insertFotoAGalleria(int IDGalleria, int IDFoto) {
        String statement = "INSERT INTO galleria.CONTIENE VALUES (?,?)";

        try(PreparedStatement newFotoInGalleria = connection.prepareStatement(statement)){

            newFotoInGalleria.setInt(1, IDGalleria);
            newFotoInGalleria.setInt(2, IDFoto);

            newFotoInGalleria.executeUpdate();
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    @Override
    public void deleteFotoDaGalleria(int IDGalleria, int IDFoto) {
        String statement = "DELETE FROM galleria.CONTIENE WHERE IDGALLERIA = ? AND IDFOTO = ?";

        try(PreparedStatement deleteFotoInGalleria = connection.prepareStatement(statement)){

            deleteFotoInGalleria.setInt(1, IDGalleria);
            deleteFotoInGalleria.setInt(2, IDFoto);

            deleteFotoInGalleria.executeUpdate();
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    public void getAllContenute(ArrayList<Integer> idGalleria, ArrayList<Integer>idFoto){
        String statement = "SELECT * FROM galleria.CONTIENE";

        try(PreparedStatement query = connection.prepareStatement(statement)) {

            ResultSet result = query.executeQuery();

            while (result.next()) {
                idGalleria.add(result.getInt("IDGALLERIA"));
                idFoto.add(result.getInt("IDFOTO"));
            }
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
