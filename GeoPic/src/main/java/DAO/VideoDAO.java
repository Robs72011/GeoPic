package DAO;

import java.util.ArrayList;

public interface VideoDAO {
    void insertVideo(String titoloVideo, String descrizione, int galleria);

    void deleteVideo(int idVideo);

    void getAllVideo(ArrayList<Integer> idVideo, ArrayList<String> titoloVideo, ArrayList<String> descrizione,
                            ArrayList<Integer> galleria);

    public void getSalvatoIn(ArrayList<Integer> idVideo, ArrayList<Integer> idGalleria);
}
