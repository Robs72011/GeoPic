package Model.DAO;

public interface PartecipaDAO {
    void insertPartecipante(String idGalleria, String idUtente);

    void deletePartecipante(String idGalleria, String idUtente);
}
