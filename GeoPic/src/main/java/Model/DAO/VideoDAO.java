package Model.DAO;

public interface VideoDAO {
    void insertVideo(String idVideo, String titoloVideo, String descrizione, String galleria);

    void deleteVideo(String idVideo);
}
