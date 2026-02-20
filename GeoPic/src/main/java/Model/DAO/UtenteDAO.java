package Model.DAO;

public interface UtenteDAO {
    void addUtente(String idUtente, String username, String password, boolean isAdmin, boolean isSoggetto);

    void removeUtente(String idUtente);
}
