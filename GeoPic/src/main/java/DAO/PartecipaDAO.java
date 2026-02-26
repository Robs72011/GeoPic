package DAO;

import java.util.ArrayList;

public interface PartecipaDAO {
    void insertPartecipante(String idGalleria, String idUtente);

    void deletePartecipante(String idGalleria, String idUtente);

    void getAllPartecipanti(ArrayList<String> idGalleria, ArrayList<String> idUtente);

}
