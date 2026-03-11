package Model;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Rappresenta un utente registrato nel sistema.
 * Gestisce le credenziali, i privilegi (amministratore), l'eventuale ruolo come soggetto
 * e le relazioni con fotografie e gallerie (proprietario o partecipante).
 */
public class Utente {

    /** Identificativo univoco dell'utente. */
    private Integer idUtente;

    /** Username dell'utente. */
    private String username;

    /** Password dell'utente (si consiglia di gestirla criptata). */
    private String password;

    /** Flag che indica se l'utente possiede privilegi di amministratore. */
    private boolean isAdmin;

    /** Flag che indica se l'utente è associato a un'entità {@link Soggetto}. */
    private boolean isSoggetto;

    /** Lista delle fotografie scattate dall'utente (Associazione "Scatta"). */
    private final ArrayList<Fotografia> fotoScattate;

    /** Lista delle gallerie di cui l'utente è proprietario (Associazione "Possiede"). */
    private final ArrayList<Galleria> galleriePossedute;

    /** Lista delle gallerie condivise a cui l'utente partecipa (Associazione "Partecipa"). */
    private final ArrayList<GalleriaCondivisa> utentePartecipaGalleriaCondivisa;

    /**
     * Costruttore completo della classe Utente.
     * Inizializza le collezioni vuote se i parametri forniti sono nulli.
     * @param idUtente ID univoco.
     * @param username Nome utente.
     * @param password Password.
     * @param isAdmin True se amministratore.
     * @param isSoggetto True se l'utente è anche un soggetto.
     * @param fotoScattate Lista foto scattate.
     * @param galleriePossedute Lista gallerie di proprietà.
     * @param utentePartecipaGalleriaCondivisa Lista gallerie condivise a cui partecipa.
     */
    public Utente (Integer idUtente, String username, String password, boolean isAdmin, boolean isSoggetto,
                   ArrayList<Fotografia> fotoScattate, ArrayList<Galleria> galleriePossedute,
                   ArrayList<GalleriaCondivisa> utentePartecipaGalleriaCondivisa){
        this.idUtente = idUtente;
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;

        this.isSoggetto = isSoggetto;

        if(fotoScattate == null) {
            this.fotoScattate = new ArrayList<>();
        }else{
            this.fotoScattate = fotoScattate;
        }

        if(galleriePossedute == null) {
            this.galleriePossedute = new ArrayList<>();
        }else{
            this.galleriePossedute = galleriePossedute;
        }

        if(utentePartecipaGalleriaCondivisa == null) {
            this.utentePartecipaGalleriaCondivisa = new ArrayList<>();
        }else{
            this.utentePartecipaGalleriaCondivisa = utentePartecipaGalleriaCondivisa;
        }
    }

    /** @return L'ID utente. */
    public Integer getIdUtente() {
        return idUtente;
    }

    /** @param idUtente L'ID da assegnare. */
    public void setIdUtente(Integer idUtente) {
        this.idUtente = idUtente;
    }

    /** @return Lo username dell'utente. */
    public String getUsername() {
        return username;
    }

    /**
     * Imposta lo username dell'utente.
     * @param username Il nuovo nome utente.
     * @return {@code true} se valido e impostato, {@code false} se nullo o vuoto.
     */
    public boolean setUsername(String username) {
        if(username == null || username.trim().isEmpty())
            return false;
        else {
            this.username = username;
            return true;
        }
    }

    /** @return La password. */
    public String getPassword() {
        return password;
    }

    /** @param password La password da impostare. */
    public void setPassword(String password) {
        this.password = password;
    }

    /** @return {@code true} se l'utente è amministratore. */
    public boolean isAdmin() {
        return isAdmin;
    }

    /** @param admin Flag amministratore. */
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    /** @return {@code true} se l'utente è un soggetto. */
    public boolean isSoggetto() {
        return isSoggetto;
    }

    /** @param soggetto Flag per impostare se l'utente è un soggetto. */
    public void setSoggetto(boolean soggetto) {
        isSoggetto = soggetto;
    }

    /** @return Lista delle foto scattate. */
    public ArrayList<Fotografia> getFotoScattate() {
        return fotoScattate;
    }

    /**
     * Registra una nuova foto scattata dall'utente.
     * @param f La {@link Fotografia}.
     * @return {@code true} se aggiunta.
     */
    public boolean addFotoScattate(Fotografia f){
        boolean aggiunta = false;

        if(!(this.fotoScattate.contains(f))){
            this.fotoScattate.add(f);

            aggiunta = true;
        }

        return aggiunta;
    }

    /**
     * Aggiunge una galleria alle proprietà dell'utente.
     * @param g La {@link Galleria}.
     * @return {@code true} se aggiunta.
     */
    public boolean addGalleriePossedute(Galleria g){
        boolean aggiunta = false;

        if(!(this.galleriePossedute.contains(g))){
            this.galleriePossedute.add(g);

            aggiunta = true;
        }

        return aggiunta;
    }

    /**
     * Recupera l'elenco delle gallerie di cui l'utente è proprietario.
     * @return Un {@link ArrayList} contenente le gallerie {@link Galleria} possedute dall'utente.
     */
    public  ArrayList<Galleria> getGalleriePossedute() {
        return galleriePossedute;
    }

    /**
     * Registra la partecipazione a una galleria condivisa.
     * @param galCond La {@link GalleriaCondivisa}.
     * @return {@code true} se aggiunta.
     */
    public boolean addPartecipazioneAGalleria(GalleriaCondivisa galCond){
        boolean aggiunta = false;

        if(!(this.utentePartecipaGalleriaCondivisa.contains(galCond))){
            this.utentePartecipaGalleriaCondivisa.add(galCond);

            aggiunta = true;
        }

        return aggiunta;
    }

    /**
     * Rimuove una foto dall'elenco delle foto scattate dall'utente.
     * @param f La {@link Fotografia}.
     * @return {@code true} se rimossa.
     */
    public boolean removeFotoScattata(Fotografia f){
        boolean rimozione = false;

        if(this.fotoScattate.contains(f)){
            this.fotoScattate.remove(f);

            rimozione = true;
        }

        return  rimozione;
    }

    /**
     * Confronta l'utente corrente con un oggetto basandosi sull'ID.
     * @param obj L'oggetto da confrontare.
     * @return {@code true} se gli ID coincidono.
     */
    @Override
    public boolean equals(final Object obj){
        if(obj == this) return true;

        if(!(obj instanceof Utente utente)) return false;

        return Objects.equals(idUtente, utente.idUtente);
    }
}