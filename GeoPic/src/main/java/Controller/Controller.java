package Controller;

import ImplementazioniPostgresDAO.*;
import Model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
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

    /**
     * Esegue il caricamento completo del grafo dei dati dal database alla memoria.
     * La procedura segue due fasi distinte:
     * 1. Caricamento "piatto" delle entità base e derivate nelle liste in memoria.
     * 2. Risoluzione delle relazioni (linking) per stabilire i riferimenti bidirezionali
     * tra le diverse entità caricate.
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
     * Carica tutti gli utenti dal database e li inizializza in memoria.
     * Svuota la lista {@code utentiInMemory} e popola i nuovi oggetti {@link Utente}
     * con i dati estratti dal database.
     * @return true se il caricamento degli utenti è avvenuto con successo, false in caso di eccezione.
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
     * Carica tutte le gallerie dal database e le suddivide in memoria.
     * Svuota le liste {@code galleriePrivateInMemory} e {@code gallerieCondiviseInMemory}
     * e inizializza gli oggetti {@link GalleriaPrivata} o {@link GalleriaCondivisa}
     * in base al flag di condivisione.
     * @return true se il caricamento è avvenuto con successo, false in caso di errore.
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
     * Carica tutti i soggetti dal database e li inizializza in memoria.
     * Svuota la lista {@code soggettiInMemory} e crea i nuovi oggetti {@link Soggetto}
     * con i dati estratti dal database.
     * @return true se il caricamento dei soggetti è avvenuto con successo, false in caso di errore.
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
     * Carica tutti i luoghi dal database e li inizializza in memoria.
     * Svuota la lista {@code luoghiInMemory} e crea i nuovi oggetti {@link Luogo}
     * basandosi sui dati estratti dal database.
     * @return true se il caricamento dei luoghi è avvenuto con successo, false in caso di eccezione.
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
     * Carica tutti i video dal database e li inizializza in memoria.
     * Svuota la lista {@code videosInMemory} e popola i nuovi oggetti {@link Video}
     * con i dati estratti dal database.
     * @return true se il caricamento dei video è avvenuto con successo, false altrimenti.
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
     * Carica tutte le fotografie dal database e le inizializza in memoria.
     * Svuota la lista esistente {@code fotografieInMemory} e ripopola gli oggetti
     * {@link Fotografia} con i dati estratti dal database.
     * @return true se il caricamento è andato a buon fine, false in caso di errore.
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
     * Sincronizza in memoria la relazione di paternità tra fotografie e utenti.
     * Recupera l'associazione autore-fotografia dal database e aggiorna
     * i riferimenti bidirezionali tra gli oggetti {@link Fotografia} e {@link Utente}.
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
     * Sincronizza in memoria l'associazione tra fotografie e luoghi raffigurati.
     * Recupera le relazioni dal database e aggiorna i riferimenti incrociati
     * tra gli oggetti {@link Fotografia} e {@link Luogo}.
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
     * Sincronizza in memoria le relazioni tra fotografie e soggetti ritratti.
     * Recupera le associazioni dal database e popola i riferimenti
     * incrociati all'interno degli oggetti {@link Fotografia} e {@link Soggetto}.
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
     * Sincronizza in memoria la proprietà delle gallerie.
     * Recupera dal database le informazioni su chi possiede ciascuna galleria,
     * distinguendo tra gallerie private e condivise, e aggiorna i riferimenti
     * bidirezionali tra gli oggetti {@link Utente} e {@link Galleria}.
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
     * Sincronizza in memoria la proprietà delle gallerie.
     * Recupera dal database le informazioni su chi possiede ciascuna galleria,
     * distinguendo tra gallerie private e condivise, e aggiorna i riferimenti
     * bidirezionali tra gli oggetti {@link Utente} e {@link Galleria}.
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
     * Sincronizza in memoria le relazioni di partecipazione tra utenti e gallerie condivise.
     * Recupera le associazioni dal database e aggiorna i riferimenti bidirezionali
     * tra gli oggetti {@link Utente} e {@link GalleriaCondivisa}.
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
     * Sincronizza in memoria le relazioni tra video e gallerie private.
     * Recupera le associazioni dal database e aggiorna i riferimenti bidirezionali
     * tra gli oggetti {@link Video} e {@link GalleriaPrivata}.
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
     * Sincronizza le relazioni tra gallerie e fotografie in memoria.
     * Recupera le associazioni contenute nel database e aggiorna i riferimenti
     * incrociati all'interno degli oggetti {@link Galleria} e {@link Fotografia}.
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
     * Sincronizza in memoria le relazioni tra video e fotografie.
     * Recupera le associazioni dal database e popola le liste di riferimento
     * incrociato all'interno degli oggetti {@link Video} e {@link Fotografia}.
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
     * Cerca un utente all'interno di una lista utilizzando il suo ID univoco.
     * @param utenti       La lista di oggetti {@link Utente} in cui effettuare la ricerca.
     * @param idUserToFind L'ID dell'utente da ricercare.
     * @return L'oggetto {@link Utente} corrispondente all'ID, oppure null se non viene trovato.
     */
    private Utente getUtenteByID(ArrayList<Utente> utenti, Integer idUserToFind) {
        for (Utente utente : utenti) {
            if (utente.getIdUtente().equals(idUserToFind)) {
                return utente;
            }
        }
        return null;
    }

    /**
     * Cerca un utente all'interno di una lista utilizzando il suo nome utente.
     * @param utenti          La lista di oggetti {@link Utente} in cui effettuare la ricerca.
     * @param usernameToFind  Lo username dell'utente da ricercare.
     * @return L'oggetto {@link Utente} corrispondente allo username, oppure null se non viene trovato.
     */
    private Utente getUtenteByUsername(ArrayList<Utente> utenti, String usernameToFind) {
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
    private Fotografia getFotografiaByID(ArrayList<Fotografia> fotografie, Integer idFotoToFind) {
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
    private Galleria getGalleriaByID(Integer idGalleria) {
        Galleria galleria = getGalleriaPrivataByID(galleriePrivateInMemory, idGalleria);
        if (galleria != null) {
            return galleria;
        }

        galleria = getGalleriaCondivisaByID(gallerieCondiviseInMemory, idGalleria);
        return galleria;
    }

    /**
     * Cerca una galleria condivisa all'interno di una lista utilizzando il suo ID.
     * @param gallCondivise   La lista di oggetti {@link GalleriaCondivisa} in cui effettuare la ricerca.
     * @param galCondIdToFind L'ID della galleria condivisa da ricercare.
     * @return L'oggetto {@link GalleriaCondivisa} corrispondente all'ID, oppure null se non viene trovato.
     */
    private GalleriaCondivisa getGalleriaCondivisaByID(ArrayList<GalleriaCondivisa> gallCondivise,
                                                       Integer galCondIdToFind) {
        for (GalleriaCondivisa galleriaCondivisa : gallCondivise) {
            if (galleriaCondivisa.getIdGalleria().equals(galCondIdToFind)) {
                return galleriaCondivisa;
            }
        }
        return null;
    }

    /**
     * Cerca una galleria privata all'interno di una lista utilizzando il suo ID.
     * @param gallPrivate     La lista di oggetti {@link GalleriaPrivata} in cui effettuare la ricerca.
     * @param galPrivIdToFind L'ID della galleria privata da ricercare.
     * @return L'oggetto {@link GalleriaPrivata} corrispondente all'ID, oppure null se non viene trovato.
     */
    private GalleriaPrivata getGalleriaPrivataByID(ArrayList<GalleriaPrivata> gallPrivate,
                                                   Integer galPrivIdToFind) {
        for (GalleriaPrivata galleriaPrivata : gallPrivate) {
            if (galleriaPrivata.getIdGalleria().equals(galPrivIdToFind)) {
                return galleriaPrivata;
            }
        }
        return null;
    }

    /**
     * Cerca un luogo all'interno di una lista basandosi sulle sue coordinate.
     * @param luoghi           La lista di oggetti {@link Luogo} in cui effettuare la ricerca.
     * @param coordinateToFind La stringa delle coordinate da ricercare.
     * @return L'oggetto {@link Luogo} corrispondente alle coordinate, oppure null se non viene trovato.
     */
    private Luogo getLuogoByCoordinate(ArrayList<Luogo> luoghi, String coordinateToFind) {
        for (Luogo luogo : luoghi) {
            if (luogo.getCoordinate().equals(coordinateToFind)) {
                return luogo;
            }
        }
        return null;
    }

    /**
     * Cerca un soggetto all'interno di una lista utilizzando il suo nome.
     * @param soggetti          La lista di oggetti {@link Soggetto} in cui effettuare la ricerca.
     * @param nomeSoggettoToFind Il nome del soggetto da ricercare.
     * @return L'oggetto {@link Soggetto} trovato, oppure null se non esiste.
     */
    private Soggetto getSoggettoByNomeSoggetto(ArrayList<Soggetto> soggetti, String nomeSoggettoToFind) {
        for (Soggetto soggetto : soggetti) {
            if (soggetto.getNomeSoggetto().equals(nomeSoggettoToFind)) {
                return soggetto;
            }
        }
        return null;
    }

    /**
     * Recupera un soggetto collegato a un determinato utente (se presente).
     */
    private Soggetto getSoggettoByUtente(ArrayList<Soggetto> soggetti, Utente utenteToFind) {
        if (utenteToFind == null) {
            return null;
        }

        for (Soggetto soggetto : soggetti) {
            Utente rappresentato = soggetto.getUtenteRappresentato();
            if (rappresentato != null && rappresentato.getIdUtente().equals(utenteToFind.getIdUtente())) {
                return soggetto;
            }
        }

        return null;
    }

    /**
     * Cerca un video all'interno di una lista utilizzando il suo ID univoco.
     * @param videos       La lista di oggetti {@link Video} in cui effettuare la ricerca.
     * @param idVideoToFind L'ID del video da ricercare.
     * @return L'oggetto {@link Video} corrispondente all'ID, oppure null se non viene trovato.
     */
    private Video getVideoByID(ArrayList<Video> videos, Integer idVideoToFind) {
        for (Video video : videos) {
            if (video.getIdVideo().equals(idVideoToFind)) {
                return video;
            }
        }
        return null;
    }

    /**
     * Restituisce la lista di tutti i luoghi attualmente caricati in memoria.
     * @return Un {@link ArrayList} contenente gli oggetti {@link Luogo} presenti nel sistema.
     */
    public ArrayList<Luogo> getLuoghiInMemory() {
        return luoghiInMemory;
    }

    /**
     * Restituisce l'oggetto che rappresenta l'utente attualmente autenticato nel sistema.
     * @return L'oggetto {@link Utente} relativo all'utente loggato, o null se nessuno è autenticato.
     */
    public Utente getLoggedInUtente() {
        return loggedInUtente;
    }


    /**
     * Recupera l'elenco di tutti gli username presenti nel sistema.
     * @return Un {@link ArrayList} di stringhe contenente gli username degli utenti caricati in memoria.
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
     * Restituisce la lista di tutti gli utenti attualmente caricati in memoria.
     * @return Un {@link ArrayList} contenente gli oggetti {@link Utente} presenti nel sistema.
     */
    public ArrayList<Utente> getUtentiInMemory() {
        return utentiInMemory;
    }

    /**
     * Recupera l'elenco di tutte le fotografie presenti nella galleria privata dell'utente loggato.
     * @return Una lista di oggetti {@link Fotografia} contenuti nella galleria personale,
     * oppure una lista vuota se l'utente non è loggato o la galleria è vuota.
     *
     * Recupera la galleria personale (privata) dell'utente loggato.
     */
    public GalleriaPrivata getGalleriaPersonale() {
        if (loggedInUtente == null) return null;
        return galleriePrivateInMemory.stream()
                .filter(g -> g.getProprietario() != null && g.getProprietario().getIdUtente().equals(loggedInUtente.getIdUtente()))
                .findFirst()
                .orElse(null);
    }

    public ArrayList<Fotografia> getFotoGalleriaPersonale() {
        GalleriaPrivata gp = getGalleriaPersonale();
        return gp != null ? gp.getFotoContenute() : new ArrayList<>();
    }

    /**
     * Recupera l'elenco dei video contenuti nella galleria personale dell'utente loggato.
     * @return Una lista di oggetti {@link Video} presenti nella galleria personale,
     * oppure una lista vuota se l'utente non è loggato o la galleria è priva di video.
     */
    public ArrayList<Video> getVideoGalleriaPersonale() {
        GalleriaPrivata gp = getGalleriaPersonale();
        return gp != null ? gp.getVideos() : new ArrayList<>();
    }

    /**
     * Recupera tutte le gallerie condivise a cui l'utente loggato ha accesso.
     * Include sia le gallerie condivise create dall'utente, sia quelle a cui partecipa.
     * @return Una lista di {@link GalleriaCondivisa} accessibili all'utente.
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
     * Verifica le credenziali dell'utente e gestisce il login nel sistema.
     * @param username Lo username fornito per l'accesso.
     * @param password La password associata allo username.
     * @return 2 se l'utente è admin, 1 se l'utente è standard, -1 se l'autenticazione fallisce.
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
     * Formatta le coordinate geografiche in una stringa standardizzata.
     * @param latSign Il segno della latitudine (+ o -).
     * @param latVal  Il valore numerico della latitudine.
     * @param lonSign Il segno della longitudine (+ o -).
     * @param lonVal  Il valore numerico della longitudine.
     * @return Una stringa unica nel formato "segnoValore, segnoValore".
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
     * Verifica se un toponimo esiste già e ne restituisce le coordinate storiche.
     *
     * @param toponimo il nome del luogo da cercare
     * @return le coordinate esistenti per quel toponimo, oppure null se non esiste
     */
    public String getCoordinateEsistentiByToponimo(String toponimo) {
        if (toponimo == null) {
            return null;
        }

        String topoNormalizzato = toponimo.trim();
        if (topoNormalizzato.isEmpty()) {
            return null;
        }

        for (Luogo luogo : luoghiInMemory) {
            if (luogo == null || luogo.getNomeMnemonico() == null) {
                continue;
            }
            if (luogo.getNomeMnemonico().equalsIgnoreCase(topoNormalizzato)) {
                return luogo.getCoordinate();
            }
        }

        return null;
    }

    /**
     * Registra un nuovo utente nel sistema e gli assegna automaticamente una galleria personale.
     * @param username   Il nome univoco scelto dall'utente.
     * @param password   La password per l'accesso.
     * @param isAdmin    Indica se l'utente deve avere privilegi di amministratore.
     * @param isSoggetto Indica se l'utente può essere taggato come soggetto nelle foto.
     * @return true se l'utente e la sua galleria sono stati creati con successo, false altrimenti.
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
     * Crea una nuova foto, ne gestisce il luogo e registra i soggetti raffigurati.
     * @param dispositivo       Il nome del dispositivo usato per lo scatto.
     * @param visibilita        Se la foto deve essere pubblica (true) o privata (false).
     * @param coordinate        La posizione geografica dello scatto.
     * @param toponimo          Il nome del luogo associato alle coordinate.
     * @param nomiSoggetti      I nomi dei soggetti presenti nella foto.
     * @param categorieSoggetti Le categorie (es. Utente, Persona, Oggetto) dei soggetti.
     * @return La nuova {@link Fotografia} creata, o null se il salvataggio su DB fallisce.
     */
    public Fotografia creazioneNuovaFoto(String dispositivo, boolean visibilita, String coordinate, String toponimo,
                                         ArrayList<String> nomiSoggetti, ArrayList<String> categorieSoggetti){

        if (loggedInUtente == null) {
            System.err.println("Nessun utente loggato: impossibile creare una fotografia senza autore.");
            return null;
        }

        // Logica "First-Come, First-Served" per la gestione automatica dei conflitti
        String coordinateStoriche = getCoordinateEsistentiByToponimo(toponimo);
        if (coordinateStoriche != null) {
            coordinate = coordinateStoriche; // Usa le coordinate storiche, ignorando quelle fornite
        } else {
            String toponimoEsistente = getToponimoEsistente(coordinate);
            if (toponimoEsistente != null) {
                toponimo = toponimoEsistente; // Usa il toponimo storico, ignorando quello fornito
            }
        }

        Luogo luogo = getLuogoByCoordinate(luoghiInMemory, coordinate);
        if (luogo == null) {
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
        GalleriaPrivata autoreGalPriv = getGalleriaPersonale();

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
                                            new ArrayList<>());


        ArrayList<Soggetto> soggettiRaffigurati = new ArrayList<>();

        Soggetto soggetto;
        if (nomiSoggetti != null && categorieSoggetti != null && nomiSoggetti.size() == categorieSoggetti.size()) {
            for (int i = 0; i < nomiSoggetti.size(); i++) {
                String nomeSoggetto = nomiSoggetti.get(i);
                String categoriaSoggetto = categorieSoggetti.get(i);

                if ("Utente".equals(categoriaSoggetto)) {
                    Utente utenteSoggetto = getUtenteByUsername(utentiInMemory, nomeSoggetto);
                    if (utenteSoggetto == null) {
                        System.err.println("Soggetto utente non valido: " + nomeSoggetto);
                        continue;
                    }

                    // Un utente puo essere associato a un solo soggetto (vincolo unico su idutente).
                    soggetto = getSoggettoByUtente(soggettiInMemory, utenteSoggetto);
                    if (soggetto == null) {
                        soggettoPostgresDAO.insertUtenteAsSoggetto(nomeSoggetto, categoriaSoggetto, utenteSoggetto.getIdUtente());
                        soggetto = new Soggetto(nomeSoggetto, categoriaSoggetto, utenteSoggetto, null);
                        soggettiInMemory.add(soggetto);
                    }

                    utenteSoggetto.setSoggetto(true);
                    utentePostgresDAO.updateIsSoggetto(utenteSoggetto.getIdUtente(), true);
                } else {
                    soggetto = getSoggettoByNomeSoggetto(soggettiInMemory, nomeSoggetto);

                    if (soggetto == null) {
                        soggettoPostgresDAO.insertSoggetto(nomeSoggetto, categoriaSoggetto);
                        soggetto = new Soggetto(nomeSoggetto, categoriaSoggetto);
                        soggettiInMemory.add(soggetto);
                    }
                }

                // Mappiamo nel DB usando il nome reale del soggetto in memoria.
                mostraPostgresDAO.insertSoggettoInFoto(soggetto.getNomeSoggetto(), idNewFoto);

                if (!soggettiRaffigurati.contains(soggetto)) {
                    soggettiRaffigurati.add(soggetto);
                }
                newFoto.addSoggetto(soggetto);
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

        //Aggiungiamo la foto alla lista dei luoghi in cui è raffigurato il luogo (lato luogo)
        luogo.addLuogoRaffiguratoIn(newFoto);

        return newFoto;
    }

    /**
     *
     */
    public Fotografia creazioneNuovaFotoConCondivisione(String dispositivo,
                                                        boolean visibilita,
                                                        String coordinate,
                                                        String toponimo,
                                                        ArrayList<String> nomiSoggetti,
                                                        ArrayList<String> categorieSoggetti,
                                                        ArrayList<GalleriaCondivisa> gallerieCondiviseSelezionate) {
        Fotografia nuovaFoto = creazioneNuovaFoto(
                dispositivo,
                visibilita,
                coordinate,
                toponimo,
                nomiSoggetti,
                categorieSoggetti
        );

        if (nuovaFoto == null) {
            return null;
        }

        if (gallerieCondiviseSelezionate != null && !gallerieCondiviseSelezionate.isEmpty()) {
            AggiungiFotoCondivisa(nuovaFoto, gallerieCondiviseSelezionate);
        }

        return nuovaFoto;
    }

    /**
     * Associa una fotografia a una o più gallerie condivise, gestendo la persistenza su database
     * e l'aggiornamento dei riferimenti in memoria.
     * Il metodo esegue i seguenti controlli e operazioni per ogni galleria selezionata:
     * Verifica che la galleria esista effettivamente nella cache globale {@code gallerieCondiviseInMemory}.
     * Controlla se la fotografia è già presente nella galleria per evitare inserimenti duplicati.
     * <e la foto non è presente, inserisce il legame nel database (tabella {@code contiene}).
     * Aggiorna bidirezionalmente gli oggetti in memoria: aggiunge la galleria alla lista dei
     * contenitori della foto e la foto alla lista dei contenuti della galleria.
     * @param fotoDaCondividere          L'oggetto {@link Fotografia} da inserire nelle gallerie.
     * @param gallerieCondiviseSelezionate Una lista di {@link GalleriaCondivisa} che dovranno contenere la foto.
     * @note Se uno dei parametri è {@code null}, o se una specifica galleria non viene trovata
     * in memoria, il metodo prosegue senza lanciare eccezioni (fail-silent).
     * @see #getGalleriaCondivisaByID(ArrayList, Integer)
     */
    public void AggiungiFotoCondivisa(Fotografia fotoDaCondividere,
                                      ArrayList<GalleriaCondivisa> gallerieCondiviseSelezionate) {
        if (fotoDaCondividere == null || gallerieCondiviseSelezionate == null) {
            return;
        }

        // Se la foto è privata, la rendiamo pubblica prima di condividerla.
        if (!fotoDaCondividere.isVisibile()) {
            setFotografiaPubblica(fotoDaCondividere.getIdFoto());
            if (!fotoDaCondividere.isVisibile()) {
                return;
            }
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

    public void AggiungiFotoCondivisa(ArrayList<Fotografia> fotoDaCondividere,
                                      GalleriaCondivisa galleriaCondivisaDestinazione) {
        if (fotoDaCondividere == null || galleriaCondivisaDestinazione == null) {
            return;
        }

        ArrayList<GalleriaCondivisa> destinazione = new ArrayList<>();
        destinazione.add(galleriaCondivisaDestinazione);

        for (Fotografia foto : fotoDaCondividere) {
            if (foto != null) {
                AggiungiFotoCondivisa(foto, destinazione);
            }
        }
    }

    public ArrayList<Fotografia> getFotoCandidabiliCondivisione(Integer idGalleriaCondivisa) {
        ArrayList<Fotografia> candidabili = new ArrayList<>();

        if (idGalleriaCondivisa == null) {
            return candidabili;
        }

        GalleriaCondivisa galleriaDestinazione = getGalleriaCondivisaByID(gallerieCondiviseInMemory, idGalleriaCondivisa);
        if (galleriaDestinazione == null) {
            return candidabili;
        }

        ArrayList<Fotografia> fotoPersonali = getFotoGalleriaPersonale();
        for (Fotografia foto : fotoPersonali) {
            if (foto == null) {
                continue;
            }
            if (!galleriaDestinazione.getFotoContenute().contains(foto)) {
                candidabili.add(foto);
            }
        }

        return candidabili;
    }

    /**
     * Crea una nuova galleria condivisa nel sistema, associando un proprietario e una lista di partecipanti.
     * Il metodo esegue le seguenti operazioni core:
     * Registra la galleria nel database PostgreSQL, impostando il flag di condivisione a {@code true}.
     * Risolve gli username dei partecipanti forniti in input recuperando i relativi oggetti {@link Utente} dalla memoria.
     * Istanzia l'oggetto {@link GalleriaCondivisa} inizializzandolo con una lista vuota di fotografie.
     * Stabilisce la relazione di partecipazione sul database (tabella {@code partecipa}) e aggiorna
     * il profilo di ogni utente partecipante in memoria.
     * Aggiorna le collezioni dell'utente loggato (proprietario) e la lista globale delle gallerie condivise.
     * @param nomeGalleria       Il nome identificativo da assegnare alla galleria.
     * @param partecipantiString Un array di stringhe contenente gli username degli utenti da invitare.
     * Se {@code null}, la galleria verrà creata senza partecipanti iniziali.
     * @note Se un username non corrisponde ad alcun utente registrato, viene ignorato silenziosamente.
     * @see GalleriaCondivisa
     * @see Utente#addPartecipazioneAGalleria(GalleriaCondivisa)
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
     * Imposta una fotografia come privata, rimuovendola da tutte le gallerie condivise
     * in cui è inserita per garantirne la riservatezza.
     * Il metodo esegue le seguenti operazioni:
     * Verifica la validità dell'ID e l'esistenza della fotografia in memoria.
     * Aggiorna lo stato di visibilità nel database PostgreSQL ({@code visibile = false}).
     * Identifica tutte le istanze di {@link GalleriaCondivisa} che contengono la foto.
     * Rimuove permanentemente il legame tra la foto e tali gallerie sia sul database
     * (tabella di associazione) che nelle strutture dati in memoria.
     * Aggiorna lo stato dell'oggetto {@link Fotografia} riflettendo il cambio di visibilità.
     * @param fotoId L'identificativo univoco della fotografia da rendere privata.
     * Se {@code null}, l'operazione viene annullata con un messaggio di log.
     * @see GalleriaCondivisa
     * @see #getFotografiaByID(ArrayList, Integer)
     */
    public void setFotografiaPrivata(Integer fotoId) {
        if (fotoId == null){
            System.out.println("L'id della foto passato e' null.");
            return;
        }

        Fotografia foto = getFotografiaByID(fotografieInMemory, fotoId);

        if (utentePuoGestireVisibilitaFoto(foto)) {
            System.out.println("Non e' possibile modificare la visibilita' di una foto non propria.");
            return;
        }

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
     * @param fotoId Id della foto da rendere pubblica
     */
    public void setFotografiaPubblica(Integer fotoId) {
        if (fotoId == null){
            System.out.println("L'id della foto passato e' null.");
            return;
        }

        Fotografia foto = getFotografiaByID(fotografieInMemory, fotoId);

        if (utentePuoGestireVisibilitaFoto(foto)) {
            System.out.println("Non e' possibile modificare la visibilita' di una foto non propria.");
            return;
        }

        if(foto != null && !(foto.isVisibile())) {
            fotografiaPostgresDAO.updateVisibilita(foto.getIdFoto(), true);

            foto.setVisibility(true);
        }
    }

    public boolean aggiornaVisibilitaFotografia(Integer fotoId, boolean visibile) {
        if (fotoId == null) {
            return false;
        }

        Fotografia foto = getFotografiaByID(fotografieInMemory, fotoId);
        if (utentePuoGestireVisibilitaFoto(foto)) {
            return false;
        }

        if (foto != null && foto.isVisibile() == visibile) {
            return true;
        }

        if (visibile) {
            setFotografiaPubblica(fotoId);
        } else {
            setFotografiaPrivata(fotoId);
        }

        Fotografia fotoAggiornata = getFotografiaByID(fotografieInMemory, fotoId);
        return fotoAggiornata != null && fotoAggiornata.isVisibile() == visibile;
    }

    private boolean utentePuoGestireVisibilitaFoto(Fotografia foto) {
        if (loggedInUtente == null || foto == null || foto.getAutore() == null) {
            return true;
        }

        Integer idUtenteLoggato = loggedInUtente.getIdUtente();
        Integer idAutoreFoto = foto.getAutore().getIdUtente();

        return idUtenteLoggato == null || !idUtenteLoggato.equals(idAutoreFoto);
    }

    /**
     * Crea un nuovo video associato all'utente attualmente loggato, gestendo la persistenza
     * su database e l'aggiornamento della cache in memoria.
     * Il processo segue i seguenti step:
     * Recupera la galleria privata dell'utente loggato.
     * Inserisce il record del video nel database PostgreSQL per ottenere l'ID univoco.
     * Risolve i riferimenti degli oggetti {@link Fotografia} partendo dagli ID forniti.
     * Istanzia l'oggetto {@link Video} e lo aggiunge alla collezione globale in memoria.
     * Stabilisce la relazione molti-a-molti tra Video e Fotografia sia sul DB (tabella di composizione)
     * che negli oggetti in memoria.
     * Aggiorna la galleria privata dell'utente con il nuovo video prodotto.
     * @param titolo      Il titolo da assegnare al video.
     * @param descrizione Una breve descrizione del contenuto del video.
     * @param foto        Un array di {@link Integer} contenente gli ID delle fotografie che comporranno il video.
     * @note Se l'inserimento nel database fallisce (ID restituito nullo), l'operazione viene interrotta
     * senza modificare lo stato della memoria.
     */
    public boolean creazioneVideo(String titolo, String descrizione, Integer[] foto){
        GalleriaPrivata galPrivUtente = getGalleriaPersonale();
        if (galPrivUtente == null || foto == null || foto.length == 0) {
            return false;
        }

        Integer newVideoId = videoPostgresDAO.insertVideo(titolo, descrizione,
                galPrivUtente.getIdGalleria());

        if(newVideoId == null){
            System.out.println("Impossibile crare il video.");
            return false;
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
        return true;
    }

    /**
     * Ricerca testuale nelle foto della galleria personale per luogo (toponimo o coordinate).
     *
     * @param filtro testo cercato
     * @return elenco foto che soddisfano il filtro
     */
    public ArrayList<Fotografia> ricercaFotoPersonalePerLuogo(String filtro) {
        ArrayList<Fotografia> risultati = new ArrayList<>();
        String filtroNormalizzato = filtro != null ? filtro.trim().toLowerCase() : "";

        if (filtroNormalizzato.isEmpty()) {
            return risultati;
        }

        for (Fotografia foto : getFotoGalleriaPersonale()) {
            if (foto == null || foto.getLuogo() == null) {
                continue;
            }

            String nomeLuogo = foto.getLuogo().getNomeMnemonico() != null
                    ? foto.getLuogo().getNomeMnemonico().toLowerCase()
                    : "";
            String coordinate = foto.getLuogo().getCoordinate() != null
                    ? foto.getLuogo().getCoordinate().toLowerCase()
                    : "";

            if (nomeLuogo.contains(filtroNormalizzato) || coordinate.contains(filtroNormalizzato)) {
                risultati.add(foto);
            }
        }

        return risultati;
    }

    /**
     * Ricerca testuale nelle foto della galleria personale per soggetto (nome o categoria).
     *
     * @param filtro testo cercato
     * @return elenco foto che soddisfano il filtro
     */
    public ArrayList<Fotografia> ricercaFotoPersonalePerSoggetto(String filtro) {
        ArrayList<Fotografia> risultati = new ArrayList<>();
        String filtroNormalizzato = filtro != null ? filtro.trim().toLowerCase() : "";

        if (filtroNormalizzato.isEmpty()) {
            return risultati;
        }

        for (Fotografia foto : getFotoGalleriaPersonale()) {
            if (foto == null || foto.getSoggetti() == null || foto.getSoggetti().isEmpty()) {
                continue;
            }

            for (Soggetto soggetto : foto.getSoggetti()) {
                if (soggetto == null) {
                    continue;
                }

                String nome = soggetto.getNomeSoggetto() != null
                        ? soggetto.getNomeSoggetto().toLowerCase()
                        : "";
                String categoria = soggetto.getCategoria() != null
                        ? soggetto.getCategoria().toLowerCase()
                        : "";

                if (nome.contains(filtroNormalizzato) || categoria.contains(filtroNormalizzato)) {
                    risultati.add(foto);
                    break;
                }
            }
        }

        return risultati;
    }

    /**
     * Formatta i risultati della ricerca per la visualizzazione testuale nel Boundary.
     * Applica le regole di business per la presentazione, come il limite massimo di risultati mostrati.
     * @param risultati Lista di fotografie da formattare
     * @return Stringa formattata pronta per la GUI
     */
    public String formattaRisultatiRicercaTestuale(ArrayList<Fotografia> risultati) {
        StringBuilder sb = new StringBuilder();
        sb.append("Risultati trovati: ").append(risultati.size()).append("\n\n");

        int max = Math.min(20, risultati.size());
        for (int i = 0; i < max; i++) {
            Fotografia foto = risultati.get(i);
            sb.append("- ID ").append(foto.getIdFoto())
                    .append(" | Dispositivo: ")
                    .append(foto.getDispositivo() != null ? foto.getDispositivo() : "N/D")
                    .append("\n");
        }

        if (risultati.size() > max) {
            sb.append("\n...");
        }
        return sb.toString();
    }


    /**
     * Identifica i tre luoghi più ricorrenti nelle fotografie salvate nel sistema.
     * Vengono filtrate le foto che non hanno un luogo associato.
     * @return Un {@link ArrayList} di {@link Luogo} contenente i primi 3 luoghi per numero di scatti,
     * ordinati dal più frequente al meno frequente.
     */
    public ArrayList<Luogo> getTop3Luoghi(){
        // Calcoliamo la frequenza di ciascun luogo basandoci sulle fotografie
        Map<Luogo, Long> conteggi = fotografieInMemory.stream()
                .filter(f -> f.getLuogo() != null)
                .collect(Collectors.groupingBy(Fotografia::getLuogo, Collectors.counting()));

        // Usiamo getLuoghiInMemory() per ottenere la lista di luoghi e la ordiniamo
        // in base alla frequenza calcolata, per poi prendere i primi 3.
        return getLuoghiInMemory().stream()
                .filter(conteggi::containsKey) // Consideriamo solo luoghi presenti nelle foto
                .sorted(java.util.Comparator.comparing(conteggi::get).reversed()) // Ordiniamo per popolarità
                .limit(3) // Prendiamo il podio
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Formatta i tre luoghi più ricorrenti per la visualizzazione testuale nel Boundary.
     * @return Stringa formattata contenente la classifica, oppure null se non vi sono luoghi da mostrare.
     */
    public String formattaTop3LuoghiTestuale() {
        ArrayList<Luogo> topLuoghi = getTop3Luoghi();

        if (topLuoghi.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("I 3 luoghi più fotografati:\n\n");

        for (int i = 0; i < topLuoghi.size(); i++) {
            Luogo luogo = topLuoghi.get(i);
            String nomeMnemonico = luogo.getNomeMnemonico();

            sb.append(i + 1).append(". ");

            if (nomeMnemonico != null && !nomeMnemonico.isEmpty()) {
                sb.append(nomeMnemonico);
            } else {
                sb.append("Sconosciuto");
            }

            sb.append(" (Coordinate: ").append(luogo.getCoordinate()).append(")\n");
        }
        return sb.toString();
    }

    // --- METODI DI FORMATTAZIONE E BUSINESS LOGIC PER LA VIEW (BCE PATTERN) ---

    public String formattaDettagliFotografiaHtml(Fotografia foto) {
        if (foto == null) return "Nessuna fotografia da mostrare.";
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='text-align: left; padding: 20px;'>");
        sb.append("<h1>Dettagli Fotografia</h1>");
        sb.append("<p><b>ID Foto:</b> ").append(foto.getIdFoto()).append("</p>");
        sb.append("<p><b>Autore:</b> ").append(foto.getAutore() != null ? foto.getAutore().getUsername() : "Sconosciuto").append("</p>");
        sb.append("<p><b>Visibilità:</b> ").append(foto.isVisibile() ? "Pubblica" : "Privata").append("</p>");
        sb.append("<p><b>Dispositivo:</b> ").append(foto.getDispositivo() != null ? foto.getDispositivo() : "N/D").append("</p>");
        sb.append("<p><b>Data Scatto:</b> ").append(foto.getDataDiScatto()).append("</p>");
        sb.append("<hr>");

        if (foto.getLuogo() != null) {
            sb.append("<h2>Dettagli Luogo</h2>");
            sb.append("<p><b>Nome Luogo:</b> ").append(foto.getLuogo().getNomeMnemonico()).append("</p>");
            sb.append("<p><b>Coordinate:</b> ").append(foto.getLuogo().getCoordinate()).append("</p>");
        } else {
            sb.append("<p>Nessun luogo associato.</p>");
        }
        sb.append("<hr>");

        sb.append("<h2>Soggetti</h2>");
        if (foto.getSoggetti() != null && !foto.getSoggetti().isEmpty()) {
            sb.append("<ul>");
            for (Soggetto s : foto.getSoggetti()) {
                sb.append("<li>").append(s.getNomeSoggetto());
                if (s.getCategoria() != null && !s.getCategoria().isEmpty()) {
                    sb.append(" (").append(s.getCategoria()).append(")");
                }
                sb.append("</li>");
            }
            sb.append("</ul>");
        } else {
            sb.append("<p>Nessun soggetto presente in questa foto.</p>");
        }
        sb.append("</body></html>");
        return sb.toString();
    }

    public boolean isUtenteLoggatoAutoreDi(Fotografia foto) {
        if (loggedInUtente == null || foto == null || foto.getAutore() == null) return false;
        return loggedInUtente.equals(foto.getAutore());
    }

    public String getUsernameUtenteLoggato() {
        return loggedInUtente != null ? loggedInUtente.getUsername() : "Sconosciuto";
    }

    public String getIdUtenteLoggatoTestuale() {
        return loggedInUtente != null ? String.valueOf(loggedInUtente.getIdUtente()) : "N/D";
    }

    public String formattaDescrizioneImmagineHtml(Fotografia foto) {
        if (foto == null) return "";
        return "<html><div style='text-align: center;'><b>ID Foto: " + foto.getIdFoto() + "</b><br/>Dispositivo:<br/>" + (foto.getDispositivo() != null ? foto.getDispositivo() : "N/D") + "</div></html>";
    }

    public String formattaThumbnailVideoHtml(Fotografia foto) {
        if (foto == null) return "";
        return "<html><b>Foto ID:</b> " + foto.getIdFoto() + " <br><i>" + foto.getDataDiScatto() + "</i></html>";
    }

    public String formattaPartecipantiGalleria(GalleriaCondivisa galleria) {
        if (galleria == null || galleria.getPartecipanti() == null || galleria.getPartecipanti().isEmpty()) {
            return "Nessun partecipante";
        }
        return galleria.getPartecipanti().stream()
                .map(Utente::getUsername)
                .collect(Collectors.joining(", "));
    }

    public String formattaTitoloSlideshow(Video slideshow) {
        if (slideshow == null || slideshow.getTitolo() == null || slideshow.getTitolo().isEmpty()) {
            return "Senza titolo";
        }
        return slideshow.getTitolo();
    }

    public String getTitoloGalleriaCondivisa(GalleriaCondivisa galleria) {
        return galleria != null ? "Galleria Condivisa: " + galleria.getNomeGalleria() : "Galleria Condivisa";
    }

    public String getSottotitoloGalleriaCondivisa(GalleriaCondivisa galleria) {
        return galleria != null ? "ID: " + galleria.getIdGalleria() : "ID: N/D";
    }

    public String getNomeSempliceGalleriaCondivisa(GalleriaCondivisa galleria) {
        return (galleria != null && galleria.getNomeGalleria() != null) ? galleria.getNomeGalleria() : "Condivisa";
    }

    public ArrayList<Fotografia> getFotoDaGalleriaCondivisa(GalleriaCondivisa galleria) {
        return galleria != null && galleria.getFotoContenute() != null ? new ArrayList<>(galleria.getFotoContenute()) : new ArrayList<>();
    }

    public ArrayList<String> getUsernamesCandidabiliPerGalleria() {
        ArrayList<String> allUsers = new ArrayList<>();
        String loggedIn = getUsernameUtenteLoggato();
        allUsers.remove(loggedIn);

        if (utentiInMemory != null) {
            for (Utente u : utentiInMemory) {
                // Escludo l'utente loggato e tutti gli admin
                if (!u.getUsername().equals(loggedIn) && !u.isAdmin()) {
                    allUsers.add(u.getUsername());
                }
            }
        }
        return allUsers;
    }

    public String formattaFotoPerLista(Fotografia foto) {
        if (foto == null || foto.getIdFoto() == null) return "N/D";
        String dispositivo = foto.getDispositivo() != null ? foto.getDispositivo() : "N/D";
        return "ID " + foto.getIdFoto() + " - " + dispositivo;
    }

    public String formattaFotoPerEliminazione(Fotografia foto) {
        if (foto == null) return "";
        return "ID: " + foto.getIdFoto() + " | Dispositivo: " +
                (foto.getDispositivo() != null ? foto.getDispositivo() : "N/D") +
                " (" + foto.getDataDiScatto() + ")";
    }

    public Object[] formattaRigaTabellaFoto(Fotografia foto) {
        if (foto == null) return new Object[]{"N/D", "N/D", "N/D", "N/D"};
        return new Object[]{
                String.valueOf(foto.getIdFoto()),
                foto.getDispositivo() != null ? foto.getDispositivo() : "N/D",
                foto.getDataDiScatto() != null ? foto.getDataDiScatto().toString() : "N/D",
                foto.isVisibile() ? "Pubblica" : "Privata"
        };
    }

    public Object[] formattaRigaTabellaUtente(Utente u) {
        if (u == null) return new Object[]{Boolean.FALSE, "N/D", "N/D", "N/D", "N/D"};
        return new Object[]{
                Boolean.FALSE,
                u.getIdUtente(),
                u.getUsername() != null ? u.getUsername() : "Sconosciuto",
                u.isAdmin() ? "Sì" : "No",
                u.isSoggetto() ? "Sì" : "No"
        };
    }


    /**
     * Esegue l'eliminazione di un utente dal sistema da parte di un amministratore,
     * gestendo la persistenza o la rimozione delle sue fotografie.
     * La logica di eliminazione segue queste regole:
     * Se la foto non è in una galleria condivisa, viene eliminata.0
     * Resta nel sistema (cambiando autore all'admin corrente) se tra i soggetti della foto
     * Se la foto è in una galleria condivisa:
     * è presente almeno un altro partecipante della galleria.
     * Viene eliminata se non ci sono altri partecipanti della galleria ritratti nella foto.
     * @param idUtenteDaEliminare L'identificativo univoco dell'utente da rimuovere.
     * @throws SecurityException Se l'utente attualmente loggato non ha privilegi di amministratore.
     */

    public void eliminazioneUtenteByAdmin(Integer idUtenteDaEliminare){
        if(!loggedInUtente.isAdmin()){
            System.out.println("L'utente loggato non e' admin.");
            return;
        }

        Utente utenteDaEliminare = getUtenteByID(utentiInMemory, idUtenteDaEliminare);
        if(utenteDaEliminare == null) return;

        ArrayList<Fotografia> fotoDaEliminare = new ArrayList<>();
        ArrayList<Fotografia> fotoDaPreservare = new ArrayList<>();

        for(Fotografia foto : utenteDaEliminare.getFotoScattate()){

            ArrayList<Galleria> galleriaDaCuiTogliereLaFoto = new ArrayList<>();
            boolean photoStays = false;

            if(foto.isFotoPartOfVideo()){

                photoStays = true;
                // qui impostiamo la data di eliminazione perche' e' l'unico caso in cui l'impostiamo
                foto.setDataDiEliminazione(LocalDate.now());
                fotografiaPostgresDAO.updateDataEliminazione(foto.getIdFoto(),  foto.getDataDiEliminazione());

                // settare ad admin lo facciamo alla fine ciclando su fotodapreservare
                if(!fotoDaPreservare.contains(foto))
                    fotoDaPreservare.add(foto);
            }

            if(foto.isFotoInGalleriaCondivisa()){
                ArrayList<Utente> utentiInFoto = new ArrayList<>(foto.getUtentiInFoto());

                if(utentiInFoto.isEmpty() && !photoStays){
                    //System.out.println("ti amo");
                    fotoDaEliminare.add(foto);

                }else{ // i soggetti nella foto sono degli utenti

                    //ciclo sulle gallerie in cui e' contenuta la foto
                    for(Galleria gal : foto.getGalleriaContenitrice()){
                        if(gal instanceof GalleriaCondivisa galCond){

                            //mi vado a prendere i partecipanti della galleria
                            ArrayList<Utente> partecipanti = new ArrayList<>(galCond.getPartecipanti());

                            boolean validSoggetto = false;

                            for(Utente partecipante : partecipanti){
                                // se nella foto ci sono utenti che non sono l'utente da eliminare deve essere conservata
                                if(utentiInFoto.contains(partecipante) && !partecipante.equals(utenteDaEliminare)){
                                    validSoggetto = true;
                                    break;
                                }
                            }

                            if(validSoggetto) photoStays = true;
                            else galleriaDaCuiTogliereLaFoto.add(galCond);
                        }
                    }

                    if(photoStays && !fotoDaPreservare.contains(foto)) fotoDaPreservare.add(foto);
                    else if(!photoStays && !fotoDaEliminare.contains(foto)) fotoDaEliminare.add(foto);
                }
            }else{
                if(!photoStays && !fotoDaEliminare.contains(foto)) fotoDaEliminare.add(foto);
                else if (photoStays && !fotoDaPreservare.contains(foto)) fotoDaPreservare.add(foto);
            }

            // tolgo effettivamente la foto dalla galleria in cui non va bene
            for(Galleria gal : galleriaDaCuiTogliereLaFoto){
                foto.getGalleriaContenitrice().remove(gal);
                gal.getFotoContenute().remove(foto);

                contienePostgresDAO.deleteFotoDaGalleria(gal.getIdGalleria(), foto.getIdFoto());
            }

            galleriaDaCuiTogliereLaFoto.clear();
        }

        //elimino le foto dal db e dalla memoria
        for(Fotografia foto : fotoDaEliminare){

            System.out.println(foto.getIdFoto());
            fotografieInMemory.remove(foto);

            fotografiaPostgresDAO.deleteFotografia(foto.getIdFoto());
        }

        //aggiorno l'autore delle foto che devono rimanere
        for(Fotografia foto : fotoDaPreservare){
            foto.setAutore(loggedInUtente);
            fotografiaPostgresDAO.updateAutore(foto.getIdFoto(), loggedInUtente.getIdUtente());
        }


        for(Galleria gal : new ArrayList<>(utenteDaEliminare.getGalleriePossedute())){
            // vado a trasferire la proprieta' delle gallerie condivise a un nuovo utente
            if(gal instanceof GalleriaCondivisa galCond){
                //l'owner della galleria cond diventa admin
                Utente newOwner = loggedInUtente;
                galCond.setProprietario(newOwner);
                utenteDaEliminare.getGalleriePossedute().remove(galCond);

                galleriaPostgresDAO.updateOwner(galCond.getIdGalleria(), newOwner.getIdUtente());
            }
        }

        for(Galleria gal : new ArrayList<>(utenteDaEliminare.getUtentePartecipaGalleriaCondivisa())) {
            if(gal instanceof GalleriaCondivisa galCond) {
                galCond.getPartecipanti().remove(utenteDaEliminare);
                partecipaPostgresDAO.deletePartecipante(galCond.getIdGalleria(), utenteDaEliminare.getIdUtente());
            }
        }


        //vado eliminare la galleria privata dell'utente che sta per essere eliminato
        for(Galleria gal : utenteDaEliminare.getGalleriePossedute()){
            if(gal instanceof GalleriaPrivata galPriv){

                //vado a cancellare i video
                for(Video video : galPriv.getVideos()){
                    videosInMemory.remove(video);

                    videoPostgresDAO.deleteVideo(video.getIdVideo());
                }

                galleriePrivateInMemory.remove(galPriv);
                galleriaPostgresDAO.deleteGalleria(galPriv.getIdGalleria());
            }
        }

        utentePostgresDAO.deleteUtente(utenteDaEliminare.getIdUtente());
        utentiInMemory.remove(utenteDaEliminare);
    }


    /**
     * Gestisce l'eliminazione massiva di utenti da parte di un amministratore.
     * Verifica i permessi dell'utente loggato e impedisce l'auto-eliminazione.
     * @param idsUtentiDaEliminare La lista degli ID degli utenti da rimuovere.
     */
    public void eliminazioneUtenteByAdmin(ArrayList<Integer> idsUtentiDaEliminare) {
        if (idsUtentiDaEliminare == null || idsUtentiDaEliminare.isEmpty()) return;
        if (loggedInUtente == null || !loggedInUtente.isAdmin()) return;

        for (Integer idUtente : idsUtentiDaEliminare) {
            if (idUtente == null) continue;
            if (loggedInUtente.getIdUtente().equals(idUtente)) continue;
            if (getUtenteByID(utentiInMemory, idUtente) == null) continue;
            eliminazioneUtenteByAdmin(idUtente);
        }
    }


    /**
     * Avvia l'iter di eliminazione per un elenco di fotografie presenti nella galleria privata.
     * @param idsFotoDaEliminare La lista degli ID delle fotografie da eliminare.
     */
    public void eliminazioneFotoGalleriaPrivata(ArrayList<Integer> idsFotoDaEliminare) {
        if (idsFotoDaEliminare == null || idsFotoDaEliminare.isEmpty()) return;
        for (Integer idFoto : idsFotoDaEliminare) {
            if (idFoto != null) {
                eliminazioneFotoGalleriaPrivata(idFoto);
            }
        }
    }

    /**
     * Esegue l'eliminazione di una singola fotografia dalla galleria privata.
     * Il metodo si occupa di rimuovere i riferimenti reciproci in memoria, gestire la persistenza
     * della data di eliminazione (per i vincoli relativi ai video) e aggiornare il database tramite DAO.
     * @param idFoto L'identificativo univoco della foto da rimuovere.
     */
    public void eliminazioneFotoGalleriaPrivata(Integer idFoto) {
        //vado a prendere la foto da eliminare direttamente dalla galleria personale dell'utente
        Fotografia fotoDaEliminare = getFotografiaByID(getFotoGalleriaPersonale(), idFoto);

        if (idFoto == null || fotoDaEliminare == null) return;

        //vado a prendere la galleria personale dell'utente
        GalleriaPrivata galleriaPrivata = getGalleriaPersonale();

        //rompo il legame foto - galleria in memoria
        fotoDaEliminare.getGalleriaContenitrice().remove(galleriaPrivata);

        //rompo il legame galleria - foto in memoria
        galleriaPrivata.getFotoContenute().remove(fotoDaEliminare);

        if (!fotoDaEliminare.getFotoComponeVideo().isEmpty()) {
            //imposto la data di eliminazione in memoria, per la questione video
            fotoDaEliminare.setDataDiEliminazione(LocalDate.now());

            //aggiorno la data di eliminazione su DB
            fotografiaPostgresDAO.updateDataEliminazione(fotoDaEliminare.getIdFoto(), fotoDaEliminare.getDataDiEliminazione());
        }

        //rompo il legame tra la foto e la galleria privata nel DB
        contienePostgresDAO.deleteFotoDaGalleria(galleriaPrivata.getIdGalleria(), fotoDaEliminare.getIdFoto());

        if(!fotoDaEliminare.isFotoInGalleriaCondivisa() && !fotoDaEliminare.isFotoPartOfVideo()){

            fotografiaPostgresDAO.deleteFotografia(fotoDaEliminare.getIdFoto());
        }
    }
}