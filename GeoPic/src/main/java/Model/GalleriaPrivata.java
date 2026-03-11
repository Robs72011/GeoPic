package Model;

import java.util.ArrayList;

/**
 * Rappresenta una galleria fotografica di tipo privato.
 * Estende la classe {@link Galleria} aggiungendo la possibilità di gestire
 * una collezione di video salvati al suo interno.
 *
 *
 */
public class GalleriaPrivata extends Galleria{

    /** * Elenco dei video salvati all'interno della galleria privata
     * (Associazione "Salvato in").
     */
    private final ArrayList<Video> videos;

    /**
     * Costruttore completo per la GalleriaPrivata.
     * Inizializza le informazioni di base tramite la superclasse {@link Galleria}
     * e configura la lista dei video associati.
     * @param idGalleria Identificativo univoco della galleria.
     * @param nomeGalleria Nome descrittivo della galleria.
     * @param proprietario L'utente {@link Utente} titolare della galleria.
     * @param fotoContenute Lista iniziale di fotografie (se {@code null}, viene inizializzata vuota).
     * @param videos Lista iniziale di video (se {@code null}, viene inizializzata vuota).
     */
    public GalleriaPrivata(Integer idGalleria, String nomeGalleria, Utente proprietario,
                           ArrayList<Fotografia> fotoContenute, ArrayList<Video> videos){
        super(idGalleria, nomeGalleria, proprietario, fotoContenute);

        if(videos !=  null){
            this.videos = videos;
        }else {
            this.videos = new ArrayList<>();
        }
    }

    /**
     * Recupera la lista dei video contenuti nella galleria.
     * @return Un {@link ArrayList} di oggetti {@link Video}.
     */
    public ArrayList<Video> getVideos(){
        return this.videos;
    }

    /**
     * Aggiunge un video alla galleria se non è già presente.
     * @param video L'oggetto {@link Video} da aggiungere.
     * @return {@code true} se il video è stato aggiunto correttamente,
     * {@code false} se era già presente nella lista.
     */
    public boolean addVideo(Video video){
        boolean aggiunta = false;

        if(!(this.videos.contains(video))){
            this.videos.add(video);

            aggiunta = true;
        }

        return aggiunta;
    }

    /**
     * Rimuove un video dalla galleria.
     * @param video L'oggetto {@link Video} da rimuovere.
     * @return {@code true} se il video è stato rimosso,
     * {@code false} se il video non è stato trovato nella lista.
     */
    public boolean removeVideo(Video video){
        boolean rimozione = false;

        if(this.videos.contains(video)){
            this.videos.remove(video);

            rimozione = true;
        }

        return rimozione;
    }

}