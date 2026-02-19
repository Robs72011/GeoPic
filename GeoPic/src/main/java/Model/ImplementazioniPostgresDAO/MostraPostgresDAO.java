package Model.ImplementazioniPostgresDAO;

import Model.DAO.MostraDAO;

import java.sql.*;

public class MostraPostgresDAO implements MostraDAO {
    Connection connection;

    public MostraPostgresDAO(Connection connection) {
        this.connection = connection;
    }
    @Override
    public void addSoggettoAFoto(String nomeSoggetto, String IDFoto) {
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
    public void removeSoggettoDaFoto(String nomeSoggetto, String IDFoto) {
        String statement = "INSERT INTO galleria.MOSTRA VALUES(?,?)";

        try(PreparedStatement aggiuntaSoggettoAFoto = connection.prepareStatement(statement)) {

            aggiuntaSoggettoAFoto.setString(1, nomeSoggetto);
            aggiuntaSoggettoAFoto.setString(2, IDFoto);

            aggiuntaSoggettoAFoto.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
