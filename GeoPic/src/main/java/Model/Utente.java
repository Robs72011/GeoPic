package Model;

import java.util.ArrayList;

public class Utente {

    private String idUtente;
    private String username;
    private boolean isAdmin;

    //Rappresenta la relazione "Rappresenta" tra utente e soggetto
    private boolean isSoggetto;

    //Rappresenta la relazione "Scatta" tra utente e foto
    private ArrayList<Fotografia> fotoScattate;

    //Rappresenta la relazione "Possiede" tra utente e gallerie
    private ArrayList<Galleria> galleriePossedute;

    //Rappresenta la relazione "partecipa" tra utente e gallerie condivise
    private ArrayList<GalleriaCondivisa> utentePartecipaGalleriaCondivisa;

    //costruttore
    public Utente (String idutente, String username, boolean isAdmin){
        this.idUtente = idutente;
        this.username = username;
        this.isAdmin = isAdmin;

        this.isSoggetto = false;
        this.fotoScattate = new ArrayList<>();
        this.galleriePossedute = new ArrayList<>();
        this.utentePartecipaGalleriaCondivisa = new ArrayList<>();
    }
}