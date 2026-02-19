package Model.DAO;

public interface ContieneDAO {
    void addFotoAGalleria(String IDGalleria, String IDFoto);

    void removeFotoDAGalleria(String IDGalleria, String IDFoto);
}
