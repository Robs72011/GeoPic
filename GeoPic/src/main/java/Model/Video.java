package Model;

import java.util.ArrayList;

public class Video {

    private String idvideo;
    private String descrizione;
    private String nome;

    //Rappresenta l'associazione "Compone" tra video e foto
    private ArrayList<Foto> compostoDaFoto;
}