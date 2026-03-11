package DAO;

import java.util.ArrayList;

/**
 * Interfaccia DAO per la gestione della persistenza dell'entità {@link Model.Galleria}.
 * Definisce le operazioni per la creazione, eliminazione e il recupero delle gallerie,
 * nonché la gestione delle relazioni di proprietà e condivisione.
 */
public interface GalleriaDAO {

    /**
     * Inserisce una nuova galleria nel sistema.
     * @param nomeGalleria Il nome descrittivo della galleria.
     * @param condivisione Flag che indica se la galleria è condivisa o privata.
     * @param proprietario L'identificativo dell'utente proprietario della galleria.
     * @return L'identificativo univoco generato per la nuova galleria.
     */
    Integer insertGalleria(String nomeGalleria, boolean condivisione, int proprietario);

    /**
     * Rimuove una galleria dal sistema basandosi sul suo identificativo.
     * @param idGalleria L'identificativo univoco della galleria da eliminare.
     */
    void deleteGalleria(int idGalleria);

    /**
     * Recupera l'elenco completo di tutte le gallerie registrate.
     * I dati vengono popolati nelle liste fornite come parametri.
     * @param idGalleria Lista degli ID delle gallerie.
     * @param nomeGalleria Lista dei nomi delle gallerie.
     * @param condivisa Lista degli stati di condivisione.
     * @param proprietario Lista degli ID dei proprietari.
     */
    void getAllGallerie(ArrayList<Integer> idGalleria, ArrayList<String> nomeGalleria,
                        ArrayList<Boolean> condivisa, ArrayList<Integer> proprietario);

    /**
     * Recupera le informazioni di proprietà e condivisione relative alle gallerie.
     * @param idGalleria Lista degli ID delle gallerie.
     * @param proprietario Lista degli ID dei proprietari associati.
     * @param condivisione Lista degli stati di condivisione corrispondenti.
     */
    void getOwner(ArrayList<Integer> idGalleria, ArrayList<Integer> proprietario, ArrayList<Boolean> condivisione);
}
