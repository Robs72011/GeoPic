package Model;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Rappresenta un luogo geografico associato a una o più fotografie.
 * Gestisce la localizzazione tramite coordinate e un nome identificativo leggibile (mnemonico).
 */
public class Luogo {

    /** Stringa rappresentante le coordinate geografiche (es. Latitudine/Longitudine). */
    private String coordinate;

    /** Nome descrittivo o etichetta del luogo (es. "Piazza Duomo"). */
    private String nomeMnemonico;

    /** Elenco delle fotografie che ritraggono questo luogo (Associazione "Raffigura"). */
    private final ArrayList<Fotografia> luogoRaffiguratoIn;

    /**
     * Costruttore della classe Luogo.
     * @param coordinate Stringa contenente i dati di localizzazione.
     * @param nomeMnemonico Etichetta testuale del luogo.
     * @param luogoRaffiguratoIn Lista iniziale di fotografie scattate in questo luogo.
     * Se {@code null}, viene inizializzata una lista vuota.
     */
    public Luogo(String coordinate, String nomeMnemonico, ArrayList<Fotografia> luogoRaffiguratoIn) {
        this.coordinate = coordinate;
        this.nomeMnemonico = nomeMnemonico;

        if(luogoRaffiguratoIn != null){
            this.luogoRaffiguratoIn = luogoRaffiguratoIn;
        }else{
            this.luogoRaffiguratoIn = new ArrayList<>();
        }
    }

    /** @return Le coordinate geografiche del luogo. */
    public String getCoordinate() {
        return coordinate;
    }

    /** @param coordinate Le coordinate da impostare per questo luogo. */
    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    /** @return Il nome mnemonico associato al luogo. */
    public String getNomeMnemonico() {
        return nomeMnemonico;
    }

    /** @param nomeMnemonico Il nome descrittivo da impostare. */
    public void setNomeMnemonico(String nomeMnemonico) {
        this.nomeMnemonico = nomeMnemonico;
    }

    /** @return La lista di oggetti {@link Fotografia} scattate in questo luogo. */
    public ArrayList<Fotografia> getLuogoRaffiguratoIn() {
        return  luogoRaffiguratoIn;
    }

    /**
     * Aggiunge una fotografia alla lista delle foto associate a questo luogo.
     * Evita duplicati controllando se la foto è già presente.
     * @param f La {@link Fotografia} da associare.
     * @return {@code true} se la fotografia è stata aggiunta, {@code false} se era già presente.
     */
    public boolean addLuogoRaffiguratoIn(Fotografia f){
        boolean aggiunta = false;

        if(!(this.luogoRaffiguratoIn.contains(f))){
            this.luogoRaffiguratoIn.add(f);

            aggiunta = true;
        }

        return aggiunta;
    }

    /**
     * Verifica l'uguaglianza tra due luoghi.
     * Due luoghi sono considerati uguali se hanno le stesse coordinate.
     * @param obj L'oggetto da confrontare.
     * @return {@code true} se le coordinate coincidono, {@code false} altrimenti.
     */
    @Override
    public boolean equals(final Object obj){
        if(obj == this) return true;

        if(!(obj instanceof Luogo luogo)) return false;

        return Objects.equals(coordinate, luogo.coordinate);
    }
}