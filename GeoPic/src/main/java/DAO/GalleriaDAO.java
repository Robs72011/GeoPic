package DAO;

import java.util.ArrayList;

public interface GalleriaDAO {
    void insertGalleria(String nomeGalleria, boolean condivisione, int proprietario);

    void deleteGalleria(int idGalleria);

    public void getAllGallerie(ArrayList<Integer> idGalleria, ArrayList<String> nomeGalleria,
                               ArrayList<Boolean> condivisa, ArrayList<Integer> proprietario);

    public void getOwner(ArrayList<Integer> idGalleria, ArrayList<Integer> proprietario, ArrayList<Boolean> condivisione);
}
