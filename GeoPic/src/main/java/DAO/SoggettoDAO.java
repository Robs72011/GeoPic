package DAO;

import java.util.ArrayList;

/**
 * Interfaccia DAO per la gestione della persistenza dell'entità {@link Model.Soggetto}.
 * Definisce le operazioni per gestire i soggetti, inclusa la possibilità di associare
 * un utente del sistema a un soggetto specifico.
 */
public interface SoggettoDAO {

    /**
     * Inserisce un nuovo soggetto generico nel database.
     * @param nomeSoggetto Il nome del soggetto.
     * @param categoria La categoria di appartenenza del soggetto.
     */
    void insertSoggetto(String nomeSoggetto, String categoria);

    /**
     * Elimina un soggetto dal database.
     * @param nomeSoggetto Il nome del soggetto da rimuovere.
     */
    void deleteSoggetto(String nomeSoggetto);

    /**
     * Inserisce un soggetto che corrisponde a un utente registrato nel sistema.
     * @param nomeUtente Il nome del soggetto.
     * @param categoria La categoria del soggetto.
     * @param idUtente L'identificativo dell'utente {@link Model.Utente} da associare.
     */
    void insertUtenteAsSoggetto(String nomeUtente, String categoria, int idUtente);

    /**
     * Elimina l'associazione di un utente come soggetto dal database.
     * @param nomeUtente Il nome del soggetto/utente da rimuovere.
     */
    void deleteUtenteAsSoggetto(String nomeUtente);

    /**
     * Recupera l'elenco di tutti i soggetti presenti nel database,
     * popolando le liste passate come parametri.
     * @param nomeSoggetto Lista in cui verranno inseriti i nomi dei soggetti.
     * @param categoria Lista in cui verranno inserite le categorie corrispondenti.
     */
    void getAllSoggetti(ArrayList<String> nomeSoggetto, ArrayList<String> categoria);
}
