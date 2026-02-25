package Model.DAO;

import java.time.LocalDate;
import java.util.ArrayList;

public interface FotografiaDAO {
    //Aggiunge una foto con tutte le info
    void insertFotografia(String idFoto, String device, LocalDate dataScatto, LocalDate dataEliminazione,
                 boolean visibilita, String coordinate, String autore);

    //rimuove una foto andando ad aggiungere la data di eliminazione
    void deleteFotografia(String IDFoto);

    void updateVisibilita(String IDFoto, boolean visibilita);

    void updateDataEliminazione(String IDFoto, LocalDate dataEliminazione);

    void getAllFotografie(ArrayList<String> idFoto, ArrayList<String> device, ArrayList<String> autore,
                          ArrayList<String> coordinate, ArrayList<Boolean> visibilita,
                          ArrayList<LocalDate> dataDiScatto, ArrayList<LocalDate> dataEliminazione);
}
