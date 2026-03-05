package Model;

import java.time.LocalDate;
import java.util.*;

public class Fotografia {

    private Integer idFoto;
    private String dispositivo;
    private LocalDate dataDiScatto;
    private LocalDate dataDiEliminazione = null;
    private boolean visibility = true;

    //Rappresenta l'associazione "Scattata" tra foto e utente
    private Utente autore;

    //Rappresenta l'associazione "Raffigura" tra foto e luogo
    private Luogo luogo;

    //Rappresenta l'associazione "Contenuta" tra foto e galleria
    private ArrayList<Galleria> galleriaContenitrice;

    //Rappresenta l'associazione "Compone" tra foto e video
    private ArrayList<Video> fotoComponeVideo;

    //Rappresenta l'associazione "Mostra" tra foto e soggetto
    private ArrayList<Soggetto> soggetti;

    //costruttore manca luogo e fotoComponeVideo
    public Fotografia(Integer idFoto, String dispositivo, LocalDate dataDiScatto, LocalDate dataDiEliminazione, boolean visibility, Utente autore,
                      Luogo luogo, ArrayList<Galleria> galleriaContenitrice, ArrayList<Soggetto> soggetti){

        this.idFoto = idFoto;
        this.dispositivo = dispositivo;
        this.dataDiScatto = dataDiScatto;
        if(dataDiEliminazione == null){
            this.dataDiEliminazione = null;
        }else{
            this.dataDiEliminazione = dataDiEliminazione;
        }
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

    public Integer getIdFoto() {
        return idFoto;
    }

    public void setIdFoto(Integer idFoto) {
        this.idFoto = idFoto;
    }

    public String getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(String dispositivo) {
        this.dispositivo = dispositivo;
    }

    public LocalDate getDataDiScatto() {
        return dataDiScatto;
    }

    public void setDataDiScatto(LocalDate dataDiScatto) {
        this.dataDiScatto = dataDiScatto;
    }

    public LocalDate getDataDiEliminazione() {
        return dataDiEliminazione;
    }

    public void setDataDiEliminazione(LocalDate dataDiEliminazione) {
        this.dataDiEliminazione = dataDiEliminazione;
    }

    public boolean isVisibile() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public Utente getAutore() {
        return autore;
    }

    public void setAutore(Utente autore) {
        this.autore = autore;
    }

    public Luogo getLuogo() {
        return luogo;
    }

    public void setLuogo(Luogo luogo) {
        this.luogo = luogo;
    }

    public ArrayList<Galleria> getGalleriaContenitrice() {
        return galleriaContenitrice;
    }

    public boolean addGalleriaContenitrice(Galleria galleria){
        boolean aggiunta = false;

        if(!(this.galleriaContenitrice.contains(galleria))){
            this.galleriaContenitrice.add(galleria);

            aggiunta = true;
        }

        return aggiunta;
    }

    public boolean removeGalleriaContenitrice(Galleria galleria){
        boolean rimozione = false;

        if(this.galleriaContenitrice.contains(galleria)){
            this.galleriaContenitrice.remove(galleria);

            rimozione = true;
        }

        return rimozione;
    }

    //soggetti
    public ArrayList<Soggetto> getSoggetti() {
        return soggetti;
    }

    public boolean addSoggetto(Soggetto soggetto){
        boolean aggiunta = false;

        if(!(this.soggetti.contains(soggetto))){
            this.soggetti.add(soggetto);

            aggiunta = true;
        }

        return aggiunta;
    }

    public boolean removeSoggetto(Soggetto soggetto){
        boolean rimozione = false;

        if(this.soggetti.contains(soggetto)){
            this.soggetti.remove(soggetto);

            rimozione = true;
        }

        return rimozione;
    }


    //foto compone vide

    public ArrayList<Video> getFotoComponeVideo() {
        return fotoComponeVideo;
    }


    //Funzione che aggiunge all'array list di video in cui la foto appare
    public boolean addVideo(Video video){
        boolean aggiunta = false;

        if(!(this.fotoComponeVideo.contains(video))){
            this.fotoComponeVideo.add(video);

            aggiunta = true;
        }

        return aggiunta;
    }

    public boolean removeVideo(Video video){
        boolean rimozione = false;

        if(this.fotoComponeVideo.contains(video)){
            this.fotoComponeVideo.remove(video);

            rimozione = true;
        }

        return rimozione;
    }

    @Override
    public boolean equals(final Object obj){
        if(obj == this) return true;

        if(obj == null || !(obj instanceof Fotografia)) return false;

        Fotografia fotografia = (Fotografia) obj;

        return Objects.equals(idFoto, fotografia.idFoto);
    }
}