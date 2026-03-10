package ImplementazioniPostgresDAO;

import DAO.MostraDAO;

import java.sql.*;
import java.util.ArrayList;

/**
 * Implementazione PostgreSQL per l'interfaccia {@link MostraDAO}.
 * Gestisce la persistenza dell'associazione che identifica quali soggetti
 * sono presenti in determinate fotografie.
 */
public class MostraPostgresDAO implements MostraDAO {
    Connection connection;

    /**
     * Costruisce l'implementazione DAO con la connessione fornita.
     * @param connection La connessione attiva al database.
     */
    public MostraPostgresDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Associa un soggetto a una fotografia nel database.
     * @param nomeSoggetto Il nome del soggetto.
     * @param IDFoto L'ID della fotografia.
     */
    @Override
    public void insertSoggettoInFoto(String nomeSoggetto, int IDFoto) {
        String statement = "INSERT INTO galleria.MOSTRA VALUES(?,?)";

        try(PreparedStatement aggiuntaSoggettoAFoto = connection.prepareStatement(statement)) {

            aggiuntaSoggettoAFoto.setString(1, nomeSoggetto);
            aggiuntaSoggettoAFoto.setInt(2, IDFoto);

            aggiuntaSoggettoAFoto.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    /**
     * Rimuove l'associazione di un soggetto da una fotografia.
     * @param nomeSoggetto Il nome del soggetto.
     * @param IDFoto L'ID della fotografia.
     */
    @Override
    public void deleteSoggettoDaFoto(String nomeSoggetto, int IDFoto) {
        String statement = "DELETE FROM galleria.MOSTRA WHERE nomeSoggetto LIKE ? AND IDFoto = ?";

        try(PreparedStatement aggiuntaSoggettoAFoto = connection.prepareStatement(statement)) {

            aggiuntaSoggettoAFoto.setString(1, nomeSoggetto);
            aggiuntaSoggettoAFoto.setInt(2, IDFoto);

            aggiuntaSoggettoAFoto.executeUpdate();

        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    /**
     * Recupera tutte le associazioni soggetto-foto presenti nel sistema.
     * @param nomeSoggetto Lista in cui verranno inseriti i nomi dei soggetti.
     * @param idFoto Lista in cui verranno inseriti gli ID delle foto.
     */
    @Override
    public void getAllSoggettiMostrati(ArrayList<String> nomeSoggetto, ArrayList<Integer> idFoto){
        String statement = "SELECT * FROM galleria.MOSTRA";

        try(PreparedStatement query = connection.prepareStatement(statement)){

            ResultSet rs = query.executeQuery();

            while(rs.next()){
                nomeSoggetto.add(rs.getString("nomeSoggetto"));
                idFoto.add(rs.getInt("IDFoto"));
            }


        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
