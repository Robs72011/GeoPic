package Model.DAO;

import java.util.ArrayList;

public interface ContieneDAO {
    void insertFotoAGalleria(String IDGalleria, String IDFoto);

    void deleteFotoDaGalleria(String IDGalleria, String IDFoto);

    void getAllContenute(ArrayList<String> idGalleria, ArrayList<String>idFoto);
}
