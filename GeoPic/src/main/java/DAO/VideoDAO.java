package DAO;

import java.util.ArrayList;

public interface VideoDAO {
    void insertVideo(int idVideo, String titoloVideo, String descrizione, String galleria);

    void deleteVideo(int idVideo);

    void getAllVideo(ArrayList<Integer> idVideo, ArrayList<String> titoloVideo, ArrayList<String> descrizione,
                            ArrayList<Integer> galleria);

    public void getSalvatoIn(ArrayList<Integer> idVideo, ArrayList<Integer> idGalleria);
}
