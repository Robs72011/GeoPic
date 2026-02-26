package DAO;

import java.util.ArrayList;

public interface VideoDAO {
    void insertVideo(String idVideo, String titoloVideo, String descrizione, String galleria);

    void deleteVideo(String idVideo);

    void getAllVideo(ArrayList<String> idVideo, ArrayList<String> titoloVideo, ArrayList<String> descrizione,
                            ArrayList<String> galleria);
}
