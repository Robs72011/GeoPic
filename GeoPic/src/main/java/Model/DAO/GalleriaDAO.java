package Model.DAO;

public interface GalleriaDAO {
    void insertGalleria(String idGalleria, String nomeGalleria, boolean condivisione,
                     String proprietario);

    void deleteGalleria(String idGalleria);
}
