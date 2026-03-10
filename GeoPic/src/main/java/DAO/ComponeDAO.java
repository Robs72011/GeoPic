package DAO;

import java.util.ArrayList;

public interface ComponeDAO {
    //aggiunge una coppia idfoto e idvideo, ovvero aggiunge una foto a un video
    void insertComposizione(int IDVideo, int IDFoto);

    //rimuove una coppia idfoto e idvideo, ovvero rimuove una foto da un video
    void deleteComposione(int IDVideo, int IDFoto);

    //aggiorna una coppia idvideo idfoto
    void updateComposizione(int IDVideo, int IDFoto, int NewIDVideo, int NewIDFoto);

    void getAllComposizioni(ArrayList<Integer> idVideo, ArrayList<Integer> idFoto);
}
