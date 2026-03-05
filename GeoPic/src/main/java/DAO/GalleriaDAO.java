package DAO;

import java.util.ArrayList;

public interface GalleriaDAO {
    void insertGalleria(int idGalleria, String nomeGalleria, boolean condivisione,
                     String proprietario);

    void deleteGalleria(int idGalleria);

    public void getAllGallerie(ArrayList<Integer> idGalleria, ArrayList<String> nomeGalleria,
                               ArrayList<Boolean> condivisa, ArrayList<String> proprietario);

    public void getOwner(ArrayList<Integer> idGalleria, ArrayList<String> proprietario);
}
