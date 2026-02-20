package Model.ImplementazioniPostgresDAO;

import Model.DAO.UtenteDAO;
import java.sql.*;

public class UtentePostgresDAO implements UtenteDAO {
    Connection connection;

    public  UtentePostgresDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addUtente(String idUtente, String nome, boolean isAdmin) {
        String statement = "INSERT INTO galleria.UTENTE VALUES(?, ?, ?)";

        try(PreparedStatement aggiuntaUtente = connection.prepareStatement(statement)) {

            aggiuntaUtente.setString(1, idUtente);
            aggiuntaUtente.setString(2, nome);
            aggiuntaUtente.setBoolean(3, isAdmin);

            aggiuntaUtente.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    @Override
    public void removeUtente(String idUtente) {
        String statement = "DELETE FROM .UTENTE WHERE IDUtente LIKE ?";

        try(PreparedStatement rimozioneUtente = connection.prepareStatement(statement)){

            rimozioneUtente.setString(1, idUtente);

            rimozioneUtente.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }

    }
}
