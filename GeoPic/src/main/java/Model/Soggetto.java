package Model;

import java.util.Objects;

public class Soggetto {
    private String nomeSoggetto;
    private String categoria;

    private Utente utenteRappresentato;

    //costruttore c'è tutto
    public Soggetto(String nomeSoggetto, String categoria, Utente utenteRappresentato) {
        this.nomeSoggetto = nomeSoggetto;
        this.categoria = categoria;

        if(utenteRappresentato != null){
            this.utenteRappresentato = utenteRappresentato;
        }else{
            this.utenteRappresentato = null;
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
}