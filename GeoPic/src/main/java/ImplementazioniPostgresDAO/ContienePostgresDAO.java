package ImplementazioniPostgresDAO;

import DAO.ContieneDAO;
import java.sql.*;
import java.util.ArrayList;

/**
 * Implementazione PostgreSQL per l'interfaccia {@link ContieneDAO}.
 * Gestisce la persistenza dell'associazione tra le entità {@link Model.Galleria}
 * e {@link Model.Fotografia}.
 */
public class ContienePostgresDAO implements ContieneDAO {
    private Connection connection;

    /**
     * Costruisce l'implementazione DAO con la connessione specificata.
     * @param connection La connessione attiva al database.
     */
    public ContienePostgresDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Inserisce un'associazione tra una galleria e una foto nel database.
     * @param IDGalleria L'ID della galleria.
     * @param IDFoto L'ID della foto da inserire.
     */
    @Override
    public void insertFotoAGalleria(int IDGalleria, int IDFoto) {
        String statement = "INSERT INTO galleria.CONTIENE VALUES (?,?)";

        try(PreparedStatement newFotoInGalleria = connection.prepareStatement(statement)){

            newFotoInGalleria.setInt(1, IDGalleria);
            newFotoInGalleria.setInt(2, IDFoto);

            newFotoInGalleria.executeUpdate();
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    /**
     * Rimuove l'associazione tra una specifica galleria e una foto.
     * @param IDGalleria L'ID della galleria.
     * @param IDFoto L'ID della foto da rimuovere.
     */
    @Override
    public void deleteFotoDaGalleria(int IDGalleria, int IDFoto) {
        String statement = "DELETE FROM galleria.CONTIENE WHERE IDGALLERIA = ? AND IDFOTO = ?";

        try(PreparedStatement deleteFotoInGalleria = connection.prepareStatement(statement)){

            deleteFotoInGalleria.setInt(1, IDGalleria);
            deleteFotoInGalleria.setInt(2, IDFoto);

            deleteFotoInGalleria.executeUpdate();
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    /**
     * Recupera tutte le associazioni foto-galleria presenti nel sistema.
     * @param idGalleria Lista da popolare con gli ID delle gallerie.
     * @param idFoto Lista da popolare con gli ID delle foto corrispondenti.
     */
    public void getAllContenute(ArrayList<Integer> idGalleria, ArrayList<Integer>idFoto){
        String statement = "SELECT * FROM galleria.CONTIENE";

        try(PreparedStatement query = connection.prepareStatement(statement)) {

            ResultSet result = query.executeQuery();

            while (result.next()) {
                idGalleria.add(result.getInt("IDGALLERIA"));
                idFoto.add(result.getInt("IDFOTO"));
            }
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
