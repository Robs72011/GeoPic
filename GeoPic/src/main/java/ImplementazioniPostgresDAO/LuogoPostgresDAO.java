package ImplementazioniPostgresDAO;

import DAO.LuogoDAO;

import java.sql.*;
import java.util.ArrayList;

/**
 * Implementazione PostgreSQL per l'interfaccia {@link LuogoDAO}.
 * Gestisce la persistenza e il recupero dei dati geografici {@link Model.Luogo},
 * utilizzando le coordinate come identificativo univoco.
 */
public class LuogoPostgresDAO implements LuogoDAO {
    Connection connection;

    /**
     * Costruisce l'implementazione DAO con la connessione fornita.
     * @param connection La connessione attiva al database.
     */
    public LuogoPostgresDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Inserisce un nuovo luogo nel sistema.
     * @param coordinate Le coordinate geografiche (chiave primaria).
     * @param toponimo Il nome del luogo.
     */
    @Override
    public void insertLuogo(String coordinate, String toponimo) {
        String statement = "INSERT INTO galleria.LUOGO VALUES(?,?)";

        try(PreparedStatement aggiuntaLuogo = connection.prepareStatement(statement)){

            aggiuntaLuogo.setString(1, coordinate);
            aggiuntaLuogo.setString(2, toponimo);

            aggiuntaLuogo.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    /**
     * Rimuove un luogo dal sistema in base alle sue coordinate.
     * @param coordinate Le coordinate del luogo da eliminare.
     */
    @Override
    public void deleteLuogo(String coordinate) {
        String statement = "DELETE FROM galleria.LUOGO WHERE coordinate = ?";

        try(PreparedStatement rimozioneLuogo = connection.prepareStatement(statement)){

            rimozioneLuogo.setString(1, coordinate);

            rimozioneLuogo.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    /**
     * Recupera l'elenco di tutti i luoghi censiti, popolando le liste passate come parametro.
     */
    public void getAllLuoghi(ArrayList<String> coordinate, ArrayList<String> toponimo){
        String statement = "SELECT * FROM galleria.LUOGO";

        try(PreparedStatement query = connection.prepareStatement(statement)){

            ResultSet resultSet = query.executeQuery();

            while(resultSet.next()){
                coordinate.add(resultSet.getString("coordinate"));
                toponimo.add(resultSet.getString("toponimo"));
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }

    }
}
