package Model.DAO;

public interface VideoDAO {
    void createVideo(String idVideo, String titoloVideo, String descrizione, String galleria);

    void removeVideo(String idVideo);
}
