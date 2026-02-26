package Model.ImplementazioniPostgresDAO;

import Model.DAO.PartecipaDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.function.Predicate;

public class PartecipaPostgresDAO implements PartecipaDAO {
    Connection connection;
    public PartecipaPostgresDAO(Connection connection){
        this.connection = connection;
    }

    @Override
    public void insertPartecipante(String idGalleria, String idUtente) {
        String statement = "INSERT INTO galleria.PARTECIPA VALUES(?,?)";

        try(PreparedStatement aggiuntaPartecipanteAGalleria = connection.prepareStatement(statement)){

            aggiuntaPartecipanteAGalleria.setString(1, idGalleria);
            aggiuntaPartecipanteAGalleria.setString(2, idUtente);

            aggiuntaPartecipanteAGalleria.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    @Override
    public void deletePartecipante(String idGalleria, String idUtente) {
        String statement = "DELETE FROM galleria.PARTECIPA WHERE IDGalleria LIKE ? AND IDUtente LIKE ?";

        try(PreparedStatement rimozioneUtenteDaGalleriaCondivisa = connection.prepareStatement(statement)){

            rimozioneUtenteDaGalleriaCondivisa.setString(1, idGalleria);
            rimozioneUtenteDaGalleriaCondivisa.setString(2, idUtente);

            rimozioneUtenteDaGalleriaCondivisa.executeUpdate();
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    public void getAllPartecipanti(ArrayList<String> idGalleria, ArrayList<String> idUtente){
        String statement = "SELECT * FROM galleria.PARTECIPA";

        try(PreparedStatement query = connection.prepareStatement(statement)) {

            ResultSet resultSet = query.executeQuery();

            while(resultSet.next()){
                idGalleria.add(resultSet.getString("IDGalleria"));
                idUtente.add(resultSet.getString("IDUtente"));
            }

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
