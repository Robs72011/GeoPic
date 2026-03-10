package DAO;

import java.util.ArrayList;

public interface PartecipaDAO {
    void insertPartecipante(int idGalleria, int idUtente);

    void deletePartecipante(int idGalleria, int idUtente);

    void getAllPartecipanti(ArrayList<Integer> idGalleria, ArrayList<Integer> idUtente);

}
