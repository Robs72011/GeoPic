package ImplementazioniPostgresDAO;

import DAO.PartecipaDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PartecipaPostgresDAO implements PartecipaDAO {
    Connection connection;
    public PartecipaPostgresDAO(Connection connection){
        this.connection = connection;
    }

    @Override
    public void insertPartecipante(int idGalleria, int idUtente) {
        String statement = "INSERT INTO galleria.PARTECIPA VALUES(?,?)";

        try(PreparedStatement aggiuntaPartecipanteAGalleria = connection.prepareStatement(statement)){

            aggiuntaPartecipanteAGalleria.setInt(1, idGalleria);
            aggiuntaPartecipanteAGalleria.setInt(2, idUtente);

            aggiuntaPartecipanteAGalleria.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    @Override
    public void deletePartecipante(int idGalleria, int idUtente) {
        String statement = "DELETE FROM galleria.PARTECIPA WHERE IDGalleria = ? AND IDUtente = ?";

        try(PreparedStatement rimozioneUtenteDaGalleriaCondivisa = connection.prepareStatement(statement)){

            rimozioneUtenteDaGalleriaCondivisa.setInt(1, idGalleria);
            rimozioneUtenteDaGalleriaCondivisa.setInt(2, idUtente);

            rimozioneUtenteDaGalleriaCondivisa.executeUpdate();
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    public void getAllPartecipanti(ArrayList<Integer> idGalleria, ArrayList<Integer> idUtente){
        String statement = "SELECT * FROM galleria.PARTECIPA";

        try(PreparedStatement query = connection.prepareStatement(statement)) {

            ResultSet resultSet = query.executeQuery();

            while(resultSet.next()){
                idGalleria.add(resultSet.getInt("IDGalleria"));
                idUtente.add(resultSet.getInt("IDUtente"));
            }

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
