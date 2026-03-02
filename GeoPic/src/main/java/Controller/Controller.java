package Controller;

import ImplementazioniPostgresDAO.*;
import Model.*;

import java.sql.*;
import java.time.LocalDate;
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

    public boolean loadSoggetti (){
        soggettiInMemory.clear();

        ArrayList<String> tmpNomeSoggetto = new ArrayList<>();
        ArrayList<String> tmpCategoria = new ArrayList<>();

        try {

            soggettoPostgresDAO.getAllSoggetti(tmpNomeSoggetto, tmpCategoria);

            for (int i = 0; i < tmpNomeSoggetto.size(); i++) {

                Soggetto subject = new Soggetto(
                        tmpNomeSoggetto.get(i),
                        tmpCategoria.get(i),
                        null,
                        null);

                soggettiInMemory.add(subject);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loadLuoghi(){
        luoghiInMemory.clear();

        ArrayList<String> tmpCoordinate =  new ArrayList<>();
        ArrayList<String> tmpNomeLuoghi = new ArrayList<>();

        try{

            luogoPostgresDAO.getAllLuoghi(tmpCoordinate, tmpNomeLuoghi);

            for(int i = 0; i < tmpCoordinate.size(); i++){
                Luogo luogo = new Luogo(
                        tmpCoordinate.get(i),
                        tmpNomeLuoghi.get(i),
                        null);

                luoghiInMemory.add(luogo);
            }

            return true;
        }catch (Exception e){
            e.printStackTrace();

            return false;
        }
    }

    public boolean loadVideos(){
        videosInMemory.clear();

        ArrayList<String> tmpIdVideos = new ArrayList<>();
        ArrayList<String> tmpDescrizione = new ArrayList<>();
        ArrayList<String> tmpTitolo = new ArrayList<>();
        ArrayList<String> tmpGalleria = new ArrayList<>();

        try{

            videoPostgresDAO.getAllVideo(tmpIdVideos, tmpDescrizione, tmpTitolo, tmpGalleria);

            for(int i = 0; i < tmpIdVideos.size(); i++){
                Video video = new Video(
                        tmpIdVideos.get(i),
                        tmpDescrizione.get(i),
                        tmpTitolo.get(i),
                        null,
                        null);

                videosInMemory.add(video);
            }

            return true;
        }catch (Exception e){
            e.printStackTrace();

            return false;
        }
    }

    public boolean loadFotografie(){

        fotografieInMemory.clear();

        ArrayList<String> tmpIdFoto = new ArrayList<>();
        ArrayList<String> tmpDevice = new ArrayList<>();
        ArrayList<String> tmpAutore = new ArrayList<>();
        ArrayList<String> tmpCoordinate = new ArrayList<>();
        ArrayList<Boolean> tmpVisibilita = new ArrayList<>();
        ArrayList<LocalDate> tmpDataDiScatto = new ArrayList<>();
        ArrayList<LocalDate> tmpDataEliminazione = new ArrayList<>();


        try {
            fotografiaPostgresDAO.getAllFotografie(tmpIdFoto, tmpDevice, tmpAutore,
                    tmpCoordinate, tmpVisibilita, tmpDataDiScatto, tmpDataEliminazione);

            for (int i = 0; i < tmpIdFoto.size(); i++) {
                Fotografia foto = new Fotografia(
                        tmpIdFoto.get(i),
                        tmpDevice.get(i),
                        tmpDataDiScatto.get(i),
                        tmpDataEliminazione.get(i),
                        tmpVisibilita.get(i),
                        null,
                        null,
                        null,
                        null);

                fotografieInMemory.add(foto);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();

            return false;
        }

    }

    public Fotografia getFotografiaByID(ArrayList<Fotografia> fotografie, String idFotoToFind) {
        for(Fotografia foto: fotografie){
            if(foto.getIdFoto().equals(idFotoToFind)){
                return foto;
            }
        }
        return null;
    }

    // se l'id passato è di una galleria privata, ritorna una privata, altrimenti una condivisa
    public Galleria getGalleriaByID(String idGalleria) {
        Galleria galleria = getGalleriaPrivataByID(galleriePrivateInMemory, idGalleria);
        if(galleria != null){
            return galleria;
        }

        galleria = getGalleriaCondivisaByID(gallerieCondiviseInMemory, idGalleria);
        if(galleria != null){
            return galleria;
        }

        return null;
    }
    
    public GalleriaCondivisa getGalleriaCondivisaByID(ArrayList<GalleriaCondivisa> gallCondivise,
                                                      String galCondIdToFind){
        for(GalleriaCondivisa galleriaCondivisa : gallCondivise){
            if(galleriaCondivisa.getIdGalleria().equals(galCondIdToFind)){
                return galleriaCondivisa;
            }
        }
        return null;
    }

    public GalleriaPrivata getGalleriaPrivataByID(ArrayList<GalleriaPrivata> gallPrivate,
                                                      String galPrivIdToFind){
        for(GalleriaPrivata galleriaPrivata : gallPrivate){
            if(galleriaPrivata.getIdGalleria().equals(galPrivIdToFind)){
                return galleriaPrivata;
            }
        }
        return null;
    }

    public Luogo getLuogoByCoordinate(ArrayList<Luogo> luoghi, String coordinateToFind) {
        for(Luogo luogo : luoghi){
            if(luogo.getCoordinate().equals(coordinateToFind)){
                return luogo;
            }
        }
        return null;
    }

    public Soggetto getSoggettoByNomeSoggetto(ArrayList<Soggetto> soggetti, String nomeSoggettoToFind) {
        for(Soggetto soggetto : soggetti){
            if(soggetto.getNomeSoggetto().equals(nomeSoggettoToFind)){
                return soggetto;
            }
        }
        return null;
    }

    public Utente getUtenteByID(ArrayList<Utente> utenti, String idUserToFind) {
        for(Utente utente : utenti){
            if(utente.getIdUtente().equals(idUserToFind)){
                return utente;
            }
        }
        return null;
    }

    public Video getVideoByID(ArrayList<Video> videos, String idVideoToFind) {
        for(Video video : videos){
            if(video.getIdVideo().equals(idVideoToFind)){
                return video;
            }
        }
        return null;
    }

    public void linkFotografiaAUtente() {
        ArrayList<String> tmpIdFotografia = new ArrayList<>();
        ArrayList<String> tmpAutore = new ArrayList<>();

        fotografiaPostgresDAO.getScatta(tmpIdFotografia, tmpAutore);

        for(int i = 0; i < tmpIdFotografia.size(); i++){
            Fotografia foto = getFotografiaByID(fotografieInMemory, tmpIdFotografia.get(i));
            Utente autore = getUtenteByID(utentiInMemory, tmpAutore.get(i));

            if(foto != null && autore != null){
                foto.setAutore(autore);
                autore.addFotoScattate(foto);
            }
        }
    }

    public void linkFotografiaLuogo(){
        ArrayList<String> tmpIdFotografia = new ArrayList<>();
        ArrayList<String> tmpLuogo = new ArrayList<>();

        fotografiaPostgresDAO.getRaffigura(tmpIdFotografia, tmpLuogo);

        for (int i=0;i<tmpIdFotografia.size();i++){
            Fotografia foto = getFotografiaByID(fotografieInMemory, tmpIdFotografia.get(i));
            Luogo luogo = getLuogoByCoordinate(luoghiInMemory, tmpLuogo.get(i));

            if (foto != null && luogo != null) {
                foto.setLuogo(luogo);
                luogo.addLuogoRaffiguratoIn(foto);

            }

        }
    }

    public void linkFotografiaSoggetto(){
        ArrayList<String> tmpIdFotografia = new ArrayList<>();
        ArrayList<String> tmpSoggetto = new ArrayList<>();

        mostraPostgresDAO.getAlSoggettiMostrati(tmpSoggetto, tmpIdFotografia);

        for (int i=0;i<tmpIdFotografia.size();i++){
            Fotografia foto = getFotografiaByID(fotografieInMemory, tmpIdFotografia.get(i));
            Soggetto soggetto = getSoggettoByNomeSoggetto(soggettiInMemory, tmpSoggetto.get(i));

            if (foto != null && soggetto != null){
                foto.addSoggetto(soggetto);
                soggetto.addFotoInCuiAppare(foto);
            }
        }
    }

    public void linkUtenteSoggetto(){
        ArrayList<String> tmpIdUtente =  new ArrayList<>();
        ArrayList<String> tmpSoggetto = new ArrayList<>();

        soggettoPostgresDAO.getRappresenta(tmpSoggetto, tmpIdUtente);


        for (int i=0;i<tmpIdUtente.size();i++) {
            Soggetto soggetto = getSoggettoByNomeSoggetto(soggettiInMemory, tmpSoggetto.get(i));
            Utente utente = getUtenteByID(utentiInMemory, tmpIdUtente.get(i));


            if (utente != null && soggetto != null) {
                utente.setSoggetto(true);
                soggetto.setUtenteRappresentato(utente);
            }
        }
    }

    public void linkOwnerGalleries(){
        ArrayList<String> tmpIdGalleria= new ArrayList<>();
        ArrayList<String> tmpProprietario= new ArrayList<>();
        ArrayList<Boolean> tmpCondivisione = new ArrayList<>();

        galleriaPostgresDAO.getOwner(tmpIdGalleria, tmpProprietario, tmpCondivisione);

        for(int i = 0; i < tmpIdGalleria.size(); i++){
            boolean isCondivisa = tmpCondivisione.get(i);
            String currentIdGal = tmpIdGalleria.get(i);
            String currentProprietario = tmpProprietario.get(i);

            Galleria gal;
            if(isCondivisa){
                gal = getGalleriaCondivisaByID(gallerieCondiviseInMemory, currentIdGal);
            }else {
                gal = getGalleriaPrivataByID(galleriePrivateInMemory, currentIdGal);
            }

            Utente proprietario = getUtenteByID(utentiInMemory, currentProprietario);

            if(gal != null && proprietario != null){
                gal.setProprietario(proprietario);
                proprietario.addGalleriePossedute(gal);
            }else {
                System.err.println("Errore Linking: Galleria o Proprietario non trovati per ID: " + currentIdGal);
            }
        }
    }

    public void linkPartecipazione(){
        ArrayList<String> tmpIdGalleria = new ArrayList<>();
        ArrayList<String> tmpIdPartecipante = new ArrayList<>();

        partecipaPostgresDAO.getAllPartecipanti(tmpIdGalleria, tmpIdPartecipante);

        for(int i = 0; i < tmpIdGalleria.size(); i++){
            String currentIdGal = tmpIdGalleria.get(i);
            String currentIdPartecipante = tmpIdPartecipante.get(i);

            Utente utente =  getUtenteByID(utentiInMemory, currentIdPartecipante);
            GalleriaCondivisa galleria = getGalleriaCondivisaByID(gallerieCondiviseInMemory, currentIdGal);

            if(utente != null && galleria != null){
                utente.addPartecipazioneAGalleria(galleria);
                galleria.addPartecipante(utente);
            }
        }
    }

    public void linkVideoToGalleries(){
        ArrayList<String> tmpIdVideo = new ArrayList<>();
        ArrayList<String> tmpGalleria = new ArrayList<>();

        videoPostgresDAO.getSalvatoIn(tmpIdVideo, tmpGalleria);

        for(int i = 0; i < tmpIdVideo.size(); i++){
            String currentIdVideo = tmpIdVideo.get(i);
            String currentIdGalleria = tmpGalleria.get(i);

            Video video = getVideoByID(videosInMemory, currentIdVideo);
            GalleriaPrivata galPriv = getGalleriaPrivataByID(galleriePrivateInMemory, currentIdGalleria);

            if(video != null && galPriv != null){
                video.setGalleria(galPriv);
                galPriv.addVideo(video);
            }
        }
    }

    public void linkFotoToGallerie(){
        ArrayList<String> tmpIdGalleria = new ArrayList<>();
        ArrayList<String> tmpIdFoto = new ArrayList<>();

        contienePostgresDAO.getAllContenute(tmpIdGalleria, tmpIdFoto);

        for(int i = 0; i < tmpIdGalleria.size(); i++){
            String currentIdGal = tmpIdGalleria.get(i);
            String currentIdFoto = tmpIdFoto.get(i);

            Galleria galleria = getGalleriaByID(currentIdGal);
            Fotografia foto = getFotografiaByID(fotografieInMemory,  currentIdFoto);

            if(galleria != null && foto != null){
                foto.addGalleriaContenitrice(galleria);
                galleria.addFotoAGalleria(foto);
            }
        }
    }

    public void linkFotoToVideo(){
        ArrayList<String> tmpIdVideo = new ArrayList<>();
        ArrayList<String> tmpIdFoto = new ArrayList<>();

        componePostgresDAO.getAllComposizioni(tmpIdVideo, tmpIdFoto);

        for(int i = 0; i < tmpIdVideo.size(); i++){
            String currentIdVideo = tmpIdVideo.get(i);
            String currentIdFoto = tmpIdFoto.get(i);

            Video video = getVideoByID(videosInMemory, currentIdVideo);
            Fotografia foto = getFotografiaByID(fotografieInMemory,  currentIdFoto);

            if(video != null && foto != null){
                video.addFotoAVideo(foto);
                foto.addVideo(video);
            }
        }
    }
}