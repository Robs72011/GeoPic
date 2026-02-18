package Model;

public class Utente {

    private string IDutente;
    private string Username;
    private boolean Isadmin;



    //Rappresenta la relazione "Rappresenta" tra utente e soggetto
    private Soggetto rappresentoDaSoggetto;

    //Rappresenta la relazione "Scatta" tra utente e foto
    private ArrayList<Fotografia> fotoScattate;

    //Rappresenta la relazione "Possiede" tra utente e gallerie
    private ArrayList<Galleria> galleriePossedute;

    //Rappresenta la relazione "partecipa" tra utente e gallerie condivise
    private ArrayList<GalleriaCondivisa> utentePartecipaGalleriaCondivisa;
    
}