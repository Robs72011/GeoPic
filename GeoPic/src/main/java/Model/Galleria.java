package Model;

import java.util.ArrayList;
import java.util.Objects;

public abstract class Galleria{

    private String idGalleria;
    private String nomeGalleria; //galleria privata: nome proprietario + "'s Gallery"

    //Rappresenta la relazione "possiede" tra galleria e utente
    private Utente proprietario;
    //Rappresenta la relazione "Contiene" tra galleria e foto, indicando le foto che sono contenute nella galleria
    private ArrayList<Fotografia> fotoContenute = new ArrayList<>();

    public Galleria(String idGalleria, String nomeGalleria, Utente proprietario, ArrayList<Fotografia> fotoContenute) {
        this.idGalleria = idGalleria;
        this.nomeGalleria = nomeGalleria;

        if(proprietario != null)
            this.proprietario = proprietario;
        else
            this.proprietario = null;

        if(fotoContenute != null){
            this.fotoContenute = fotoContenute;
        }else{
            this.fotoContenute = new ArrayList<>();
        }
    }

    public String getIdGalleria() {
        return idGalleria;
    }

    public void setIdGalleria(String idGalleria) {
        this.idGalleria = idGalleria;
    }

    public String getNomeGalleria() {
        return nomeGalleria;
    }

    public void setNomeGalleria(String nomeGalleria) {
        this.nomeGalleria = nomeGalleria;
    }

    public Utente getProprietario() {
        return proprietario;
    }

    public void setProprietario(Utente proprietario) {
        this.proprietario = proprietario;
    }

    public ArrayList<Fotografia> getFotoContenute() {
        return fotoContenute;
    }

    public boolean addFotoAGalleria(Fotografia foto){
        boolean aggiunta = false;

        if(!(this.fotoContenute.contains(foto))){
            this.fotoContenute.add(foto);

            aggiunta = true;
        }

        return aggiunta;
    }

    public boolean removeFotoDAGalleria(Fotografia foto){
        boolean rimozione = false;

        if(this.fotoContenute.contains(foto)){
            this.fotoContenute.remove(foto);

            rimozione = true;
        }

        return rimozione;
    }

    @Override
    public boolean equals(final Object obj){
        if(obj == this) return true;

        if(obj == null || !(obj instanceof Galleria)) return false;

        Galleria galleria = (Galleria) obj;

        return Objects.equals(idGalleria, galleria.idGalleria);
    }
}