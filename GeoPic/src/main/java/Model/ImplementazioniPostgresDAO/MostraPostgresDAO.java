package Model.ImplementazioniPostgresDAO;

import Model.DAO.MostraDAO;

import java.sql.*;
import java.util.ArrayList;

public class MostraPostgresDAO implements MostraDAO {
    Connection connection;

    public MostraPostgresDAO(Connection connection) {
        this.connection = connection;
    }
    @Override
    public void insertSoggettoInFoto(String nomeSoggetto, String IDFoto) {
        String statement = "INSERT INTO galleria.MOSTRA VALUES(?,?)";

        try(PreparedStatement aggiuntaSoggettoAFoto = connection.prepareStatement(statement)) {

            aggiuntaSoggettoAFoto.setString(1, nomeSoggetto);
            aggiuntaSoggettoAFoto.setString(2, IDFoto);

            aggiuntaSoggettoAFoto.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    @Override
    public void deleteSoggettoDaFoto(String nomeSoggetto, String IDFoto) {
        String statement = "INSERT INTO galleria.MOSTRA VALUES(?,?)";

        try(PreparedStatement aggiuntaSoggettoAFoto = connection.prepareStatement(statement)) {

            aggiuntaSoggettoAFoto.setString(1, nomeSoggetto);
            aggiuntaSoggettoAFoto.setString(2, IDFoto);

            aggiuntaSoggettoAFoto.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    public void getAlSoggettiMostrati(ArrayList<String> nomeSoggetto, ArrayList<String> idFoto){
        String statement = "SELECT * FROM galleria.MOSTRA";

        try(PreparedStatement query = connection.prepareStatement(statement)){

            ResultSet rs = query.executeQuery();

            while(rs.next()){
                nomeSoggetto.add(rs.getString("nomeSoggetto"));
                idFoto.add(rs.getString("IDFoto"));
            }


        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
