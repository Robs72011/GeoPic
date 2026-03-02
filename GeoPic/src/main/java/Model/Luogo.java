package Model;

import java.util.ArrayList;
import java.util.Objects;

public class Luogo {

    private String coordinate;
    private String nomeMnemonico;

    //rappresenta la relazione Raffigura tra foto e Luogo
    private ArrayList<Fotografia> luogoRaffiguratoIn;

    //costruttore
    public Luogo(String coordinate, String nomeMnemonico, ArrayList<Fotografia> luogoRaffiguratoIn) {
        this.coordinate = coordinate;
        this.nomeMnemonico = nomeMnemonico;

        if(luogoRaffiguratoIn != null){
            this.luogoRaffiguratoIn = luogoRaffiguratoIn;
        }else{
            this.luogoRaffiguratoIn = new ArrayList<>();
        }
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public String getNomeMnemonico() {
        return nomeMnemonico;
    }

    public void setNomeMnemonico(String nomeMnemonico) {
        this.nomeMnemonico = nomeMnemonico;
    }

    public ArrayList<Fotografia> getLuogoRaffiguratoIn() {
        return  luogoRaffiguratoIn;
    }

    public boolean addLuogoRaffiguratoIn(Fotografia f){
        boolean aggiunta = false;

        if(!(this.luogoRaffiguratoIn.contains(f))){
            this.luogoRaffiguratoIn.add(f);

            aggiunta = true;
        }

        return aggiunta;
    }

    @Override
    public boolean equals(final Object obj){
        if(obj == this) return true;

        if(obj == null || !(obj instanceof Luogo)) return false;

        Luogo luogo = (Luogo) obj;

        return Objects.equals(coordinate, luogo.coordinate);
    }
}