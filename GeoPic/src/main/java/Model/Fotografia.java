package Model;

import java.time.LocalDate;
import java.util.*;

public class Fotografia {

    private String idFoto;
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
    public Fotografia(String idFoto, String dispositivo, LocalDate dataDiScatto, boolean visibility, Utente autore, ArrayList<Galleria> galleriaContenitrice, ArrayList<Soggetto> soggetti){

        this.idFoto=idFoto;
        this.dispositivo=dispositivo;
        this.dataDiScatto=dataDiScatto;
        this.dataDiEliminazione=null;
        this.visibility=visibility;

        this.luogo=null;

        if(galleriaContenitrice != null){
            this.galleriaContenitrice=galleriaContenitrice;
        }else{
            this.galleriaContenitrice=new ArrayList<>();
        }

        this.fotoComponeVideo=new ArrayList<>();

        if(soggetti != null){
            this.soggetti=soggetti;
        }else{
            this.soggetti = snew ArrayList<>();
        }

    }

    public String getIdFoto() {
        return idFoto;
    }

    public void setIdFoto(String idFoto) {
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

        if(this.galleriaContenitrice == null){
            this.galleriaContenitrice = new ArrayList<>();
        }

        if(!(this.galleriaContenitrice.contains(galleria))){
            this.galleriaContenitrice.add(galleria);

            aggiunta = true;
        }

        return aggiunta;
    }
}
