package Model.DAO;

public interface UtenteDAO {
    void addUtente(String idUtente, String nome, boolean isAdmin);

    void removeUtente(String idUtente);
}
