package Model;

import javax.swing.*;
import java.util.ArrayList;

public class Video {

    private String idVideo;
    private String descrizione;
    private String nome;

    //Rappresenta l'associazione "Compone" tra video e foto
    private ArrayList<Fotografia> compostoDaFoto;

    //rappresenta l'associazione "Salvato in" tra video e galleria
    private Galleria galleria;

    //costruttore
    public Video(String idVideo, String descrizione, String nome, ArrayList<Fotografia> compostoDaFoto, Galleria galleria){

        this.idVideo=idVideo;
        this.descrizione=descrizione;

        this.compostoDaFoto=compostoDaFoto;
        this.galleria=galleria;




    }
}