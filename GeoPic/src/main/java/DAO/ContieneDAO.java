package DAO;

import java.util.ArrayList;

public interface ContieneDAO {
    void insertFotoAGalleria(int IDGalleria, int IDFoto);

    void deleteFotoDaGalleria(int IDGalleria, int IDFoto);

    void getAllContenute(ArrayList<Integer> idGalleria, ArrayList<Integer>idFoto);
}
