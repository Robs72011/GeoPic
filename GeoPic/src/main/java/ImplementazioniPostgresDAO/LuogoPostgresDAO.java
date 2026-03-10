package ImplementazioniPostgresDAO;

import DAO.LuogoDAO;

import java.sql.*;
import java.util.ArrayList;

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
    public void deleteLuogo(String coordinate) {
        String statement = "DELETE FROM galleria.LUOGO WHERE coordinate = ?";

        try(PreparedStatement rimozioneLuogo = connection.prepareStatement(statement)){

            rimozioneLuogo.setString(1, coordinate);

            rimozioneLuogo.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    public void getAllLuoghi(ArrayList<String> coordinate, ArrayList<String> toponimo){
        String statement = "SELECT * FROM galleria.LUOGO";

        try(PreparedStatement query = connection.prepareStatement(statement)){

            ResultSet resultSet = query.executeQuery();

            while(resultSet.next()){
                coordinate.add(resultSet.getString("coordinate"));
                toponimo.add(resultSet.getString("toponimo"));
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }

    }
}
