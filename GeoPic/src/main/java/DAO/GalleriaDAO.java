package DAO;

import java.util.ArrayList;

public interface GalleriaDAO {
    void insertGalleria(String idGalleria, String nomeGalleria, boolean condivisione,
                     String proprietario);

    void deleteGalleria(String idGalleria);

    public void getAllGallerie(ArrayList<String> idGalleria, ArrayList<String> nomeGalleria,
                               ArrayList<Boolean> condivisa, ArrayList<String> proprietario);

    }
