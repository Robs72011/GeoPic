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
     */
    /**
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
     * @return Una stringa unica nel formato "segnoValore,segnoValore".
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

        //Aggiungiamo la foto alla lista dei luoghi in cui e' raffigurato il luogo (lato luogo)
        luogo.addLuogoRaffiguratoIn(newFoto);

        return newFoto;
    }

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
                                      java.util.List<GalleriaCondivisa> gallerieCondiviseSelezionate) {
        if (fotoDaCondividere == null || gallerieCondiviseSelezionate == null) {
            return;
        }

        // Se la foto e' privata, la rendiamo pubblica prima di condividerla.
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

    public void AggiungiFotoCondivisa(java.util.List<Fotografia> fotoDaCondividere,
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

        if (!utentePuoGestireVisibilitaFoto(foto)) {
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
     * @param fotoId
     */
    public void setFotografiaPubblica(Integer fotoId) {
        if (fotoId == null){
            System.out.println("L'id della foto passato e' null.");
            return;
        }

        Fotografia foto = getFotografiaByID(fotografieInMemory, fotoId);

        if (!utentePuoGestireVisibilitaFoto(foto)) {
            System.out.println("Non e' possibile modificare la visibilita' di una foto non propria.");
            return;
        }

        if(foto != null && !(foto.isVisibile())) {
            fotografiaPostgresDAO.updateVisibilita(foto.getIdFoto(), true);

            foto.setVisibility(true);
        }
    }

    public boolean utenteLoggatoPuoGestireVisibilitaFoto(Integer fotoId) {
        if (fotoId == null) {
            return false;
        }

        Fotografia foto = getFotografiaByID(fotografieInMemory, fotoId);
        return utentePuoGestireVisibilitaFoto(foto);
    }

    public boolean aggiornaVisibilitaFotografia(Integer fotoId, boolean visibile) {
        if (fotoId == null) {
            return false;
        }

        Fotografia foto = getFotografiaByID(fotografieInMemory, fotoId);
        if (!utentePuoGestireVisibilitaFoto(foto)) {
            return false;
        }

        if (foto.isVisibile() == visibile) {
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
            return false;
        }

        Integer idUtenteLoggato = loggedInUtente.getIdUtente();
        Integer idAutoreFoto = foto.getAutore().getIdUtente();

        return idUtenteLoggato != null && idUtenteLoggato.equals(idAutoreFoto);
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
    public void creazioneVideo(String titolo, String descrizione, Integer[] foto){
        GalleriaPrivata galPrivUtente = getGalleriaPersonale();
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

    /**
     * Recupera tutte le fotografie in cui appare un determinato soggetto, identificato dal nome.
     * @param nomeSoggetto Il nome del soggetto da ricercare.
     * @return Un {@link ArrayList} di {@link Fotografia} contenente il soggetto specificato;
     * restituisce una lista vuota se il soggetto non esiste o non è presente in alcuna foto.
     */
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

    /**
     * Identifica i tre luoghi più ricorrenti nelle fotografie salvate nel sistema.
     * Vengono filtrate le foto che non hanno un luogo associato.
     * @return Un {@link ArrayList} di {@link Luogo} contenente i primi 3 luoghi per numero di scatti,
     * ordinati dal più frequente al meno frequente.
     */
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
    private void eliminazioneUtenteByAdmin(Integer idUtenteDaEliminare){
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

    public void eliminazioneUtentiByAdmin(ArrayList<Integer> idsUtentiDaEliminare) {
        if (idsUtentiDaEliminare == null || idsUtentiDaEliminare.isEmpty()) return;
        if (loggedInUtente == null || !loggedInUtente.isAdmin()) return;

        for (Integer idUtente : idsUtentiDaEliminare) {
            if (idUtente == null) continue;
            if (loggedInUtente.getIdUtente().equals(idUtente)) continue;
            if (getUtenteByID(utentiInMemory, idUtente) == null) continue;
            eliminazioneUtenteByAdmin(idUtente);
        }

    }

}