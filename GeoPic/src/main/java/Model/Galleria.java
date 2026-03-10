package Model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Classe astratta che definisce la struttura base di una Galleria nel sistema.
 * Una galleria è un contenitore di oggetti {@link Fotografia} ed è associata a un {@link Utente} proprietario.
 */
public abstract class Galleria{

    /** Identificativo univoco della galleria. */
    private Integer idGalleria;

    /** Nome della galleria. Solitamente segue il pattern: nome proprietario + "'s Gallery". */
    private String nomeGalleria;

    /** L'utente che possiede la galleria (Associazione "Possiede"). */
    private Utente proprietario;

    /** Elenco delle fotografie contenute nella galleria (Associazione "Contiene"). */
    private ArrayList<Fotografia> fotoContenute = new ArrayList<>();

    /**
     * Costruttore per la classe astratta Galleria.
     * Inizializza l'identificativo, il nome, il proprietario e la lista delle foto.
     * @param idGalleria Identificativo univoco della galleria.
     * @param nomeGalleria Nome descrittivo della galleria.
     * @param proprietario L'oggetto {@link Utente} titolare della galleria.
     * @param fotoContenute Lista iniziale di fotografie (se {@code null}, viene inizializzata una lista vuota).
     */
    public Galleria(Integer idGalleria, String nomeGalleria, Utente proprietario, ArrayList<Fotografia> fotoContenute) {
        this.idGalleria = idGalleria;
        this.nomeGalleria = nomeGalleria;

        if(proprietario != null)
            this.proprietario = proprietario;
        else
            this.proprietario = null;

        if(fotoContenute != null){
            this.fotoContenute = fotoContenute;
        }else{
            this.fotoContenute = new ArrayList<>();
        }
    }

    /** @return L'identificativo univoco della galleria. */
    public Integer getIdGalleria() {
        return idGalleria;
    }

    /** @param idGalleria L'ID da assegnare alla galleria. */
    public void setIdGalleria(Integer idGalleria) {
        this.idGalleria = idGalleria;
    }

    /** @return Il nome della galleria. */
    public String getNomeGalleria() {
        return nomeGalleria;
    }

    /** @param nomeGalleria Il nome da assegnare alla galleria. */
    public void setNomeGalleria(String nomeGalleria) {
        this.nomeGalleria = nomeGalleria;
    }

    /** @return L'utente proprietario della galleria. */
    public Utente getProprietario() {
        return proprietario;
    }

    /** @param proprietario L'utente da impostare come proprietario. */
    public void setProprietario(Utente proprietario) {
        this.proprietario = proprietario;
    }

    /** @return La lista delle fotografie presenti in questa galleria. */
    public ArrayList<Fotografia> getFotoContenute() {
        return fotoContenute;
    }

    /**
     * Aggiunge una fotografia alla galleria se non è già presente.
     * @param foto L'oggetto {@link Fotografia} da inserire.
     * @return {@code true} se la foto è stata aggiunta, {@code false} se era già presente nella galleria.
     */
    public boolean addFotoAGalleria(Fotografia foto){
        boolean aggiunta = false;

        if(!(this.fotoContenute.contains(foto))){
            this.fotoContenute.add(foto);

            aggiunta = true;
        }

        return aggiunta;
    }

    /**
     * Rimuove una fotografia dalla galleria, se esistente.
     * @param foto L'oggetto {@link Fotografia} da rimuovere.
     * @return {@code true} se la rimozione è avvenuta con successo, {@code false} altrimenti.
     */
    public boolean removeFotoDAGalleria(Fotografia foto){
        boolean rimozione = false;

        if(this.fotoContenute.contains(foto)){
            this.fotoContenute.remove(foto);

            rimozione = true;
        }

        return rimozione;
    }

    /**
     * Verifica l'uguaglianza tra questa galleria e un altro oggetto.
     * Il confronto si basa esclusivamente sull'{@code idGalleria}.
     * @param obj L'oggetto da confrontare.
     * @return {@code true} se gli oggetti condividono lo stesso ID, {@code false} altrimenti.
     */
    @Override
    public boolean equals(final Object obj){
        if(obj == this) return true;

        if(obj == null || !(obj instanceof Galleria)) return false;

        Galleria galleria = (Galleria) obj;

        return Objects.equals(idGalleria, galleria.idGalleria);
    }
}