package Model;

import java.time.LocalDate;
import java.util.*;

public class Fotografia {

    private String idFoto;
    private String dispositivo;
    private LocalDate dataDiScatto;
    private LocalDate dataDiEliminazione = null;
    private boolean visibility = true;

    //Rappresenta l'associazione "Scattata" tra foto e autore
    private Utente autore;

    //Rappresenta l'associazione "Raffigura" tra foto e luogo
    private Luogo luogo;

    //Rappresenta l'associazione "Contenuta" tra foto e galleria
    private ArrayList<Galleria> galleriaContenitrice;

    //Rappresenta l'associazione "Compone" tra foto e video
    private ArrayList<Video> fotoComponeSlideshows;

    //Rappresenta l'associazione "Mostra" tra foto e soggetto
    private ArrayList<Soggetto> soggetti;
    
    

}
