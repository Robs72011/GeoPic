package DAO;

import java.util.ArrayList;

/**
 * Interfaccia DAO per la gestione della relazione "Partecipa" tra {@link Model.Utente}
 * e {@link Model.GalleriaCondivisa}.
 * Definisce le operazioni per gestire l'accesso degli utenti alle gallerie condivise.
 */
public interface PartecipaDAO {

    /**
     * Registra la partecipazione di un utente a una galleria condivisa.
     * @param idGalleria L'identificativo della galleria condivisa.
     * @param idUtente L'identificativo dell'utente che partecipa alla galleria.
     */
    void insertPartecipante(int idGalleria, int idUtente);

    /**
     * Rimuove la partecipazione di un utente da una galleria condivisa.
     * @param idGalleria L'identificativo della galleria condivisa.
     * @param idUtente L'identificativo dell'utente da rimuovere dalla lista dei partecipanti.
     */
    void deletePartecipante(int idGalleria, int idUtente);

    /**
     * Recupera tutte le associazioni di partecipazione presenti nel database,
     * popolando le liste passate come parametri.
     * @param idGalleria Lista in cui vengono memorizzati gli ID delle gallerie condivise.
     * @param idUtente Lista in cui vengono memorizzati gli ID degli utenti partecipanti.
     */
    void getAllPartecipanti(ArrayList<Integer> idGalleria, ArrayList<Integer> idUtente);

}
