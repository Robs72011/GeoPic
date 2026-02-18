package Model.DAO;

public interface Compone {
    //aggiunge una coppia idfoto e idvideo, ovvero aggiunge una foto a un video
    void addComposizione(String IDVideo, String IDFoto);

    //rimuove una coppia idfoto e idvideo, ovvero rimuove una foto da un videoz
    void removeComposione(String IDVideo, String IDFoto);

    //aggiorna una coppia idvideo idfoto
    void updateComposizione(String IDVideo, String IDFoto, String NewIDVideo, String NewIDFoto);

}
