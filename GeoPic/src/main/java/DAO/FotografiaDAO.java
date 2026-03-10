package DAO;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Interfaccia DAO per la gestione della persistenza dell'entità {@link Model.Fotografia}.
 * Definisce le operazioni per l'inserimento, la rimozione logica e l'aggiornamento
 * degli attributi fotografici, oltre alle relazioni con autori e luoghi.
 */
public interface FotografiaDAO {
    /**
     * Inserisce una nuova fotografia nel sistema.
     * @param device Il dispositivo utilizzato per lo scatto.
     * @param dataScatto La data in cui è stata scattata la foto.
     * @param dataEliminazione La data di eliminazione (può essere {@code null}).
     * @param visibilita Stato di visibilità della foto.
     * @param coordinate Le coordinate geografiche del luogo associato.
     * @param autore L'identificativo dell'utente che ha scattato la foto.
     * @return L'identificativo univoco generato per la nuova fotografia.
     */
    Integer insertFotografia(String device, LocalDate dataScatto, LocalDate dataEliminazione,
                             boolean visibilita, String coordinate, int autore);


    /**
     * Esegue una rimozione logica della fotografia impostando la data di eliminazione.
     * @param IDFoto L'identificativo della fotografia da eliminare.
     */
    void deleteFotografia(int IDFoto);

    /**
     * Aggiorna lo stato di visibilità di una fotografia.
     * @param IDFoto L'identificativo della fotografia.
     * @param visibilita Il nuovo stato di visibilità.
     */
    void updateVisibilita(int IDFoto, boolean visibilita);

    /**
     * Aggiorna la data di eliminazione di una fotografia.
     * @param IDFoto L'identificativo della fotografia.
     * @param dataEliminazione La nuova data di eliminazione.
     */
    void updateDataEliminazione(int IDFoto, LocalDate dataEliminazione);

    /**
     * Recupera l'elenco completo di tutte le fotografie presenti nel database.
     * I risultati vengono popolati nelle liste passate come parametri.
     * @param idFoto Lista degli ID.
     * @param device Lista dei dispositivi.
     * @param autore Lista degli ID degli autori.
     * @param coordinate Lista delle coordinate dei luoghi.
     * @param visibilita Lista degli stati di visibilità.
     * @param dataDiScatto Lista delle date di scatto.
     * @param dataEliminazione Lista delle date di eliminazione.
     */
    void getAllFotografie(ArrayList<Integer> idFoto, ArrayList<String> device, ArrayList<Integer> autore,
                          ArrayList<String> coordinate, ArrayList<Boolean> visibilita,
                          ArrayList<LocalDate> dataDiScatto, ArrayList<LocalDate> dataEliminazione);

    /**
     * Recupera le associazioni "Raffigura" tra fotografie e luoghi.
     * @param idFoto Lista degli ID delle foto.
     * @param luogo Lista delle coordinate dei luoghi corrispondenti.
     */
    public void getRaffigura(ArrayList<Integer> idFoto, ArrayList<String> luogo);

    /**
     * Recupera le associazioni "Scatta" tra fotografie e autori.
     * @param idFoto Lista degli ID delle foto.
     * @param autore Lista degli ID degli autori corrispondenti.
     */
    public void getScatta(ArrayList<Integer> idFoto, ArrayList<Integer> autore);
}