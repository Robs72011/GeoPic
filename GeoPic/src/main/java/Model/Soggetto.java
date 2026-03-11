package Model;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Rappresenta un soggetto presente all'interno di una o più fotografie.
 * Un soggetto può essere un'entità generica o corrispondere a un {@link Utente} registrato nel sistema.
 */
public class Soggetto {

    /** Nome identificativo del soggetto. */
    private String nomeSoggetto;

    /** Categoria o classificazione del soggetto (es. "Persona", "Animale"). */
    private String categoria;

    /** Riferimento opzionale all'utente, se il soggetto è un utente del sistema. */
    private Utente utenteRappresentato;

    /** Lista delle fotografie in cui questo soggetto è ritratto. */
    private ArrayList<Fotografia> fotoInCuiAppare;

    /**
     * Costruttore completo della classe Soggetto.
     * @param nomeSoggetto Nome identificativo.
     * @param categoria Categoria di appartenenza.
     * @param utenteRappresentato L'oggetto {@link Utente} corrispondente (può essere {@code null}).
     * @param fotoInCuiAppare Lista di fotografie iniziali (se {@code null}, viene inizializzata una lista vuota).
     */
    public Soggetto(String nomeSoggetto, String categoria, Utente utenteRappresentato,
                    ArrayList<Fotografia> fotoInCuiAppare) {
        this.nomeSoggetto = nomeSoggetto;
        this.categoria = categoria;

        this.utenteRappresentato = utenteRappresentato;

        if(fotoInCuiAppare != null){
            this.fotoInCuiAppare = fotoInCuiAppare;
        }else{
            this.fotoInCuiAppare = new ArrayList<>();
        }
    }

    /**
     * Costruttore semplificato per un soggetto non associato a un utente.
     * @param nomeSoggetto Nome identificativo.
     * @param categoria Categoria di appartenenza.
     */
    public Soggetto(String nomeSoggetto, String categoria) {
        this.nomeSoggetto = nomeSoggetto;
        this.categoria = categoria;
        this.utenteRappresentato = null;
        this.fotoInCuiAppare = new ArrayList<>();
    }

    /** @return Il nome del soggetto. */
    public String getNomeSoggetto() {
        return nomeSoggetto;
    }

    /** @param nomeSoggetto Il nome da assegnare. */
    public void setNomeSoggetto(String nomeSoggetto) {
        this.nomeSoggetto = nomeSoggetto;
    }

    /** @return La categoria del soggetto. */
    public String getCategoria() {
        return categoria;
    }

    /** @param categoria La categoria da assegnare. */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * Verifica se il soggetto è un utente registrato.
     * @return {@code true} se è un utente, {@code false} altrimenti.
     */
    public boolean isUtente(){
        //Se il soggetto è utente, ritorna true
        return utenteRappresentato != null;
    }

    /** @return L'utente associato, o {@code null} se non presente. */
    public Utente getUtenteRappresentato() {
        return utenteRappresentato;
    }

    /** @param utente L'utente da associare al soggetto. */
    public void setUtenteRappresentato(Utente utente) {
        this.utenteRappresentato = utente;
    }

    /**
     * Verifica l'uguaglianza basata sul nome del soggetto.
     * @param obj L'oggetto da confrontare.
     * @return {@code true} se i nomi coincidono, {@code false} altrimenti.
     */
    @Override
    public boolean equals(final Object obj){
        if(obj == this) return true;

        if(!(obj instanceof Soggetto soggetto)) return false;

        return Objects.equals(nomeSoggetto, soggetto.nomeSoggetto);
    }

    /**
     * Aggiunge una fotografia all'elenco di quelle in cui appare il soggetto.
     *
     * @param fotografia La {@link Fotografia} da aggiungere.
     */
    public void addFotoInCuiAppare(Fotografia fotografia){
        boolean aggiunta = false;

        if(!(this.fotoInCuiAppare.contains(fotografia))){
            this.fotoInCuiAppare.add(fotografia);

            aggiunta = true;
        }

    }
}