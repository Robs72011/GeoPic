package DAO;

import java.util.ArrayList;

/**
 * Interfaccia DAO per la gestione della persistenza dell'entità {@link Model.Utente}.
 * Definisce le operazioni per registrare, rimuovere e recuperare gli utenti,
 * oltre a gestire il controllo dell'autenticazione.
 */
public interface UtenteDAO {

    /**
     * Registra un nuovo utente nel sistema.
     * @param username Il nome utente scelto.
     * @param password La password associata all'account.
     * @param isAdmin Flag per indicare se l'utente ha privilegi di amministratore.
     * @param isSoggetto Flag per indicare se l'utente funge anche da soggetto.
     * @return {@code true} se l'utente è stato inserito correttamente, {@code false} altrimenti.
     */
    boolean insertUtente(String username, String password, boolean isAdmin, boolean isSoggetto);

    /**
     * Rimuove un utente dal sistema in base al suo ID.
     * @param idUtente L'identificativo univoco dell'utente da eliminare.
     */
    void deleteUtente(int idUtente);

    /**
     * Recupera l'elenco di tutti gli utenti registrati, popolando le liste fornite come parametri.
     * @param idUtente Lista degli ID utente.
     * @param username Lista degli username.
     * @param password Lista delle password (si consiglia di gestire in modo sicuro).
     * @param isAdmin Lista dei privilegi amministrativi.
     * @param isSoggetto Lista dei flag soggetto.
     */
    void getAllUtenti(ArrayList<Integer> idUtente, ArrayList<String> username, ArrayList<String> password,
                      ArrayList<Boolean> isAdmin, ArrayList<Boolean> isSoggetto);

    /**
     * Verifica la presenza di un utente per il login e ne restituisce l'ID se trovato.
     * @param username Lo username da cercare.
     * @return L'identificativo dell'utente se trovato, {@code null} o un valore indicativo in caso contrario.
     */
    Integer getLoggedInUtente(String username);
}
