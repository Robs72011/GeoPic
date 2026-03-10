package Model;

import java.util.ArrayList;
import java.util.Objects;

public class Soggetto {
    private String nomeSoggetto;
    private String categoria;

    private Utente utenteRappresentato;

    private ArrayList<Fotografia> fotoInCuiAppare;

    //costruttore c'è tutto
    public Soggetto(String nomeSoggetto, String categoria, Utente utenteRappresentato,
                    ArrayList<Fotografia> fotoInCuiAppare) {
        this.nomeSoggetto = nomeSoggetto;
        this.categoria = categoria;

        if(utenteRappresentato != null){
            this.utenteRappresentato = utenteRappresentato;
        }else{
            this.utenteRappresentato = null;
        }

        if(fotoInCuiAppare != null){
            this.fotoInCuiAppare = fotoInCuiAppare;
        }else{
            this.fotoInCuiAppare = new ArrayList<>();
        }
    }

    //costruttore manca utenteRappresentato
    public Soggetto(String nomeSoggetto, String categoria) {
        this.nomeSoggetto = nomeSoggetto;
        this.categoria = categoria;
        this.utenteRappresentato = null;
    }

    public String getNomeSoggetto() {
        return nomeSoggetto;
    }
    public void setNomeSoggetto(String nomeSoggetto) {
        this.nomeSoggetto = nomeSoggetto;
    }
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public boolean isUtente(){
        //Se il soggetto è utente, ritorna true
        return utenteRappresentato != null;
    }

    public Utente getUtenteRappresentato() {
        return utenteRappresentato;
    }

    public void setUtenteRappresentato(Utente utente) {
        this.utenteRappresentato = utente;
    }

    @Override
    public boolean equals(final Object obj){
        if(obj == this) return true;

        if(obj == null || !(obj instanceof Soggetto)) return false;

        Soggetto soggetto = (Soggetto) obj;

        return Objects.equals(nomeSoggetto, soggetto.nomeSoggetto);
    }

    public boolean addFotoInCuiAppare(Fotografia fotografia){
        boolean aggiunta = false;

        if(!(this.fotoInCuiAppare.contains(fotografia))){
            this.fotoInCuiAppare.add(fotografia);

            aggiunta = true;
        }

        return aggiunta;
    }
}