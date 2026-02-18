package Model;

import java.util.ArrayList;

public abstract class Galleria{

    private String idgalleria;
    private String nomeGalleria; //galleria privata: nome proprietario + "'s Gallery"

    //Rappresenta la relazione "possiede" tra galleria e utente
    private Utente proprietario;
    //Rappresenta la relazione "Contiene" tra galleria e foto, indicando le foto che sono contenute nella galleria
    private ArrayList<Fotografia> contenute = null;
}