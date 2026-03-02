package Model;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Objects;

public class Video {

    private String idVideo;
    private String descrizione;
    private String titolo;

    //Rappresenta l'associazione "Compone" tra video e foto
    private ArrayList<Fotografia> compostoDaFoto;

    //rappresenta l'associazione "Salvato in" tra video e galleria
    private Galleria galleria;

    //costruttore
    public Video(String idVideo, String descrizione, String titolo, ArrayList<Fotografia> compostoDaFoto, Galleria galleria){

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

    public String getIdVideo() {
        return idVideo;
    }

    public void setIdVideo(String idVideo) {
        this.idVideo = idVideo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public ArrayList<Fotografia> getCompostoDaFoto() {
        return compostoDaFoto;
    }

    public void setGalleria(Galleria galleria) {
        this.galleria = galleria;
    }

    public boolean addFotoAVideo(Fotografia foto){
        boolean aggiunta = false;

        if(!(this.compostoDaFoto.contains(foto))){
            this.compostoDaFoto.add(foto);

            aggiunta = true;
        }

        return aggiunta;
    }

    public boolean removeFotoAVideo(Fotografia foto){
        boolean rimossa = false;

        if(this.compostoDaFoto.contains(foto)){
            this.compostoDaFoto.remove(foto);

            rimossa = true;
        }

        return rimossa;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;

        if(obj == null || !(obj instanceof Video)) return false;

        Video video = (Video) obj;

        return Objects.equals(idVideo, video.getIdVideo());
    }
}