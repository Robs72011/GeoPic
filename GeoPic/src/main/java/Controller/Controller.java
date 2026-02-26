package Controller;

import ImplementazioniPostgresDAO.*;
import Model.*;

import java.sql.*;
import java.util.ArrayList;

public class Controller {

    private ComponePostgresDAO componePostgresDAO;
    private ContienePostgresDAO contienePostgresDAO;
    private FotografiaPostgresDAO fotografiaPostgresDAO;
    private GalleriaPostgresDAO galleriaPostgresDAO;
    private LuogoPostgresDAO luogoPostgresDAO;
    private MostraPostgresDAO  mostraPostgresDAO;
    private PartecipaPostgresDAO partecipaPostgresDAO;
    private SoggettoPostgresDAO soggettoPostgresDAO;
    private UtentePostgresDAO utentePostgresDAO;
    private VideoPostgresDAO videoPostgresDAO;

    private ArrayList<Fotografia> fotografieInMemory;
    private ArrayList<GalleriaPrivata> galleriePrivateInMemory;
    private ArrayList<GalleriaCondivisa> gallerieCondiviseInMemory;
    private ArrayList<Luogo> luoghiInMemory;
    private ArrayList<Soggetto> soggettiInMemory;
    private ArrayList<Utente> utentiInMemory;
    private ArrayList<Video> videosInMemory;

    private Utente loggedInUtente;

    public Controller(){
        try{

            Connection connessione = ConnessioneDataBasePostgres.getIstanza().getConnesione();

            this.componePostgresDAO = new ComponePostgresDAO(connessione);
            this.contienePostgresDAO = new ContienePostgresDAO(connessione);
            this.fotografiaPostgresDAO = new FotografiaPostgresDAO(connessione);
            this.galleriaPostgresDAO = new GalleriaPostgresDAO(connessione);
            this.luogoPostgresDAO = new LuogoPostgresDAO(connessione);
            this.mostraPostgresDAO = new MostraPostgresDAO(connessione);
            this.partecipaPostgresDAO = new PartecipaPostgresDAO(connessione);
            this.soggettoPostgresDAO = new SoggettoPostgresDAO(connessione);
            this.utentePostgresDAO = new UtentePostgresDAO(connessione);
            this.videoPostgresDAO = new VideoPostgresDAO(connessione);

            this.fotografieInMemory = new ArrayList<>();
            this.galleriePrivateInMemory = new ArrayList<>();
            this.gallerieCondiviseInMemory = new ArrayList<>();
            this.luoghiInMemory = new ArrayList<>();
            this.soggettiInMemory = new ArrayList<>();
            this.utentiInMemory = new ArrayList<>();
            this.videosInMemory = new ArrayList<>();

        }catch(SQLException sqle){
            System.err.println("Impossibile connettersi al DB.");
            sqle.printStackTrace();
        }
    }

    public boolean loadUtenti(){

        utentiInMemory.clear();

        ArrayList<String> tmpIdUtente = new ArrayList<>();
        ArrayList<String> tmpUsername = new ArrayList<>();
        ArrayList<String> tmpPassword = new ArrayList<>();
        ArrayList<Boolean> tmpIsAdmin = new ArrayList<>();
        ArrayList<Boolean> tmpIsSoggetto = new ArrayList<>();

        try {
            utentePostgresDAO.getAllUtenti(tmpIdUtente, tmpUsername, tmpPassword, tmpIsAdmin, tmpIsSoggetto);

            for (int i = 0; i < tmpIdUtente.size(); i++) {
                Utente user = new Utente(
                        tmpIdUtente.get(i),
                        tmpUsername.get(i),
                        tmpPassword.get(i),
                        tmpIsAdmin.get(i),
                        tmpIsSoggetto.get(i),
                        null,
                        null,
                        null);

                utentiInMemory.add(user);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();

            return false;
        }

    }

    public boolean loadGallerie(){

        galleriePrivateInMemory.clear();
        gallerieCondiviseInMemory.clear();

        ArrayList<String> tmpIdGalleria = new ArrayList<>();
        ArrayList<String> tmpNomeGalleria = new ArrayList<>();
        ArrayList<Boolean> tmpCondivisa = new ArrayList<>();
        ArrayList<String> tmpProprietario = new ArrayList<>();

        try{
            galleriaPostgresDAO.getAllGallerie(tmpIdGalleria, tmpNomeGalleria, tmpCondivisa, tmpProprietario);

            for(int i = 0; i < tmpIdGalleria.size(); i++){
                if(tmpCondivisa.get(i) == true){
                    GalleriaCondivisa galleriaCondivisa = new GalleriaCondivisa(
                            tmpIdGalleria.get(i),
                            tmpNomeGalleria.get(i),
                            null,
                            null,
                            null
                    );

                    gallerieCondiviseInMemory.add(galleriaCondivisa);
                }else{
                    GalleriaPrivata galleriaPrivata = new GalleriaPrivata(
                            tmpIdGalleria.get(i),
                            tmpNomeGalleria.get(i),
                            null,
                            null,
                            null
                    );

                    galleriePrivateInMemory.add(galleriaPrivata);
                }
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}