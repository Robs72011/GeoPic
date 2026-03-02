package Model;

import java.util.ArrayList;
import java.util.Objects;

public class Utente {

    private String idUtente;
    private String username;
    private String password;
    private boolean isAdmin;

    //Rappresenta la relazione "Rappresenta" tra utente e soggetto
    private boolean isSoggetto;

    //Rappresenta la relazione "Scatta" tra utente e foto
    private ArrayList<Fotografia> fotoScattate;

    //Rappresenta la relazione "Possiede" tra utente e gallerie
    private ArrayList<Galleria> galleriePossedute;

    //Rappresenta la relazione "partecipa" tra utente e gallerie condivise
    private ArrayList<GalleriaCondivisa> utentePartecipaGalleriaCondivisa;

    //costruttore manca fotoScattate e utentePartecipaGalleriaCondivisa
    public Utente (String idutente, String username, String password, boolean isAdmin, boolean isSoggetto,
                   ArrayList<Fotografia> fotoScattate, ArrayList<Galleria> galleriePossedute,
                   ArrayList<GalleriaCondivisa> utentePartecipaGalleriaCondivisa){
        this.idUtente = idutente;
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;

        this.isSoggetto = isSoggetto;

        if(fotoScattate == null) {
            this.fotoScattate = new ArrayList<>();
        }else{
            this.fotoScattate = fotoScattate;
        }

        if(galleriePossedute == null) {
            this.galleriePossedute = new ArrayList<>();
        }else{
            this.galleriePossedute = galleriePossedute;
        }

        if(utentePartecipaGalleriaCondivisa == null) {
            this.utentePartecipaGalleriaCondivisa = new ArrayList<>();
        }else{
            this.utentePartecipaGalleriaCondivisa = utentePartecipaGalleriaCondivisa;
        }
    }

    public String getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(String idUtente) {
        this.idUtente = idUtente;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isSoggetto() {
        return isSoggetto;
    }

    public void setSoggetto(boolean soggetto) {
        isSoggetto = soggetto;
    }

    public ArrayList<Fotografia> getFotoScattate() {
        return fotoScattate;
    }

    public boolean addFotoScattate(Fotografia f){
        boolean aggiunta = false;

        if(!(this.fotoScattate.contains(f))){
            this.fotoScattate.add(f);

            aggiunta = true;
        }

        return aggiunta;
    }

    public boolean addGalleriePossedute(Galleria g){
        boolean aggiunta = false;

        if(!(this.galleriePossedute.contains(g))){
            this.galleriePossedute.add(g);

            aggiunta = true;
        }

        return aggiunta;
    }

    public boolean removeFotoScattata(Fotografia f){
        boolean rimozione = false;

        if(this.fotoScattate.contains(f)){
            this.fotoScattate.remove(f);

            rimozione = true;
        }

        return  rimozione;
    }

    public boolean equals(final Object obj){
        if(obj == this) return true;

        if(obj == null || !(obj instanceof Utente)) return false;

        Utente utente = (Utente) obj;

        return Objects.equals(idUtente, utente.idUtente);
    }
}