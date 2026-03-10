package DAO;

import java.util.ArrayList;

/**
 * Interfaccia DAO per la gestione della persistenza dell'entità {@link Model.Luogo}.
 * Definisce le operazioni per gestire i luoghi geografici nel database,
 * utilizzando le coordinate come identificativo univoco.
 */
public interface LuogoDAO {

    /**
     * Inserisce un nuovo luogo nel database.
     * @param coordinate Le coordinate geografiche che fungono da identificativo del luogo.
     * @param toponimo Il nome mnemonico (etichetta) associato al luogo.
     */
    void insertLuogo(String coordinate, String toponimo);

    /**
     * Elimina un luogo dal database in base alle sue coordinate.
     * @param coordinate Le coordinate del luogo da rimuovere.
     */
    void deleteLuogo(String coordinate);

    /**
     * Recupera l'elenco di tutti i luoghi censiti nel database.
     * I dati vengono popolati nelle liste passate come parametri.
     * @param coordinate Lista in cui verranno inserite le coordinate dei luoghi.
     * @param toponimo Lista in cui verranno inseriti i nomi mnemonici corrispondenti.
     */
    void getAllLuoghi(ArrayList<String> coordinate, ArrayList<String> toponimo);
}
