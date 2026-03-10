package ImplementazioniPostgresDAO;

import DAO.MostraDAO;

import java.sql.*;
import java.util.ArrayList;

public class MostraPostgresDAO implements MostraDAO {
    Connection connection;

    public MostraPostgresDAO(Connection connection) {
        this.connection = connection;
    }
    @Override
    public void insertSoggettoInFoto(String nomeSoggetto, int IDFoto) {
        String statement = "INSERT INTO galleria.MOSTRA VALUES(?,?)";

        try(PreparedStatement aggiuntaSoggettoAFoto = connection.prepareStatement(statement)) {

            aggiuntaSoggettoAFoto.setString(1, nomeSoggetto);
            aggiuntaSoggettoAFoto.setInt(2, IDFoto);

            aggiuntaSoggettoAFoto.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    @Override
    public void deleteSoggettoDaFoto(String nomeSoggetto, int IDFoto) {
        String statement = "DELETE FROM galleria.MOSTRA WHERE nomeSoggetto LIKE ? AND IDFoto = ?";

        try(PreparedStatement aggiuntaSoggettoAFoto = connection.prepareStatement(statement)) {

            aggiuntaSoggettoAFoto.setString(1, nomeSoggetto);
            aggiuntaSoggettoAFoto.setInt(2, IDFoto);

            aggiuntaSoggettoAFoto.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    public void getAllSoggettiMostrati(ArrayList<String> nomeSoggetto, ArrayList<Integer> idFoto){
        String statement = "SELECT * FROM galleria.MOSTRA";

        try(PreparedStatement query = connection.prepareStatement(statement)){

            ResultSet rs = query.executeQuery();

            while(rs.next()){
                nomeSoggetto.add(rs.getString("nomeSoggetto"));
                idFoto.add(rs.getInt("IDFoto"));
            }


        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
