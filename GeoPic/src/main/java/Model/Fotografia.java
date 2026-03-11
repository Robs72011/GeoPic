package Model;

import java.time.LocalDate;
import java.util.*;

/**
 * Rappresenta un'entità Fotografia all'interno del sistema.
 * Gestisce le informazioni relative allo scatto, ai metadati del dispositivo,
 * alla visibilità e alle relazioni con autori, luoghi, gallerie, video e soggetti.
 */

public class Fotografia {

    /** Identificativo univoco della fotografia. */
    private Integer idFoto;

    /** Il dispositivo utilizzato per scattare la foto (es. Modello smartphone o fotocamera). */
    private String dispositivo;

    /** La data in cui è stata scattata la fotografia. */
    private LocalDate dataDiScatto;

    /** La data di eliminazione logica della foto. Se {@code null}, la foto non è eliminata. */
    private LocalDate dataDiEliminazione;

    /** Flag di visibilità della foto (pubblica/privata). Default {@code true}. */
    private boolean visibility;

    /** L'utente che ha scattato la foto (Associazione "Scattata"). */
    private Utente autore;

    /** Il luogo geografico associato allo scatto (Associazione "Raffigura"). */
    private Luogo luogo;

    /** Elenco delle gallerie che contengono questa foto (Associazione "Contenuta"). */
    private final ArrayList<Galleria> galleriaContenitrice;

    /** Elenco dei video composti da questa foto (Associazione "Compone"). */
    private final ArrayList<Video> fotoComponeVideo;

    /** Elenco dei soggetti presenti nella fotografia (Associazione "Mostra"). */
    private final ArrayList<Soggetto> soggetti;

    /**
     * Costruttore completo della classe Fotografia.
     * Inizializza le liste e gestisce i valori nulli per garantire la coerenza dell'oggetto.
     * @param idFoto Identificativo univoco.
     * @param dispositivo Modello del dispositivo di scatto.
     * @param dataDiScatto Data di acquisizione.
     * @param dataDiEliminazione Data di rimozione (può essere {@code null}).
     * @param visibility Stato di visibilità iniziale.
     * @param autore L'oggetto {@link Utente} che ha creato la foto.
     * @param luogo L'oggetto {@link Luogo} dove è stata scattata.
     * @param galleriaContenitrice Lista iniziale di gallerie di appartenenza.
     * @param soggetti Lista iniziale di soggetti ritratti.
     */
    public Fotografia(Integer idFoto, String dispositivo, LocalDate dataDiScatto, LocalDate dataDiEliminazione, boolean visibility, Utente autore,
                      Luogo luogo, ArrayList<Galleria> galleriaContenitrice, ArrayList<Soggetto> soggetti){

        this.idFoto = idFoto;
        this.dispositivo = dispositivo;
        this.dataDiScatto = dataDiScatto;
        this.dataDiEliminazione = dataDiEliminazione;
        this.visibility = visibility;

        if(luogo == null){
            this.luogo = new Luogo("", "", new ArrayList<>());
        }else{
            this.luogo = luogo;
        }

        if(galleriaContenitrice != null){
            this.galleriaContenitrice = galleriaContenitrice;
        }else{
            this.galleriaContenitrice = new ArrayList<>();
        }

        this.fotoComponeVideo = new ArrayList<>();

        if(soggetti != null){
            this.soggetti = soggetti;
        }else{
            this.soggetti = new ArrayList<>();
        }
    }

    /** @return L'ID univoco della fotografia. */
    public Integer getIdFoto() {
        return idFoto;
    }

    /** @param idFoto L'ID univoco da assegnare. */
    public void setIdFoto(Integer idFoto) {
        this.idFoto = idFoto;
    }

    /** @return Il nome del dispositivo di scatto. */
    public String getDispositivo() {
        return dispositivo;
    }

    /** @param dispositivo Il nome del dispositivo da impostare. */
    public void setDispositivo(String dispositivo) {
        this.dispositivo = dispositivo;
    }

    /** @return La data dello scatto. */
    public LocalDate getDataDiScatto() {
        return dataDiScatto;
    }

    /** @param dataDiScatto La data dello scatto da impostare. */
    public void setDataDiScatto(LocalDate dataDiScatto) {
        this.dataDiScatto = dataDiScatto;
    }

    /** @return La data di eliminazione, o {@code null} se attiva. */
    public LocalDate getDataDiEliminazione() {
        return dataDiEliminazione;
    }

    /** @param dataDiEliminazione La data in cui la foto viene segnata come eliminata. */
    public void setDataDiEliminazione(LocalDate dataDiEliminazione) {
        this.dataDiEliminazione = dataDiEliminazione;
    }

    /** @return {@code true} se la foto è visibile, {@code false} altrimenti. */
    public boolean isVisibile() {
        return visibility;
    }

    /** @param visibility Il flag di visibilità da impostare. */
    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    /** @return L'autore della fotografia. */
    public Utente getAutore() {
        return autore;
    }

    /** @param autore L'utente da associare come autore. */
    public void setAutore(Utente autore) {
        this.autore = autore;
    }

    /** @return Il luogo associato alla fotografia. */
    public Luogo getLuogo() {
        return luogo;
    }

    /** @param luogo Il luogo da associare alla fotografia. */
    public void setLuogo(Luogo luogo) {
        this.luogo = luogo;
    }

    /** @return La lista delle gallerie che contengono la foto. */
    public ArrayList<Galleria> getGalleriaContenitrice() {
        return galleriaContenitrice;
    }

    /**
     * Aggiunge la fotografia a una galleria, se non già presente.
     * @param galleria La galleria di destinazione.
     * @return {@code true} se aggiunta con successo, {@code false} se già presente.
     */
    public boolean addGalleriaContenitrice(Galleria galleria){
        boolean aggiunta = false;

        if(!(this.galleriaContenitrice.contains(galleria))){
            this.galleriaContenitrice.add(galleria);

            aggiunta = true;
        }

        return aggiunta;
    }

    /**
     * Rimuove la fotografia da una galleria.
     * @param galleria La galleria da cui rimuovere la foto.
     * @return {@code true} se rimossa, {@code false} se non era presente.
     */
    public boolean removeGalleriaContenitrice(Galleria galleria){
        boolean rimozione = false;

        if(this.galleriaContenitrice.contains(galleria)){
            this.galleriaContenitrice.remove(galleria);

            rimozione = true;
        }

        return rimozione;
    }

    /** @return La lista dei soggetti ritratti nella foto. */
    public ArrayList<Soggetto> getSoggetti() {
        return soggetti;
    }

    /**
     * Associa un nuovo soggetto alla fotografia.
     * @param soggetto Il soggetto da aggiungere.
     * @return {@code true} se aggiunto correttamente.
     */
    public boolean addSoggetto(Soggetto soggetto){
        boolean aggiunta = false;

        if(!(this.soggetti.contains(soggetto))){
            this.soggetti.add(soggetto);

            aggiunta = true;
        }

        return aggiunta;
    }

    /**
     * Rimuove un soggetto dall'associazione con la foto.
     * @param soggetto Il soggetto da rimuovere.
     * @return {@code true} se rimosso correttamente.
     */
    public boolean removeSoggetto(Soggetto soggetto){
        boolean rimozione = false;

        if(this.soggetti.contains(soggetto)){
            this.soggetti.remove(soggetto);

            rimozione = true;
        }

        return rimozione;
    }

    /** @return La lista dei video che includono questa fotografia. */
    public ArrayList<Video> getFotoComponeVideo() {
        return fotoComponeVideo;
    }

    /**
     * Registra l'appartenenza della foto a un progetto video.
     * @param video Il video a cui aggiungere la foto.
     * @return {@code true} se l'operazione ha successo.
     */
    public boolean addVideo(Video video){
        boolean aggiunta = false;

        if(!(this.fotoComponeVideo.contains(video))){
            this.fotoComponeVideo.add(video);

            aggiunta = true;
        }

        return aggiunta;
    }

    /**
     * Rimuove la foto dalla composizione di un video.
     * @param video Il video da cui dissociare la foto.
     * @return {@code true} se rimossa correttamente.
     */
    public boolean removeVideo(Video video){
        boolean rimozione = false;

        if(this.fotoComponeVideo.contains(video)){
            this.fotoComponeVideo.remove(video);

            rimozione = true;
        }

        return rimozione;
    }

    /**
     * Confronta questa fotografia con un altro oggetto per verificarne l'uguaglianza.
     * Il confronto avviene basandosi esclusivamente sull'{@code idFoto}.
     * @param obj L'oggetto da confrontare.
     * @return {@code true} se gli oggetti hanno lo stesso ID, {@code false} altrimenti.
     */
    @Override
    public boolean equals(final Object obj){
        if(obj == this) return true;

        if(!(obj instanceof Fotografia fotografia)) return false;

        return Objects.equals(idFoto, fotografia.idFoto);
    }
}