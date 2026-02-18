package Model;

import java.utils.ArrayList;

public class Luogo {

    private String coordinate;
    private String nomeMnemonico;

    //rappresenta la relazione Raffigura tra foto e Luogo
    private ArrayList<Fotografia> raffigura;

    //costruttore
    public Luogo(String coordinate, String nomeMnemonico) {
        this.coordinate = coordinate;
        this.nomeMnemonico = nomeMnemonico;

        this.raffigura = new ArrayList<>();
    }

}