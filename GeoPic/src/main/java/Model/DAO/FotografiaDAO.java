package Model.DAO;

import java.time.LocalDate;

public interface FotografiaDAO {
    //Aggiunge una foto con tutte le info
    void insertFotografia(String idFoto, String device, LocalDate dataScatto, LocalDate dataEliminazione,
                 boolean visibilita, String coordinate, String autore);

    //rimuove una foto andando ad aggiungere la data di eliminazione
    void deleteFotografia(String IDFoto);

    void updateVisibilita(String IDFoto, boolean visibilita);

    void updateDataEliminazione(String IDFoto, LocalDate dataEliminazione);
}
