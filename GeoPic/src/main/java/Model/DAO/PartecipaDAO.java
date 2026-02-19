package Model.DAO;

public interface PartecipaDAO {
    void addPartecipante(String idGalleria, String idUtente);

    void removePartecipante(String idGalleria, String idUtente);
}
