package Model.ImplementazioniPostgresDAO;

import Model.DAO.SoggettoDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SoggettoPostgresDAO implements SoggettoDAO {
    Connection connection;

    public SoggettoPostgresDAO(Connection connection){
        this.connection =  connection;
    }

    @Override
    public void addSoggetto(String nomeSoggetto, String categoria) {
        String statement = "INSERT INTO galleria.SOGGETTO VALUES(?, ?, ?)";

        try(PreparedStatement aggiuntaSoggetto = connection.prepareStatement(statement)){

            aggiuntaSoggetto.setString(1, nomeSoggetto);
            aggiuntaSoggetto.setString(2, categoria);
            aggiuntaSoggetto.setString(3, null);

            aggiuntaSoggetto.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    @Override
    public void removeSoggetto(String nomeSoggetto) {
        String statement = "DELETE FROM galleria.SOGGETTO WHERE NomeSoggetto LIKE ?";

        try(PreparedStatement rimozioneSoggetto = connection.prepareStatement(statement)){

            rimozioneSoggetto.setString(1, nomeSoggetto);

            rimozioneSoggetto.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    @Override
    public void addUtenteSoggetto(String nomeUtente, String categoria, String idUtente) {
        String statement = "INSERT INTO galleria.SOGGETTO VALUES(?, ?, ?)";

        try(PreparedStatement aggiuntaUtenteComeSoggetto = connection.prepareStatement(statement)){

            aggiuntaUtenteComeSoggetto.setString(1, nomeUtente);
            aggiuntaUtenteComeSoggetto.setString(2, categoria);
            aggiuntaUtenteComeSoggetto.setString(3, null);

            aggiuntaUtenteComeSoggetto.executeUpdate();
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    @Override
    public void removeUtenteSoggetto(String nomeUtente) {
        String statement = "DELETE FROM galleria.SOGGETTO WHERE  NomeSoggetto LIKE ?";

        try(PreparedStatement rimozioneSoggettoConUtente = connection.prepareStatement(statement)){

            rimozioneSoggettoConUtente.setString(1, nomeUtente);

            rimozioneSoggettoConUtente.executeUpdate();
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
