package Controller;

import ImplementazioniPostgresDAO.*;
import Model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller principale dell'applicazione.
 * Gestisce il caricamento iniziale dei dati dal database, la costruzione del grafo di oggetti
 * in memoria e fornisce i metodi di servizio per l'autenticazione e la manipolazione
 * delle entità (Fotografia, Galleria, Utente, ecc.).
 */
public class Controller {

    private ComponePostgresDAO componePostgresDAO;
    private ContienePostgresDAO contienePostgresDAO;
    private FotografiaPostgresDAO fotografiaPostgresDAO;
    private GalleriaPostgresDAO galleriaPostgresDAO;
    private LuogoPostgresDAO luogoPostgresDAO;
    private MostraPostgresDAO mostraPostgresDAO;
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

    /**
     * Inizializza il controller stabilendo la connessione al database
     * e istanziando i vari DAO.
     */
    public Controller() {
        try {

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

        } catch (SQLException sqle) {
            System.err.println("Impossibile connettersi al DB.");
            sqle.printStackTrace();
        }
    }

    // --- Metodi di Caricamento ---

    /**
     * Esegue il caricamento completo di tutte le entità e risolve tutte le
     * dipendenze (linking) tra di esse, completando il Data Graph.
     */
    public void loadInMemory() {
        // Flat loading
        loadUtenti();
        loadLuoghi();
        loadSoggetti();
        loadGallerie(); // Sia private che condivise
        loadVideos();
        loadFotografie();

        // Linking
        linkUtenteSoggetto();
        linkOwnerGalleries();
        linkPartecipazione();
        linkVideoToGalleries();
        linkFotografiaAUtente();
        linkFotografiaLuogo();
        linkFotografiaSoggetto();
        linkFotoToGallerie();
        linkFotoToVideo();

        System.out.println("Data Graph caricato con successo!");
    }

    /**
     * Svuota la lista degli utenti in memoria e popola la stessa con i dati
     * recuperati dal database tramite {@link UtentePostgresDAO}.
     * @return true se il caricamento ha successo, false altrimenti.
     */
    public boolean loadUtenti() {

        utentiInMemory.clear();

        ArrayList<Integer> tmpIdUtente = new ArrayList<>();
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
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }

    }

    /**
     * Svuota le liste delle gallerie in memoria e popola le strutture
     * {@code galleriePrivateInMemory} e {@code gallerieCondiviseInMemory} interrogando il {@link GalleriaPostgresDAO}.
     * @return true se il caricamento ha successo.
     */
    public boolean loadGallerie() {

        galleriePrivateInMemory.clear();
        gallerieCondiviseInMemory.clear();

        ArrayList<Integer> tmpIdGalleria = new ArrayList<>();
        ArrayList<String> tmpNomeGalleria = new ArrayList<>();
        ArrayList<Boolean> tmpCondivisa = new ArrayList<>();
        ArrayList<Integer> tmpProprietario = new ArrayList<>();

        try {
            galleriaPostgresDAO.getAllGallerie(tmpIdGalleria, tmpNomeGalleria, tmpCondivisa, tmpProprietario);

            for (int i = 0; i < tmpIdGalleria.size(); i++) {
                if (tmpCondivisa.get(i) == true) {
                    GalleriaCondivisa galleriaCondivisa = new GalleriaCondivisa(
                            tmpIdGalleria.get(i),
                            tmpNomeGalleria.get(i),
                            null,
                            null,
                            null
                    );

                    gallerieCondiviseInMemory.add(galleriaCondivisa);
                } else {
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
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Aggiorna la collezione in memoria dei soggetti censiti.
     * @return true se l'operazione di popolamento da {@link SoggettoPostgresDAO} è riuscita.
     */
    public boolean loadSoggetti() {
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

    /**
     * Aggiorna la collezione in memoria dei luoghi geografici associando le coordinate al toponimo.
     * @return true se il caricamento da {@link LuogoPostgresDAO} ha successo.
     */
    public boolean loadLuoghi() {
        luoghiInMemory.clear();

        ArrayList<String> tmpCoordinate = new ArrayList<>();
        ArrayList<String> tmpNomeLuoghi = new ArrayList<>();

        try {

            luogoPostgresDAO.getAllLuoghi(tmpCoordinate, tmpNomeLuoghi);

            for (int i = 0; i < tmpCoordinate.size(); i++) {
                Luogo luogo = new Luogo(
                        tmpCoordinate.get(i),
                        tmpNomeLuoghi.get(i),
                        null);

                luoghiInMemory.add(luogo);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    /**
     * Recupera l'elenco dei video archiviati e aggiorna la collezione locale {@code videosInMemory}.
     * @return true se il caricamento da {@link VideoPostgresDAO} è riuscito.
     */
    public boolean loadVideos() {
        videosInMemory.clear();

        ArrayList<Integer> tmpIdVideos = new ArrayList<>();
        ArrayList<String> tmpDescrizione = new ArrayList<>();
        ArrayList<String> tmpTitolo = new ArrayList<>();
        ArrayList<Integer> tmpGalleria = new ArrayList<>();

        try {

            // Il DAO si aspetta (id, titolo, descrizione, galleria)
            videoPostgresDAO.getAllVideo(tmpIdVideos, tmpTitolo, tmpDescrizione, tmpGalleria);

            for (int i = 0; i < tmpIdVideos.size(); i++) {
                Video video = new Video(
                        tmpIdVideos.get(i),
                        tmpDescrizione.get(i),
                        tmpTitolo.get(i),
                        null,
                        null);

                videosInMemory.add(video);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    /**
     * Sincronizza lo stato in memoria delle fotografie con il database.
     * @return true se la sincronizzazione da {@link FotografiaPostgresDAO} è completata.
     */
    public boolean loadFotografie() {

        fotografieInMemory.clear();

        ArrayList<Integer> tmpIdFoto = new ArrayList<>();
        ArrayList<String> tmpDevice = new ArrayList<>();
        ArrayList<Integer> tmpAutore = new ArrayList<>();
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
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }

    }

    /**
     * Crea il linking in memoria tra fotografie e i relativi autori (utenti).
     */
    public void linkFotografiaAUtente() {
        ArrayList<Integer> tmpIdFotografia = new ArrayList<>();
        ArrayList<Integer> tmpAutore = new ArrayList<>();

        fotografiaPostgresDAO.getScatta(tmpIdFotografia, tmpAutore);

        for (int i = 0; i < tmpIdFotografia.size(); i++) {
            Fotografia foto = getFotografiaByID(fotografieInMemory, tmpIdFotografia.get(i));
            Utente autore = getUtenteByID(utentiInMemory, tmpAutore.get(i));

            if (foto != null && autore != null) {
                foto.setAutore(autore);
                autore.addFotoScattate(foto);
            }
        }
    }

    /**
     * Crea il linking in memoria tra fotografie e i relativi luoghi di scatto.
     */
    public void linkFotografiaLuogo() {
        ArrayList<Integer> tmpIdFotografia = new ArrayList<>();
        ArrayList<String> tmpLuogo = new ArrayList<>();

        fotografiaPostgresDAO.getRaffigura(tmpIdFotografia, tmpLuogo);

        for (int i = 0; i < tmpIdFotografia.size(); i++) {
            Fotografia foto = getFotografiaByID(fotografieInMemory, tmpIdFotografia.get(i));
            Luogo luogo = getLuogoByCoordinate(luoghiInMemory, tmpLuogo.get(i));

            if (foto != null && luogo != null) {
                foto.setLuogo(luogo);
                luogo.addLuogoRaffiguratoIn(foto);

            }

        }
    }

    /**
     * Crea il linking tra fotografie e i soggetti raffigurati in esse.
     */
    public void linkFotografiaSoggetto() {
        ArrayList<Integer> tmpIdFotografia = new ArrayList<>();
        ArrayList<String> tmpSoggetto = new ArrayList<>();

        mostraPostgresDAO.getAllSoggettiMostrati(tmpSoggetto, tmpIdFotografia);

        for (int i = 0; i < tmpIdFotografia.size(); i++) {
            Fotografia foto = getFotografiaByID(fotografieInMemory, tmpIdFotografia.get(i));
            Soggetto soggetto = getSoggettoByNomeSoggetto(soggettiInMemory, tmpSoggetto.get(i));

            if (foto != null && soggetto != null) {
                foto.addSoggetto(soggetto);
                soggetto.addFotoInCuiAppare(foto);
            }
        }
    }

    /**
     * Associa in memoria un utente al soggetto che lo rappresenta.
     */
    public void linkUtenteSoggetto() {
        ArrayList<Integer> tmpIdUtente = new ArrayList<>();
        ArrayList<String> tmpSoggetto = new ArrayList<>();

        soggettoPostgresDAO.getRappresenta(tmpSoggetto, tmpIdUtente);


        for (int i = 0; i < tmpIdUtente.size(); i++) {
            Soggetto soggetto = getSoggettoByNomeSoggetto(soggettiInMemory, tmpSoggetto.get(i));
            Utente utente = getUtenteByID(utentiInMemory, tmpIdUtente.get(i));


            if (utente != null && soggetto != null) {
                utente.setSoggetto(true);
                soggetto.setUtenteRappresentato(utente);
            }
        }
    }

    /**
     * Associa in memoria le gallerie ai rispettivi proprietari.
     */
    public void linkOwnerGalleries() {
        ArrayList<Integer> tmpIdGalleria = new ArrayList<>();
        ArrayList<Integer> tmpProprietario = new ArrayList<>();
        ArrayList<Boolean> tmpCondivisione = new ArrayList<>();

        galleriaPostgresDAO.getOwner(tmpIdGalleria, tmpProprietario, tmpCondivisione);

        for (int i = 0; i < tmpIdGalleria.size(); i++) {
            boolean isCondivisa = tmpCondivisione.get(i);
            Integer currentIdGal = tmpIdGalleria.get(i);
            Integer currentProprietario = tmpProprietario.get(i);

            Galleria gal;
            if (isCondivisa) {
                gal = getGalleriaCondivisaByID(gallerieCondiviseInMemory, currentIdGal);
            } else {
                gal = getGalleriaPrivataByID(galleriePrivateInMemory, currentIdGal);
            }

            Utente proprietario = getUtenteByID(utentiInMemory, currentProprietario);

            if (gal != null && proprietario != null) {
                gal.setProprietario(proprietario);
                proprietario.addGalleriePossedute(gal);
            } else {
                System.err.println("Errore Linking: Galleria o Proprietario non trovati per ID: " + currentIdGal);
            }
        }
    }

    /**
     * Gestisce la relazione di partecipazione degli utenti alle gallerie condivise.
     */
    public void linkPartecipazione() {
        ArrayList<Integer> tmpIdGalleria = new ArrayList<>();
        ArrayList<Integer> tmpIdPartecipante = new ArrayList<>();

        partecipaPostgresDAO.getAllPartecipanti(tmpIdGalleria, tmpIdPartecipante);

        for (int i = 0; i < tmpIdGalleria.size(); i++) {
            Integer currentIdGal = tmpIdGalleria.get(i);
            Integer currentIdPartecipante = tmpIdPartecipante.get(i);

            Utente utente = getUtenteByID(utentiInMemory, currentIdPartecipante);
            GalleriaCondivisa galleria = getGalleriaCondivisaByID(gallerieCondiviseInMemory, currentIdGal);

            if (utente != null && galleria != null) {
                utente.addPartecipazioneAGalleria(galleria);
                galleria.addPartecipante(utente);
            }
        }
    }

    /**
     * Collega in memoria i video alle gallerie in cui sono salvati.
     */
    public void linkVideoToGalleries() {
        ArrayList<Integer> tmpIdVideo = new ArrayList<>();
        ArrayList<Integer> tmpGalleria = new ArrayList<>();

        videoPostgresDAO.getSalvatoIn(tmpIdVideo, tmpGalleria);

        for (int i = 0; i < tmpIdVideo.size(); i++) {
            Integer currentIdVideo = tmpIdVideo.get(i);
            Integer currentIdGalleria = tmpGalleria.get(i);

            Video video = getVideoByID(videosInMemory, currentIdVideo);
            GalleriaPrivata galPriv = getGalleriaPrivataByID(galleriePrivateInMemory, currentIdGalleria);

            if (video != null && galPriv != null) {
                video.setGalleria(galPriv);
                galPriv.addVideo(video);
            }
        }
    }

    /**
     * Popola in memoria la relazione tra gallerie e le fotografie in esse contenute.
     */
    public void linkFotoToGallerie() {
        ArrayList<Integer> tmpIdGalleria = new ArrayList<>();
        ArrayList<Integer> tmpIdFoto = new ArrayList<>();

        contienePostgresDAO.getAllContenute(tmpIdGalleria, tmpIdFoto);

        for (int i = 0; i < tmpIdGalleria.size(); i++) {
            Integer currentIdGal = tmpIdGalleria.get(i);
            Integer currentIdFoto = tmpIdFoto.get(i);

            Galleria galleria = getGalleriaByID(currentIdGal);
            Fotografia foto = getFotografiaByID(fotografieInMemory, currentIdFoto);

            if (galleria != null && foto != null) {
                foto.addGalleriaContenitrice(galleria);
                galleria.addFotoAGalleria(foto);
            }
        }
    }

    /**
     * Crea il legame tra composizioni di video e le fotografie che li compongono.
     */
    public void linkFotoToVideo() {
        ArrayList<Integer> tmpIdVideo = new ArrayList<>();
        ArrayList<Integer> tmpIdFoto = new ArrayList<>();

        componePostgresDAO.getAllComposizioni(tmpIdVideo, tmpIdFoto);

        for (int i = 0; i < tmpIdVideo.size(); i++) {
            Integer currentIdVideo = tmpIdVideo.get(i);
            Integer currentIdFoto = tmpIdFoto.get(i);

            Video video = getVideoByID(videosInMemory, currentIdVideo);
            Fotografia foto = getFotografiaByID(fotografieInMemory, currentIdFoto);

            if (video != null && foto != null) {
                video.addFotoAVideo(foto);
                foto.addVideo(video);
            }
        }
    }

    /**
     * Recupera un oggetto {@link Utente} tramite il suo identificativo univoco.
     */
    public Utente getUtenteByID(ArrayList<Utente> utenti, Integer idUserToFind) {
        for (Utente utente : utenti) {
            if (utente.getIdUtente().equals(idUserToFind)) {
                return utente;
            }
        }
        return null;
    }

    /**
     * Recupera un oggetto {@link Utente} tramite il suo username.
     */
    public Utente getUtenteByUsername(ArrayList<Utente> utenti, String usernameToFind) {
        for (Utente utente : utenti) {
            if (utente.getUsername().equals(usernameToFind)) {
                return utente;
            }
        }
        return null;
    }

    /**
     * Cerca una fotografia nella lista fornita in base al suo ID univoco.
     * @param fotografie La lista di riferimento in cui cercare.
     * @param idFotoToFind L'ID da ricercare.
     * @return L'oggetto {@link Fotografia} corrispondente, o null se non trovato.
     */
    public Fotografia getFotografiaByID(ArrayList<Fotografia> fotografie, Integer idFotoToFind) {
        for (Fotografia foto : fotografie) {
            if (foto.getIdFoto().equals(idFotoToFind)) {
                return foto;
            }
        }
        return null;
    }

    /**
     * Recupera un'istanza di {@link Galleria} (sia privata che condivisa) dato il suo ID.
     * @param idGalleria L'ID della galleria da recuperare.
     * @return L'oggetto Galleria trovato o null.
     */
    public Galleria getGalleriaByID(Integer idGalleria) {
        Galleria galleria = getGalleriaPrivataByID(galleriePrivateInMemory, idGalleria);
        if (galleria != null) {
            return galleria;
        }

        galleria = getGalleriaCondivisaByID(gallerieCondiviseInMemory, idGalleria);
        return galleria;
    }

    /**
     * Cerca una galleria condivisa nella lista fornita.
     */
    public GalleriaCondivisa getGalleriaCondivisaByID(ArrayList<GalleriaCondivisa> gallCondivise,
                                                      Integer galCondIdToFind) {
        for (GalleriaCondivisa galleriaCondivisa : gallCondivise) {
            if (galleriaCondivisa.getIdGalleria().equals(galCondIdToFind)) {
                return galleriaCondivisa;
            }
        }
        return null;
    }

    /**
     * Cerca una galleria privata nella lista fornita.
     */
    public GalleriaPrivata getGalleriaPrivataByID(ArrayList<GalleriaPrivata> gallPrivate,
                                                  Integer galPrivIdToFind) {
        for (GalleriaPrivata galleriaPrivata : gallPrivate) {
            if (galleriaPrivata.getIdGalleria().equals(galPrivIdToFind)) {
                return galleriaPrivata;
            }
        }
        return null;
    }

    /**
     * Recupera l'oggetto {@link Luogo} corrispondente alle coordinate specificate.
     */
    public Luogo getLuogoByCoordinate(ArrayList<Luogo> luoghi, String coordinateToFind) {
        for (Luogo luogo : luoghi) {
            if (luogo.getCoordinate().equals(coordinateToFind)) {
                return luogo;
            }
        }
        return null;
    }

    /**
     * Recupera l'oggetto {@link Soggetto} tramite il suo nome identificativo.
     */
    public Soggetto getSoggettoByNomeSoggetto(ArrayList<Soggetto> soggetti, String nomeSoggettoToFind) {
        for (Soggetto soggetto : soggetti) {
            if (soggetto.getNomeSoggetto().equals(nomeSoggettoToFind)) {
                return soggetto;
            }
        }
        return null;
    }

    /**
     * Recupera un oggetto {@link Video} tramite il suo identificativo univoco.
     */
    public Video getVideoByID(ArrayList<Video> videos, Integer idVideoToFind) {
        for (Video video : videos) {
            if (video.getIdVideo().equals(idVideoToFind)) {
                return video;
            }
        }
        return null;
    }

    /**
     * Restituisce la lista dei luoghi caricati in memoria.
     */
    public ArrayList<Luogo> getLuoghiInMemory() {
        return luoghiInMemory;
    }

    /**
     * Restituisce l'utente attualmente autenticato nella sessione.
     */
    public Utente getLoggedInUtente() {
        return loggedInUtente;
    }


    /**
     * @return una lista di stringhe con gli username di tutti gli utenti in memoria.
     */
    public ArrayList<String> getUsernamesInMemory() {
        ArrayList<String> usernames = new ArrayList<>();
        if (utentiInMemory != null) {
            for (Utente u : utentiInMemory) {
                usernames.add(u.getUsername());
            }
        }
        return usernames;
    }

    /**
     * Restituisce la lista degli utenti caricati in memoria.
     */
    public ArrayList<Utente> getUtentiInMemory() {
        return utentiInMemory;
    }

    /**
     * Recupera le fotografie dalla galleria personale dell'utente loggato.
     * @return una lista di oggetti Fotografia. Se l'utente non è loggato o non ha una galleria, ritorna una lista vuota.
     */
    public ArrayList<Fotografia> getFotoGalleriaPersonale() {
        if (loggedInUtente == null) {
            return new ArrayList<>();
        }
        for (GalleriaPrivata galleria : galleriePrivateInMemory) {
            if (galleria.getProprietario() != null && galleria.getProprietario().getIdUtente().equals(loggedInUtente.getIdUtente())) {
                // Assumendo che la galleria contenga direttamente la lista di foto dopo il linking
                return galleria.getFotoContenute();
            }
        }
        return new ArrayList<>();
    }

    /**
     * Recupera i video dalla galleria personale dell'utente loggato.
     * @return una lista di oggetti Video. Se l'utente non è loggato o non ha una galleria, ritorna una lista vuota.
     */
    public ArrayList<Video> getVideoGalleriaPersonale() {
        if (loggedInUtente == null) {
            return new ArrayList<>();
        }
        for (GalleriaPrivata galleria : galleriePrivateInMemory) {
            if (galleria.getProprietario() != null && galleria.getProprietario().getIdUtente().equals(loggedInUtente.getIdUtente())) {
                // Assumendo che la galleria contenga direttamente la lista di video dopo il linking
                return galleria.getVideos();
            }
        }
        return new ArrayList<>();
    }

    /**
     * Recupera le gallerie condivise visibili all'utente loggato.
     * Include sia le gallerie condivise possedute, sia quelle a cui partecipa.
     */
    public ArrayList<GalleriaCondivisa> getGallerieCondiviseUtenteLoggato() {
        ArrayList<GalleriaCondivisa> result = new ArrayList<>();

        if (loggedInUtente == null) {
            return result;
        }

        for (Galleria galleria : loggedInUtente.getGalleriePossedute()) {
            if (galleria instanceof GalleriaCondivisa condivisa && !result.contains(condivisa)) {
                result.add(condivisa);
            }
        }

        for (GalleriaCondivisa condivisa : gallerieCondiviseInMemory) {
            if (condivisa.getPartecipanti().contains(loggedInUtente) && !result.contains(condivisa)) {
                result.add(condivisa);
            }
        }

        return result;
    }

    /**
     * Verifica le credenziali dell'utente.
     * @return 2 (Admin), 1 (Utente standard), -1 (Credenziali errate).
     */
    public int authentication(String username, String password) {
        Integer tmpUserID;

        tmpUserID = utentePostgresDAO.getLoggedInUtente(username);

        Utente tmpUtente = getUtenteByID(utentiInMemory, tmpUserID);

        if (tmpUtente != null) {
            if(tmpUtente.getPassword().equals(password)) {
                loggedInUtente = tmpUtente;

                if (loggedInUtente.isAdmin())
                    return 2;
                else
                    return 1;
            }
        }

        return -1;
    }

    /**
     * Valuta le stringhe delle coordinate grezze per normalizzare il potenziale zero matematico (-0 -> +0)
     * e restituisce la stringa formattata compatibile con il database.
     */
    public String normalizzaCoordinate(String latSign, String latVal, String lonSign, String lonVal) {
        try {
            if (Double.parseDouble(latVal) == 0.0) latSign = "+";
            if (Double.parseDouble(lonVal) == 0.0) lonSign = "+";
        } catch (NumberFormatException e) {
            // Ignorato silenziosamente
        }
        return latSign + latVal + "," + lonSign + lonVal;
    }

    /**
     * Verifica se un determinato luogo esiste già in base alle coordinate.
     * @param coordinate Le coordinate da cercare.
     * @return Il toponimo esistente (o "Sconosciuto" se nullo). Ritorna null solo se il luogo non esiste.
     */
    public String getToponimoEsistente(String coordinate) {
        Luogo luogo = getLuogoByCoordinate(luoghiInMemory, coordinate);
        if (luogo != null) {
            return luogo.getNomeMnemonico() != null ? luogo.getNomeMnemonico() : "Sconosciuto";
        }
        return null; // Il luogo non esiste nel sistema
    }

    /**
     * Aggiunge un nuovo utente nel sistema.
     * @return true se l'inserimento nel database va a buon fine.
     */
    public boolean creazioneNuovoUtente(String username, String password, boolean isAdmin, boolean isSoggetto) {
        boolean success = utentePostgresDAO.insertUtente(username, password, isAdmin, isSoggetto);
        if (success) {
            // Recupera l'ID del nuovo utente
            Integer newUserId = utentePostgresDAO.getLoggedInUtente(username);
            if (newUserId != null) {
                // Genera in automatico la Galleria Personale base per l'utente, non condivisa
                galleriaPostgresDAO.insertGalleria("Galleria Personale di " + username, false, newUserId);
            }

            // Ricarica la memoria per tenere utenti e nuove gallerie aggiornati
            loadInMemory();
        }
        return success;
    }

    /**
     * Ritorna la galleria privata dell'utente che ha eseguito il log in.
     * @return La galleria privata dell'utente loggato
     */
    public GalleriaPrivata getLoggedInUtenteGalPriv(){
        GalleriaPrivata tmpGalPriv = null;
        for (Galleria galleria : loggedInUtente.getGalleriePossedute()) {
            if(galleria instanceof GalleriaPrivata){
                tmpGalPriv = (GalleriaPrivata) galleria;
                break;
            }
        }

        return  tmpGalPriv;
    }


    /**
     * Gestisce la logica di business per la creazione di una nuova foto,
        * persistendo il dato nel DB e aggiornando il grafo in memoria.
        * L'eventuale inserimento nelle gallerie condivise viene gestito separatamente.
        * @return la nuova fotografia creata, oppure null se il salvataggio fallisce.
     */
    public Fotografia creazioneNuovaFoto(String dispositivo, boolean visibilita, String coordinate, String toponimo,
                                         ArrayList<String> nomiSoggetti, ArrayList<String> categorieSoggetti){

        //Se il luogo non esiste, viene creato e inserito in memoria e nel DB
        Luogo luogo = getLuogoByCoordinate(luoghiInMemory, coordinate);
        if(luogo == null) {
            luogoPostgresDAO.insertLuogo(coordinate, toponimo);
            luogo = new Luogo(coordinate, toponimo, new ArrayList<>());
            luoghiInMemory.add(luogo);
        }

        Integer idNewFoto = fotografiaPostgresDAO.insertFotografia(dispositivo, LocalDate.now(), null,
                visibilita, coordinate, loggedInUtente.getIdUtente());

        if(idNewFoto == null){
            System.err.println("Errore nel salvataggio della fotografia sul Database.");
            return null;
        }

        //Prendiamo la galleria privata dell'autore della foto
        GalleriaPrivata autoreGalPriv = getLoggedInUtenteGalPriv();

        //La aggiungiamo alla lista di gallerie che contengono la foto
        //In memoria (lato foto) e nel DB
        ArrayList<Galleria> galleriaContenitrici = new ArrayList<>();
        if(autoreGalPriv != null) {
            galleriaContenitrici.add(autoreGalPriv);

            contienePostgresDAO.insertFotoAGalleria(autoreGalPriv.getIdGalleria(), idNewFoto);
        }

        //Finalmente viene creato la nuova foto in memoria
        Fotografia newFoto = new Fotografia(idNewFoto,
                                            dispositivo,
                                            LocalDate.now(),
                                            null,
                                            visibilita,
                                            loggedInUtente,
                                            luogo,
                                            galleriaContenitrici,
                                            null);


        ArrayList<Soggetto> soggettiRaffigurati = new ArrayList<>();

        Soggetto soggetto = null;
        if (nomiSoggetti != null && categorieSoggetti != null && nomiSoggetti.size() == categorieSoggetti.size()) {
            for (int i = 0; i < nomiSoggetti.size(); i++) {
                String nomeSoggetto = nomiSoggetti.get(i);
                String categoriaSoggetto = categorieSoggetti.get(i);

                soggetto = getSoggettoByNomeSoggetto(soggettiInMemory, nomeSoggetto);

                // Se non esiste, creiamolo sul DB e in memoria
                if (soggetto == null) {

                    if(categoriaSoggetto.equals("Utente")){
                        Utente utenteSoggetto = getUtenteByUsername(utentiInMemory, nomeSoggetto);
                        soggettoPostgresDAO.insertUtenteAsSoggetto(nomeSoggetto, categoriaSoggetto, utenteSoggetto.getIdUtente());
                        soggetto = new Soggetto(nomeSoggetto, categoriaSoggetto, utenteSoggetto, null);
                        utenteSoggetto.setSoggetto(true);
                    }else{
                        soggettoPostgresDAO.insertSoggetto(nomeSoggetto, categoriaSoggetto);
                        soggetto = new Soggetto(nomeSoggetto, categoriaSoggetto);
                    }
                    soggettiInMemory.add(soggetto);
                }

                // Mappiamo nel DB la foto con il soggetto
                mostraPostgresDAO.insertSoggettoInFoto(nomeSoggetto, idNewFoto);
                soggettiRaffigurati.add(soggetto);
                newFoto.setSoggetti(soggettiRaffigurati);
            }
        }

        // Collegamento simmetrico inverso (serve per la navigazione da soggetto a foto raffiguranti il soggetto)
        for (Soggetto s : soggettiRaffigurati) {
            s.addFotoInCuiAppare(newFoto);

            // Se il soggetto è un Utente, aggiorniamo anche l'oggetto Utente reale
            if (s.getCategoria().equals("Utente") && s.getUtenteRappresentato() != null) {
                s.getUtenteRappresentato().setSoggetto(true);
            }
        }

        //Aggiungiamo la foto scattata in memoria
        fotografieInMemory.add(newFoto);
        //e alla lista delle foto scattate dall'utente
        loggedInUtente.addFotoScattate(newFoto);

        //Aggiungiamo la nuova foto alla galleria privata dell'autore (lato galleria)
        if(autoreGalPriv != null)
            autoreGalPriv.addFotoAGalleria(newFoto);

        //Aggiungiamo la foto alla lista dei luoghi in cui e' raffigurato il luogo (lato luogo)
        luogo.addLuogoRaffiguratoIn(newFoto);

        return newFoto;
    }

    /**
     * Aggiunge una fotografia già esistente alle gallerie condivise selezionate nella dialog.
     * La fotografia rimane comunque contenuta nella galleria privata dell'utente proprietario.
     */
    public void AggiungiFotoCondivisa(Fotografia fotoDaCondividere,
                                      java.util.List<GalleriaCondivisa> gallerieCondiviseSelezionate) {
        if (fotoDaCondividere == null || gallerieCondiviseSelezionate == null) {
            return;
        }

        for (GalleriaCondivisa galleriaSelezionata : gallerieCondiviseSelezionate) {
            if (galleriaSelezionata == null) {
                continue;
            }

            GalleriaCondivisa galleriaInMemory = getGalleriaCondivisaByID(
                    gallerieCondiviseInMemory,
                    galleriaSelezionata.getIdGalleria());

            if (galleriaInMemory == null) {
                continue;
            }

            if (!fotoDaCondividere.getGalleriaContenitrice().contains(galleriaInMemory)) {
                contienePostgresDAO.insertFotoAGalleria(galleriaInMemory.getIdGalleria(), fotoDaCondividere.getIdFoto());
                fotoDaCondividere.addGalleriaContenitrice(galleriaInMemory);
                galleriaInMemory.addFotoAGalleria(fotoDaCondividere);
            }
        }
    }

    /**
     * Gestisce la logica di business per la creazione di una nuova galleria condivisa,
     * persistendo le relazioni di proprietà e partecipazione.
     */
    public void creazioneNuovaGalleriaCondivisa(String nomeGalleria, String[] partecipantiString){
        Integer newGalleriaCondID = galleriaPostgresDAO.insertGalleria(nomeGalleria, true, loggedInUtente.getIdUtente());

        ArrayList<Utente> partecipanti = new ArrayList<>();
        if (partecipantiString != null) {
            for (String username : partecipantiString) {
                Utente utente = getUtenteByUsername(utentiInMemory, username);
                if (utente != null) {
                    partecipanti.add(utente);
                }
            }
        }

        GalleriaCondivisa newGallCond = new GalleriaCondivisa(newGalleriaCondID,
                                                            nomeGalleria,
                                                            loggedInUtente,
                                                            new ArrayList<>(),
                                                            partecipanti);

        for(Utente utente : partecipanti){
            utente.addPartecipazioneAGalleria(newGallCond);
            partecipaPostgresDAO.insertPartecipante(newGalleriaCondID, utente.getIdUtente());
        }

        loggedInUtente.addGalleriePossedute(newGallCond);
        gallerieCondiviseInMemory.add(newGallCond);

    }

    /**
     *
     * @param fotoId
     */
    //Metodo per la privatizzazione
    public void setFotografiaPrivata(Integer fotoId) {
        if (fotoId == null){
            System.out.println("L'id della foto passato e' null.");
            return;
        }

        Fotografia foto = getFotografiaByID(fotografieInMemory, fotoId);

        if(foto != null && foto.isVisibile()) {
            fotografiaPostgresDAO.updateVisibilita(foto.getIdFoto(), false);

            ArrayList<GalleriaCondivisa> daRimuovere = new ArrayList<>();

            //Aggiungiamo le gallerie da cui la foto deve essere rimossa a 'daRimuovere'
            for (Galleria gal : foto.getGalleriaContenitrice()) {
                if (gal instanceof GalleriaCondivisa) {
                    daRimuovere.add((GalleriaCondivisa) gal);
                }
            }

            //Rimouoviamo il legame tra la foto e le gallerie condivise che la contengono
            for(GalleriaCondivisa gal : daRimuovere){
                contienePostgresDAO.deleteFotoDaGalleria(gal.getIdGalleria(), foto.getIdFoto());

                foto.removeGalleriaContenitrice(gal);
                gal.removeFotoDAGalleria(foto);
            }

            foto.setVisibility(false);
        }
    }

    /**
     * Metodo che rende una foto privata, pubblica
     * Non vengono fatti altri controlli in quanto, se fino a quel momento la foto era privata,
     * allora essa era solo nella galleria privata e in nessun altra galleria.
     * @param fotoId
     */
    public void setFotografiaPubblica(Integer fotoId) {
        if (fotoId == null){
            System.out.println("L'id della foto passato e' null.");
            return;
        }

        Fotografia foto = getFotografiaByID(fotografieInMemory, fotoId);

        if(foto != null && !(foto.isVisibile())) {
            fotografiaPostgresDAO.updateVisibilita(foto.getIdFoto(), true);

            foto.setVisibility(true);
        }
    }

    public void creazioneVideo(String titolo, String descrizione, Integer[] foto){
        GalleriaPrivata galPrivUtente = getLoggedInUtenteGalPriv();
        Integer newVideoId = videoPostgresDAO.insertVideo(titolo, descrizione,
                galPrivUtente.getIdGalleria());

        if(newVideoId == null){
            System.out.println("Impossibile crare il video.");
            return;
        }

        ArrayList<Fotografia> fotoCompongonoVideo = new ArrayList<>();
        for(Integer i : foto){
            Fotografia f = getFotografiaByID(fotografieInMemory, i);
            if(f != null)
                fotoCompongonoVideo.add(f);
        }

        Video newVideo = new Video(newVideoId, descrizione, titolo, fotoCompongonoVideo, galPrivUtente);

        videosInMemory.add(newVideo);

        for(Fotografia f : fotoCompongonoVideo){
            f.addVideo(newVideo);
            componePostgresDAO.insertComposizione(newVideo.getIdVideo(), f.getIdFoto());
        }

        galPrivUtente.addVideo(newVideo);
    }

    /**
     * Metodo che recuperare tutte le foto scattate in un luogo
     * @param coordinate Coordinate del luogo per cui si vogliono vedere tutte le foto
     * @return ArrayList di Fotografie scattate alle coordinate passate
     */
    public ArrayList<Fotografia> getAllFotoScattateInUnLuogo(String coordinate){

        Luogo luogo = getLuogoByCoordinate(luoghiInMemory, coordinate);
        if(luogo == null){
            return new ArrayList<>();
        }else {
            return fotografieInMemory.stream()
                    .filter(f -> f.getLuogo() != null && f.getLuogo().equals(luogo))
                    .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    public ArrayList<Fotografia> getAllFotoConStessoSoggetto(String nomeSoggetto){
        Soggetto soggetto = getSoggettoByNomeSoggetto(soggettiInMemory, nomeSoggetto);

        if(soggetto == null){
            return new ArrayList<>();
        }else{
            return fotografieInMemory.stream()
                    .filter(f -> f.getSoggetti().contains(soggetto))
                    .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    public ArrayList<Luogo> getTop3Luoghi(){
        return fotografieInMemory.stream()
                .filter(f -> f.getLuogo() != null)
                .collect(Collectors.groupingBy(Fotografia::getLuogo, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Luogo, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void eliminazioneUtenteByAdmin(Integer idUtenteDaEliminare){
        if(!loggedInUtente.isAdmin()){
            System.out.println("L'utente loggato, non e' admin, di conseguenza non puo' proseguire con l'eliminazione di un utente.");
            return;
        }

        Utente utenteDaEliminare = getUtenteByID(utentiInMemory, idUtenteDaEliminare);
        if(utenteDaEliminare == null) return;

        ArrayList<Fotografia> fotoScattateDaUtente = new ArrayList<>(utenteDaEliminare.getFotoScattate());
        ArrayList<Fotografia> fotoDaEliminare = new ArrayList<>();

        for(Fotografia foto : fotoScattateDaUtente){
            //if(foto not in una galleria condivisa, eliminazione semplice){
            if(!foto.isFotoInGalleriaCondivisa()){
                fotoDaEliminare.add(foto);
            }else{ //foto è in una galleria condivisa
                boolean deveRestare = false;

                //Qui prendo i soggetti della foto che sono utenti e li metto in utentiAsSoggetti
                ArrayList<Utente> utentiAsSoggetti = new ArrayList<>();
                for(Soggetto soggetto : foto.getSoggetti()){
                    if(soggetto.isUtente() && soggetto.getUtenteRappresentato() != null)
                        utentiAsSoggetti.add(soggetto.getUtenteRappresentato());
                }

                //Vado a prendere le gallerie condivise in cui la foto è presente
                for(Galleria gall : foto.getGalleriaContenitrice()){
                    if(gall instanceof GalleriaCondivisa gallCond){
                        // per ogni partecipante vedo se sono soggetto della foto
                        for(Utente partecipante : gallCond.getPartecipanti()){
                            if(utentiAsSoggetti.contains(partecipante) && !partecipante.equals(utenteDaEliminare)){
                                deveRestare = true;
                                break;
                            }
                        }
                    }
                    if(deveRestare) break;
                }
                if(deveRestare){
                    foto.setAutore(loggedInUtente);
                    loggedInUtente.addFotoScattate(foto);
                    fotografiaPostgresDAO.updateAutore(foto.getIdFoto(), loggedInUtente.getIdUtente());
                }else{
                    // elimina foto da gal cond, la foto non ha soggetti che sono dei partecipanti
                    fotoDaEliminare.add(foto);
                }
            }
        }

        for(Fotografia foto : fotoDaEliminare){
            fotografiaPostgresDAO.deleteFotografia(foto.getIdFoto());

            fotografieInMemory.remove(foto);

            for(Soggetto soggetto : foto.getSoggetti())
                soggetto.getFotoInCuiAppare().remove(foto);

            for(Galleria gall : foto.getGalleriaContenitrice())
                gall.getFotoContenute().remove(foto);
        }

        utentePostgresDAO.deleteUtente(idUtenteDaEliminare);
        utentiInMemory.remove(utenteDaEliminare);
    }

    public boolean utentePartecipaAGallCond(GalleriaCondivisa gallCond, Utente utente){
        return gallCond.getPartecipanti().contains(utente);
    }
}
