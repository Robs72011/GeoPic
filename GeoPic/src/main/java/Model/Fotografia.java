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

    //costruttore manca luogo e fotoComponeVodeo
    public Fotografia(String idFoto, String dispositivo, LocalDate dataDiScatto, boolean visibility, Utente autore, ArrayList<Galleria> galleriaContenitrice, ArrayList<Soggetto> soggetti){

        this.idFoto=idFoto;
        this.dispositivo=dispositivo;
        this.dataDiScatto=dataDiScatto;
        this.dataDiEliminazione=null;
        this.visibility=visibility;

        this.autore=autore;
        this.luogo=null;
        this.galleriaContenitrice=galleriaContenitrice;
        this.fotoComponeVideo=new ArrayList<>();
        this.soggetti=soggetti;

    }



}
