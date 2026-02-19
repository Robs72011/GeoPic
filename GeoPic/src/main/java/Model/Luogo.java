package Model;

import java.util.ArrayList;

public class Luogo {

    private String coordinate;
    private String nomeMnemonico;

    //rappresenta la relazione Raffigura tra foto e Luogo
    private ArrayList<Fotografia> luogoRaffiguratoIn;

    //costruttore
    public Luogo(String coordinate, String nomeMnemonico) {
        this.coordinate = coordinate;
        this.nomeMnemonico = nomeMnemonico;

        this.luogoRaffiguratoIn = new ArrayList<>();
    }

}