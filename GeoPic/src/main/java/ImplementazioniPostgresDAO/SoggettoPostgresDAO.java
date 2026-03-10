package ImplementazioniPostgresDAO;

import DAO.SoggettoDAO;

import java.sql.*;
import java.util.ArrayList;

public class SoggettoPostgresDAO implements SoggettoDAO {
    Connection connection;

    public SoggettoPostgresDAO(Connection connection){
        this.connection =  connection;
    }

    @Override
    public void insertSoggetto(String nomeSoggetto, String categoria) {
        String statement = "INSERT INTO galleria.SOGGETTO VALUES(?, ?, ?)";

        try(PreparedStatement aggiuntaSoggetto = connection.prepareStatement(statement)){

            aggiuntaSoggetto.setString(1, nomeSoggetto);
            aggiuntaSoggetto.setString(2, categoria);
            aggiuntaSoggetto.setNull(3, Types.INTEGER);

            aggiuntaSoggetto.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    @Override
    public void deleteSoggetto(String nomeSoggetto) {
        String statement = "DELETE FROM galleria.SOGGETTO WHERE NomeSoggetto LIKE ?";

        try(PreparedStatement rimozioneSoggetto = connection.prepareStatement(statement)){

            rimozioneSoggetto.setString(1, nomeSoggetto);

            rimozioneSoggetto.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    @Override
    public void insertUtenteAsSoggetto(String nomeUtente, String categoria, int idUtente) {
        String statement = "INSERT INTO galleria.SOGGETTO VALUES(?, ?, ?)";

        try(PreparedStatement aggiuntaUtenteComeSoggetto = connection.prepareStatement(statement)){

            aggiuntaUtenteComeSoggetto.setString(1, nomeUtente);
            aggiuntaUtenteComeSoggetto.setString(2, categoria);
            aggiuntaUtenteComeSoggetto.setInt(3, idUtente);

            aggiuntaUtenteComeSoggetto.executeUpdate();
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    @Override
    public void deleteUtenteAsSoggetto(String nomeUtente) {
        String statement = "DELETE FROM galleria.SOGGETTO WHERE  NomeSoggetto = ?";

        try(PreparedStatement rimozioneSoggettoConUtente = connection.prepareStatement(statement)){

            rimozioneSoggettoConUtente.setString(1, nomeUtente);

            rimozioneSoggettoConUtente.executeUpdate();
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    public void getAllSoggetti(ArrayList<String> nomeSoggetto, ArrayList<String> categoria){
        String statement = "SELECT * FROM galleria.SOGGETTO";

        try(PreparedStatement query = connection.prepareStatement(statement)) {

            ResultSet resultSet = query.executeQuery();

            while(resultSet.next()){
                nomeSoggetto.add(resultSet.getString("NomeSoggetto"));
                categoria.add(resultSet.getString("Categoria"));
            }

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    public void getRappresenta(ArrayList<String> nomeSoggetto, ArrayList<Integer> utente){
        String statement = "SELECT NOMESOGGETTO, IDUTENTE FROM galleria.SOGGETTO";

        try(PreparedStatement query = connection.prepareStatement(statement)){
            ResultSet resultSet = query.executeQuery();

            while(resultSet.next()){
                nomeSoggetto.add(resultSet.getString("NomeSoggetto"));
                int u = resultSet.getInt("IDUTENTE");
                if(resultSet.wasNull()){
                    utente.add(null);
                }else{
                    utente.add(u);
                }
            }

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
