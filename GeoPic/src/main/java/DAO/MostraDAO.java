package DAO;

import java.util.ArrayList;

public interface MostraDAO {
    void insertSoggettoInFoto(String nomeSoggetto, int IDFoto);

    void deleteSoggettoDaFoto(String nomeSoggetto,  int IDFoto);

    void getAllSoggettiMostrati(ArrayList<String> nomeSoggetto, ArrayList<Integer> idFoto);
}
