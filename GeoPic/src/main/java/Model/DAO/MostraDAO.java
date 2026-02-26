package Model.DAO;

import java.util.ArrayList;

public interface MostraDAO {
    void insertSoggettoInFoto(String nomeSoggetto, String IDFoto);

    void deleteSoggettoDaFoto(String nomeSoggetto,  String IDFoto);

    void getAlSoggettiMostrati(ArrayList<String> nomeSoggetto, ArrayList<String> idFoto);
}
