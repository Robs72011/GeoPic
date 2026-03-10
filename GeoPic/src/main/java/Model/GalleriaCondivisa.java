package Model;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

public class GalleriaCondivisa extends Galleria{

    //Rappresenta la relazione "Partecipa" tra galleria e utente, indicando chi sono i partecipanti della galleria condivisa
    private ArrayList<Utente> partecipanti = null;

    public GalleriaCondivisa(Integer idGalleria, String nomeGalleria, Utente proprietario,
                             ArrayList<Fotografia> fotoContenute, ArrayList<Utente> partecipanti){
        super(idGalleria, nomeGalleria, proprietario, fotoContenute);

        if(partecipanti !=  null){
            this.partecipanti = partecipanti;
        }else {
            this.partecipanti = new ArrayList<>();
        }
    }

    public ArrayList<Utente> getPartecipanti(){
        return  this.partecipanti;
    }

    public boolean addPartecipante(Utente partecipante){
        boolean aggiunta = false;

        if(!(this.partecipanti.contains(partecipante))){
            this.partecipanti.add(partecipante);

            aggiunta = true;
        }

        return aggiunta;
    }

    public boolean removePartecipante(Utente partecipante){
        boolean rimozione = false;

        if(this.partecipanti.contains(partecipante)){
            this.partecipanti.remove(partecipante);

            rimozione = true;
        }

        return rimozione;
    }
}