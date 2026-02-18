package Model.DAO;

public interface Video {
    void createVideo(String idVideo, String titoloVideo, String descrizione, String galleria);

    void removeVideo(String idVideo);
}
