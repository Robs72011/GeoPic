package ImplementazioniPostgresDAO;

import DAO.FotografiaDAO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class FotografiaPostgresDAO implements FotografiaDAO {
    private final Connection connection;

    public FotografiaPostgresDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insertFotografia(String idFoto, String device, LocalDate dataScatto, LocalDate dataEliminazione,
                          boolean visibilita, String coordinate, String autore){
        String statement = "INSERT INTO galleria.FOTOGRAFIA VALUES(?, ?, ?, ?, ?, ?, ?)";

        try{
            PreparedStatement newFoto = connection.prepareStatement(statement);
            newFoto.setString(1, idFoto);
            newFoto.setString(2, device);
            newFoto.setDate(3, Date.valueOf(dataScatto));

            if(dataEliminazione == null){
                newFoto.setDate(4, null);
            }else{
                newFoto.setDate(4, Date.valueOf(dataEliminazione));
            }

            newFoto.setBoolean(5, visibilita);
            newFoto.setString(6, coordinate);
            newFoto.setString(7, autore);

            newFoto.executeUpdate();
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    @Override
    public void deleteFotografia(String IDFoto) {
        String statement = "DELETE FROM galleria.FOTOGRAFIA WHERE IDFoto = ?";

        try{
            PreparedStatement deleteFoto = connection.prepareStatement(statement);

            deleteFoto.setString(1, IDFoto);

            deleteFoto.executeUpdate();
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    @Override
    public void updateVisibilita(String IDFoto, boolean visibilita) {
        String statement = "UPDATE galleria.FOTOGRAFIA SET Visibilita = ? WHERE IDFoto = ?";

        try{
            PreparedStatement updateVisibilita = connection.prepareStatement(statement);

            updateVisibilita.setBoolean(1, visibilita);
            updateVisibilita.setString(2, IDFoto);

            updateVisibilita.executeUpdate();
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    @Override
    public void updateDataEliminazione(String IDFoto, LocalDate newDataEliminazione) {
        String statement = "UPDATE galleria.FOTOGRAFIA SET dataEliminazione = ? WHERE IDFoto = ?";

        try(PreparedStatement updateDataEliminazione = connection.prepareStatement(statement)){

            updateDataEliminazione.setDate(1, Date.valueOf(newDataEliminazione));
            updateDataEliminazione.setString(2, IDFoto);

            updateDataEliminazione.executeUpdate();
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    public void getAllFotografie(ArrayList<String> idFoto, ArrayList<String> device, ArrayList<String> autore,
                                 ArrayList<String> coordinate, ArrayList<Boolean> visibilita,
                                 ArrayList<LocalDate> dataDiScatto, ArrayList<LocalDate> dataEliminazione) {
        String statement = "SELECT * FROM galleria.FOTOGRAFIA";

        try(PreparedStatement query = connection.prepareStatement(statement)) {

            ResultSet resultSet = query.executeQuery();

            while(resultSet.next()) {

                idFoto.add(resultSet.getString("IDFoto"));
                device.add(resultSet.getString("device"));
                autore.add(resultSet.getString("autore"));
                if (resultSet.getString("coordinate") != null) {
                    coordinate.add(resultSet.getString("coordinate"));
                } else {
                    coordinate = null;
                }

                visibilita.add(resultSet.getBoolean("visibilita"));
                dataDiScatto.add(resultSet.getDate("DataScatto").toLocalDate());

                if (resultSet.getString("DataEliminazione") != null) {
                    dataEliminazione.add(resultSet.getDate("DataEliminazione").toLocalDate());
                } else {
                    dataEliminazione = null;
                }

            }
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    public void getScatta(ArrayList<String> idFoto, ArrayList<String> autore) {
        String statement = "SELECT IDFOTO, AUTORE FROM galleria.FOTOGRAFIA";

        try(PreparedStatement query = connection.prepareStatement(statement)) {
            ResultSet resultSet = query.executeQuery();

            while(resultSet.next()) {
                idFoto.add(resultSet.getString("IDFoto"));
                autore.add(resultSet.getString("AUTORE"));
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
