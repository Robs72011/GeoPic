package Model.DAO;

public interface UtenteDAO {
    void insertUtente(String idUtente, String username, String password, boolean isAdmin, boolean isSoggetto);

    void deleteUtente(String idUtente);
}
