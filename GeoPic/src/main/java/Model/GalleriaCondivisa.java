package Model;

import java.util.ArrayList;

/**
 * Rappresenta una galleria fotografica di tipo condiviso.
 * Estende la classe {@link Galleria} aggiungendo la gestione dei partecipanti,
 * ovvero gli utenti che hanno il permesso di accedere e interagire con la galleria.
 */
public class GalleriaCondivisa extends Galleria{

    /** * Elenco degli utenti che partecipano alla galleria (Associazione "Partecipa").
     * Indica chi ha accesso ai contenuti oltre al proprietario.
     */
    private final ArrayList<Utente> partecipanti;

    /**
     * Costruttore completo per la GalleriaCondivisa.
     * Richiama il costruttore della superclasse {@link Galleria} e inizializza la lista dei partecipanti.
     * @param idGalleria Identificativo univoco della galleria.
     * @param nomeGalleria Nome descrittivo della galleria.
     * @param proprietario L'utente {@link Utente} che ha creato la galleria.
     * @param fotoContenute Lista iniziale di fotografie (se {@code null}, viene creata una lista vuota).
     * @param partecipanti Lista iniziale degli utenti partecipanti (se {@code null}, viene creata una lista vuota).
     */
    public GalleriaCondivisa(Integer idGalleria, String nomeGalleria, Utente proprietario,
                             ArrayList<Fotografia> fotoContenute, ArrayList<Utente> partecipanti){
        super(idGalleria, nomeGalleria, proprietario, fotoContenute);

        if(partecipanti !=  null){
            this.partecipanti = partecipanti;
        }else {
            this.partecipanti = new ArrayList<>();
        }
    }

    /**
     * Restituisce la lista degli utenti che partecipano alla galleria.
     * @return Un {@link ArrayList} contenente gli oggetti {@link Utente} partecipanti.
     */
    public ArrayList<Utente> getPartecipanti(){
        return  this.partecipanti;
    }

    /**
     * Aggiunge un nuovo partecipante alla galleria, evitando duplicati.
     * @param partecipante L'utente {@link Utente} da aggiungere alla lista dei partecipanti.
     * @return {@code true} se il partecipante è stato aggiunto con successo,
     * {@code false} se l'utente era già presente nella lista.
     */
    public boolean addPartecipante(Utente partecipante){
        boolean aggiunta = false;

        if(!(this.partecipanti.contains(partecipante))){
            this.partecipanti.add(partecipante);

            aggiunta = true;
        }

        return aggiunta;
    }

    /**
     * Rimuove un partecipante dalla galleria.
     * @param partecipante L'utente {@link Utente} da rimuovere.
     * @return {@code true} se l'utente è stato rimosso correttamente,
     * {@code false} se l'utente non era presente nella lista.
     */
    public boolean removePartecipante(Utente partecipante){
        boolean rimozione = false;

        if(this.partecipanti.contains(partecipante)){
            this.partecipanti.remove(partecipante);

            rimozione = true;
        }

        return rimozione;
    }

    @Override
    public String toString() {
        return getNomeGalleria();
    }
}