package DAO;

import java.time.LocalDate;
import java.util.ArrayList;

public interface FotografiaDAO {
    //Aggiunge una foto con tutte le info
    Integer insertFotografia(String device, LocalDate dataScatto, LocalDate dataEliminazione,
                             boolean visibilita, String coordinate, int autore);


    //rimuove una foto andando ad aggiungere la data di eliminazione
    void deleteFotografia(int IDFoto);

    void updateVisibilita(int IDFoto, boolean visibilita);

    void updateDataEliminazione(int IDFoto, LocalDate dataEliminazione);

    void getAllFotografie(ArrayList<Integer> idFoto, ArrayList<String> device, ArrayList<Integer> autore,
                          ArrayList<String> coordinate, ArrayList<Boolean> visibilita,
                          ArrayList<LocalDate> dataDiScatto, ArrayList<LocalDate> dataEliminazione);

    public void getRaffigura(ArrayList<Integer> idFoto, ArrayList<String> luogo);

    public void getScatta(ArrayList<Integer> idFoto, ArrayList<Integer> autore);
}