package Model;

import java.util.ArrayList;

public class Utente {

    private String idutente;
    private String username;
    private boolean isadmin;



    //Rappresenta la relazione "Rappresenta" tra utente e soggetto
    private Soggetto rappresentoDaSoggetto;

    //Rappresenta la relazione "Scatta" tra utente e foto
    private ArrayList<Fotografia> fotoScattate;

    //Rappresenta la relazione "Possiede" tra utente e gallerie
    private ArrayList<Galleria> galleriePossedute;

    //Rappresenta la relazione "partecipa" tra utente e gallerie condivise
    private ArrayList<GalleriaCondivisa> utentePartecipaGalleriaCondivisa;

    //costruttore
    public Utente (String idutente, String username, boolean isadmin){
        this.idutente = idutente;
        this.username = username;
        this.isadmin = isadmin;

        this.rappresentoDaSoggetto = new ArrayList<>();
        this.fotoScattate = new ArrayList<>();
        this.galleriePossedute = new ArrayList<>();
        this.utentePartecipaGalleriaCondivisa = new ArrayList<>();
    }
}