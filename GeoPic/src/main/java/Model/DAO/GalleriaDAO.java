package Model.DAO;

public interface GalleriaDAO {
    void addGalleria(String idGalleria, String nomeGalleria, boolean condivisione,
                     String proprietario);

    void removeGalleria(String idGalleria);
}
