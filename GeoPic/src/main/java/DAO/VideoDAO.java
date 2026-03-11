package DAO;

import java.util.ArrayList;

/**
 * Interfaccia DAO per la gestione della persistenza dell'entità {@link Model.Video}.
 * Definisce le operazioni per la creazione, eliminazione e il recupero dei video,
 * oltre alla gestione della relazione "Salvato in" con le gallerie.
 */
public interface VideoDAO {

    /**
     * Inserisce un nuovo video nel sistema.
     * @param titoloVideo Il titolo del video.
     * @param descrizione La descrizione del contenuto del video.
     * @param galleria L'identificativo della galleria in cui il video è salvato.
     */
    void insertVideo(String titoloVideo, String descrizione, int galleria);

    /**
     * Elimina un video dal sistema in base al suo identificativo.
     * @param idVideo L'identificativo univoco del video da eliminare.
     */
    void deleteVideo(int idVideo);

    /**
     * Recupera l'elenco di tutti i video presenti nel database.
     * I risultati vengono popolati nelle liste passate come parametri.
     * @param idVideo Lista degli ID dei video.
     * @param titoloVideo Lista dei titoli dei video.
     * @param descrizione Lista delle descrizioni.
     * @param galleria Lista degli ID delle gallerie di appartenenza.
     */
    void getAllVideo(ArrayList<Integer> idVideo, ArrayList<String> titoloVideo, ArrayList<String> descrizione,
                     ArrayList<Integer> galleria);

    /**
     * Recupera le associazioni "Salvato in" che collegano i video alle rispettive gallerie.
     * @param idVideo Lista degli ID dei video.
     * @param idGalleria Lista degli ID delle gallerie corrispondenti.
     */
    void getSalvatoIn(ArrayList<Integer> idVideo, ArrayList<Integer> idGalleria);
}
