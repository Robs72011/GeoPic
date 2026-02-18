package Model.DAO;

public interface Galleria {
    void addGalleria(String idGalleria, String nomeGalleria, boolean condivisione,
                     String proprietario);

    void removeGalleria(String idGalleria);
}
