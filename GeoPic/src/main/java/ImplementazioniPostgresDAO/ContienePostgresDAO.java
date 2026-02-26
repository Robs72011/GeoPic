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
    public void insertFotoAGalleria(String IDGalleria, String IDFoto) {
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
    public void deleteFotoDaGalleria(String IDGalleria, String IDFoto) {
        String statement = "SELECT * FROM galleria.CONTIENE";

        try(PreparedStatement newFotoInGalleria = connection.prepareStatement(statement)){

            newFotoInGalleria.setString(1, IDGalleria);
            newFotoInGalleria.setString(2, IDFoto);

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    public void getAllContenute(ArrayList<String>idGalleria, ArrayList<String>idFoto){
        String statement = "SELECT * FROM galleria.CONTIENE";

        try(PreparedStatement newFotoInGalleria = connection.prepareStatement(statement)){

            newFotoInGalleria.setString(1, "IDGalleria");
            newFotoInGalleria.setString(2, "IDFoto");

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
