package Model.DAO;

public interface Utente {
    void addUtente(String idUtente, String nome, boolean isAdmin);

    void removeUtente(String idUtente);
}
