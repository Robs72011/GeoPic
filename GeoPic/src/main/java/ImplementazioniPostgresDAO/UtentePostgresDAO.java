package ImplementazioniPostgresDAO;

import DAO.UtenteDAO;
import java.sql.*;
import java.util.ArrayList;

public class UtentePostgresDAO implements UtenteDAO {
    Connection connection;

    public  UtentePostgresDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insertUtente(String idUtente, String username, String password, boolean isAdmin, boolean isSoggetto){
        String statement = "INSERT INTO galleria.UTENTE VALUES(?, ?, ?, ?, ?)";

        try(PreparedStatement aggiuntaUtente = connection.prepareStatement(statement)) {

            aggiuntaUtente.setString(1, idUtente);
            aggiuntaUtente.setString(2, username);
            aggiuntaUtente.setString(3, password);
            aggiuntaUtente.setBoolean(4, isAdmin);
            aggiuntaUtente.setBoolean(5, isSoggetto);

            aggiuntaUtente.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    @Override
    public void deleteUtente(String idUtente) {
        String statement = "DELETE FROM .UTENTE WHERE IDUtente LIKE ?";

        try(PreparedStatement rimozioneUtente = connection.prepareStatement(statement)){

            rimozioneUtente.setString(1, idUtente);

            rimozioneUtente.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }

    }

    //Per recuperare tutti gli utenti dal db
    public void getAllUtenti(ArrayList<String> idUtente, ArrayList<String> username, ArrayList<String> password,
                             ArrayList<Boolean> isAdmin, ArrayList<Boolean> isSoggetto){
        String statement = "SELECT * FROM galleria.UTENTE";

        try(PreparedStatement query = connection.prepareStatement(statement)){

            ResultSet rs = query.executeQuery();

            while(rs.next()){
                idUtente.add(rs.getString("idUtente"));
                username.add(rs.getString("username"));
                password.add(rs.getString("password"));
                isAdmin.add(rs.getBoolean("isAdmin"));
                isSoggetto.add(rs.getBoolean("isSoggetto"));
            }


        }catch (SQLException sqle){
            sqle.printStackTrace();
        }

    }

    //Per recuperare un solo utente dal DB, quello loggato
    //Al metodo vengono passati username e password gia inizializzati dal tentativo di login
    public void getLoggedInUtente(String idUtente, String username, String password,
                                  Boolean isAdmin, Boolean isSoggetto){
        String statement = "SELECT * FROM galleria.UTENTE WHERE usernam LIKE ? AND password LIKE ?";

        try(PreparedStatement query = connection.prepareStatement(statement)){

            query.setString(1, username);
            query.setString(2, password);

            ResultSet rs = query.executeQuery();

            while(rs.next()){
                idUtente =  rs.getString("idUtente");
                isAdmin = rs.getBoolean("isAdmin");
                isSoggetto = rs.getBoolean("isSoggetto");
            }


        }catch (SQLException sqle){
            sqle.printStackTrace();
        }

    }

}
