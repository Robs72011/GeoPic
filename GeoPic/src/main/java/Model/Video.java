package Model;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Objects;


/**
 * Rappresenta un'entità Video all'interno del sistema.
 * Un video è composto da una sequenza di oggetti {@link Fotografia} ed è memorizzato
 * all'interno di un'istanza di {@link Galleria}.
 */
public class Video {

    /** Identificativo univoco del video. */
    private Integer idVideo;

    /** Descrizione del contenuto del video. */
    private String descrizione;

    /** Titolo del video. */
    private String titolo;

    /** Elenco delle fotografie che compongono la sequenza del video (Associazione "Compone"). */
    private ArrayList<Fotografia> compostoDaFoto;

    /** Galleria in cui il video è salvato (Associazione "Salvato in"). */
    private Galleria galleria;

    /**
     * Costruttore completo della classe Video.
     * @param idVideo ID univoco.
     * @param descrizione Breve descrizione.
     * @param titolo Titolo del video.
     * @param compostoDaFoto Lista di foto iniziali (se {@code null}, viene inizializzata vuota).
     * @param galleria Riferimento alla galleria di appartenenza.
     */
    public Video(Integer idVideo, String descrizione, String titolo, ArrayList<Fotografia> compostoDaFoto, Galleria galleria){

        this.idVideo=idVideo;
        this.descrizione=descrizione;
        this.titolo = titolo;

        if(compostoDaFoto == null){
            this.compostoDaFoto = new ArrayList<>();
        }else{
            this.compostoDaFoto = compostoDaFoto;
        }

        this.galleria=galleria;
    }

    /** @return L'ID univoco del video. */
    public Integer getIdVideo() {
        return idVideo;
    }

    /** @param idVideo L'ID da assegnare. */
    public void setIdVideo(Integer idVideo) {
        this.idVideo = idVideo;
    }

    /** @return La descrizione del video. */
    public String getDescrizione() {
        return descrizione;
    }

    /** @param descrizione La descrizione da impostare. */
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    /** @return Il titolo del video. */
    public String getTitolo() {
        return titolo;
    }

    /** @param titolo Il titolo da impostare. */
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    /** @return La lista di {@link Fotografia} che compongono il video. */
    public ArrayList<Fotografia> getCompostoDaFoto() {
        return compostoDaFoto;
    }

    /**
     * Associa una galleria a questo video.
     * @param galleria La {@link Galleria} di destinazione.
     */
    public void setGalleria(Galleria galleria) {
        this.galleria = galleria;
    }

    /**
     * Aggiunge una fotografia alla composizione del video, se non già presente.
     * @param foto L'oggetto {@link Fotografia} da inserire.
     * @return {@code true} se aggiunta con successo, {@code false} se già presente.
     */
    public boolean addFotoAVideo(Fotografia foto){
        boolean aggiunta = false;

        if(!(this.compostoDaFoto.contains(foto))){
            this.compostoDaFoto.add(foto);

            aggiunta = true;
        }

        return aggiunta;
    }

    /**
     * Rimuove una fotografia dalla composizione del video.
     * @param foto L'oggetto {@link Fotografia} da rimuovere.
     * @return {@code true} se rimossa correttamente, {@code false} se non presente.
     */
    public boolean removeFotoAVideo(Fotografia foto){
        boolean rimossa = false;

        if(this.compostoDaFoto.contains(foto)){
            this.compostoDaFoto.remove(foto);

            rimossa = true;
        }

        return rimossa;
    }

    /**
     * Verifica l'uguaglianza tra due video basandosi sul loro ID.
     * @param obj L'oggetto da confrontare.
     * @return {@code true} se gli ID coincidono.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;

        if(obj == null || !(obj instanceof Video)) return false;

        Video video = (Video) obj;

        return Objects.equals(idVideo, video.getIdVideo());
    }
}