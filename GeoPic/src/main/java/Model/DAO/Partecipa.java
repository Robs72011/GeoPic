package Model.DAO;

public interface Partecipa {
    void addPartecipante(String idGalleria, String idUtente);

    void removePartecipante(String idGalleria, String idUtente);
}
