package Model.ImplementazioniPostgresDAO;

import Model.DAO.LuogoDAO;

import java.sql.*;

public class LuogoPostgresDAO implements LuogoDAO {
    Connection connection;

    public LuogoPostgresDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insertLuogo(String coordinate, String toponimo) {
        String statement = "INSERT INTO galleria.LUOGO VALUES(?,?)";

        try(PreparedStatement aggiuntaLuogo = connection.prepareStatement(statement)){

            aggiuntaLuogo.setString(1, coordinate);
            aggiuntaLuogo.setString(2, toponimo);

            aggiuntaLuogo.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    @Override
    public void insertLuogo(String coordinate) {
        String statement = "DELETE FROM galleria.LUOGO WHERE coordinate = ?";

        try(PreparedStatement rimozioneLuogo = connection.prepareStatement(statement)){

            rimozioneLuogo.setString(1, coordinate);

            rimozioneLuogo.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
