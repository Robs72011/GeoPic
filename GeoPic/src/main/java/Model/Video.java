package Model;

public class Video {

    private String IDvideo;
    private String descrizione;
    private String Nome;

    //Rappresenta l'associazione "Compone" tra video e foto
    private ArrayList<Foto> compostoDaFoto;
}