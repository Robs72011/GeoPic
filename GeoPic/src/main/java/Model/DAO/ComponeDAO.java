package Model.DAO;

public interface ComponeDAO {
    //aggiunge una coppia idfoto e idvideo, ovvero aggiunge una foto a un video
    void addComposizione(String IDVideo, String IDFoto);

    //rimuove una coppia idfoto e idvideo, ovvero rimuove una foto da un video
    void removeComposione(String IDVideo, String IDFoto);

    //aggiorna una coppia idvideo idfoto
    void updateComposizione(String IDVideo, String IDFoto, String NewIDVideo, String NewIDFoto);

}
