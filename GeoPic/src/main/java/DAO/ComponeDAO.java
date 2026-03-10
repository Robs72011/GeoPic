package DAO;

import java.util.ArrayList;

/**
 * Interfaccia DAO per la gestione della relazione "Compone" tra {@link Model.Video} e {@link Model.Fotografia}.
 * Definisce le operazioni di persistenza per gestire l'associazione tra le foto e i video che le contengono.
 */
public interface ComponeDAO {
    /**
     * Associa una fotografia a un video all'interno del sistema.
     * @param IDVideo L'identificativo del video.
     * @param IDFoto L'identificativo della fotografia da aggiungere alla composizione.
     */
    void insertComposizione(int IDVideo, int IDFoto);

    /**
     * Rimuove l'associazione tra una fotografia e un video.
     * @param IDVideo L'identificativo del video.
     * @param IDFoto L'identificativo della fotografia da rimuovere.
     */

    void deleteComposione(int IDVideo, int IDFoto);

    /**
     * Aggiorna un'associazione esistente tra video e fotografia con nuovi identificativi.
     * @param IDVideo L'identificativo del video corrente.
     * @param IDFoto L'identificativo della fotografia corrente.
     * @param NewIDVideo Il nuovo identificativo del video.
     * @param NewIDFoto Il nuovo identificativo della fotografia.
     */
    void updateComposizione(int IDVideo, int IDFoto, int NewIDVideo, int NewIDFoto);

    /**
     * Recupera tutte le associazioni video-fotografia presenti nel database,
     * popolando le liste passate come parametri.
     * @param idVideo Lista in cui verranno inseriti gli ID dei video.
     * @param idFoto Lista in cui verranno inseriti gli ID delle fotografie corrispondenti.
     */
    void getAllComposizioni(ArrayList<Integer> idVideo, ArrayList<Integer> idFoto);
}
