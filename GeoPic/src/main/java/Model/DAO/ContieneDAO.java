package Model.DAO;

public interface ContieneDAO {
    void insertFotoAGalleria(String IDGalleria, String IDFoto);

    void deleteFotoDaGalleria(String IDGalleria, String IDFoto);
}
