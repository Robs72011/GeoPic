package View;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class AppMain {

    public static class UserItem {
        private final String id;
        private final String username;

        public UserItem(String id, String username) {
            this.id = id;
            this.username = username;
        }

        public String getId() { return id; }
        public String getUsername() { return username; }
        @Override public String toString() { return username; }
    }

    public static class GalleryItem {
        private final String id;
        private final String nome;
        private final String tipo;
        private final List<String> partecipanti;

        public GalleryItem(String id, String nome, String tipo, List<String> partecipanti) {
            this.id = id;
            this.nome = nome;
            this.tipo = tipo;
            this.partecipanti = new ArrayList<>(partecipanti);
        }

        public String getId() { return id; }
        public String getNome() { return nome; }
        public String getTipo() { return tipo; }
        public List<String> getPartecipanti() { return new ArrayList<>(partecipanti); }
        public boolean isPersonale() { return "Personale".equalsIgnoreCase(tipo); }
    }

    public static class SubjectItem {
        private final String nome;
        private final String categoria;

        public SubjectItem(String nome, String categoria) {
            this.nome = nome;
            this.categoria = categoria;
        }

        public String getNome() { return nome; }
        public String getCategoria() { return categoria; }
    }

    public static class PhotoItem {
        private final String id;
        private final String dataScatto;
        private final String latitudine;
        private final String longitudine;
        private final String luogo;
        private final String dispositivo;
        private final List<SubjectItem> soggetti;

        public PhotoItem(String id, String dataScatto, String latitudine, String longitudine,
                         String luogo, String dispositivo, List<SubjectItem> soggetti) {
            this.id = id;
            this.dataScatto = dataScatto;
            this.latitudine = latitudine;
            this.longitudine = longitudine;
            this.luogo = luogo;
            this.dispositivo = dispositivo;
            this.soggetti = new ArrayList<>(soggetti);
        }

        public String getId() { return id; }
        public String getDataScatto() { return dataScatto; }
        public String getLatitudine() { return latitudine; }
        public String getLongitudine() { return longitudine; }
        public String getLuogo() { return luogo; }
        public String getDispositivo() { return dispositivo; }
        public List<SubjectItem> getSoggetti() { return new ArrayList<>(soggetti); }
        public String getCoordinate() { return latitudine + " " + longitudine; }
    }

    public static class VideoItem {
        private final String id;
        private final String nome;
        private final List<String> fotoIds;

        public VideoItem(String id, String nome, List<String> fotoIds) {
            this.id = id;
            this.nome = nome;
            this.fotoIds = new ArrayList<>(fotoIds);
        }

        public String getId() { return id; }
        public String getNome() { return nome; }
        public List<String> getFotoIds() { return new ArrayList<>(fotoIds); }
    }

    private static final String START = "START", ADMIN = "ADMIN", USER = "USER",
            GALLERY = "GALLERY", PHOTO = "PHOTO", VIDEO = "VIDEO", UPLOAD = "UPLOAD";

    private static final String CURRENT_USERNAME = "valentina";
    private static final List<UserItem> utentiSistema = new ArrayList<>();
    private static final List<GalleryItem> mieGallerie = new ArrayList<>();
    private static final Map<String, List<PhotoItem>> fotoPerGalleria = new HashMap<>();
    private static final Map<String, List<VideoItem>> videoPerGalleria = new HashMap<>();
    private static int galleryCounter = 1;
    private static int photoCounter = 1;
    private static int videoCounter = 1;

    public static void main(String[] args) {
        initDemoData();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("GeoPic System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);

            CardLayout cards = new CardLayout();
            JPanel root = new JPanel(cards);

            AtomicReference<UserHomePanel> userHomeRef = new AtomicReference<>();
            AtomicReference<GalleryItem> currentGalleryRef = new AtomicReference<>();

            PhotoPanel photoPanel = new PhotoPanel(() -> cards.show(root, GALLERY));
            VideoPanel videoPanel = new VideoPanel(() -> cards.show(root, GALLERY));
            UploadFilePanel uploadPanel = new UploadFilePanel();

            AdminHomePanel adminHome = new AdminHomePanel(() -> {}, id -> {}, () -> cards.show(root, START));

            final GalleryPanel[] galleryPanelHolder = new GalleryPanel[1];
            GalleryPanel galleryPanel = new GalleryPanel(
                    () -> cards.show(root, USER),
                    galleryId -> reloadGalleryPanel(galleryPanelHolder[0], galleryId),
                    gallery -> {
                        uploadPanel.configure(
                                "Aggiungi foto in: " + gallery.getNome(),
                                () -> cards.show(root, GALLERY),
                                dati -> {
                                    addPhotoToGallery(gallery.getId(), dati);
                                    reloadGalleryPanel(galleryPanelHolder[0], gallery.getId());
                                    cards.show(root, GALLERY);
                                }
                        );
                        cards.show(root, UPLOAD);
                    },
                    gallery -> {
                        if (!gallery.isPersonale()) {
                            JOptionPane.showMessageDialog(frame, "I video si trovano solo nella galleria personale.");
                            return;
                        }
                        createVideoDialog(frame, gallery);
                        reloadGalleryPanel(galleryPanelHolder[0], gallery.getId());
                    },
                    photo -> {
                        photoPanel.showPhoto(photo);
                        cards.show(root, PHOTO);
                    },
                    video -> {
                        GalleryItem cg = currentGalleryRef.get();
                        List<PhotoItem> seq = resolveVideoSequence(cg != null ? cg.getId() : null, video);
                        videoPanel.showVideo(video, seq);
                        cards.show(root, VIDEO);
                    }
            );
            galleryPanelHolder[0] = galleryPanel;

            UserHomePanel userHome = new UserHomePanel(
                    () -> userHomeRef.get().reload(mieGallerie),
                    () -> cards.show(root, START),
                    (id, n, t) -> {
                        GalleryItem g = findGalleryById(id);
                        if (g == null) return;
                        currentGalleryRef.set(g);
                        galleryPanel.setGalleryContext(g);
                        reloadGalleryPanel(galleryPanel, g.getId());
                        cards.show(root, GALLERY);
                    },
                    () -> {
                        createSharedGalleryDialog(frame);
                        userHomeRef.get().reload(mieGallerie);
                    }
            );
            userHomeRef.set(userHome);
            userHome.setSearchActions(
                    q -> JOptionPane.showMessageDialog(frame, "Ricerca coordinate (demo): " + q),
                    q -> JOptionPane.showMessageDialog(frame, "Ricerca soggetto (demo): " + q),
                    () -> JOptionPane.showMessageDialog(frame, "Top 3 luoghi (demo): Roma, Napoli, Milano")
            );

            StartPanel startPanel = new StartPanel(() -> cards.show(root, ADMIN), () -> cards.show(root, USER));

            root.add(startPanel, START);
            root.add(adminHome, ADMIN);
            root.add(userHome, USER);
            root.add(galleryPanel, GALLERY);
            root.add(photoPanel, PHOTO);
            root.add(videoPanel, VIDEO);
            root.add(uploadPanel, UPLOAD);

            frame.setContentPane(root);
            userHome.reload(mieGallerie);
            cards.show(root, START);
            frame.setVisible(true);
        });
    }

    private static void initDemoData() {
        if (!utentiSistema.isEmpty()) return;

        utentiSistema.add(new UserItem("U1", "valentina"));
        utentiSistema.add(new UserItem("U2", "mario"));
        utentiSistema.add(new UserItem("U3", "anna"));
        utentiSistema.add(new UserItem("U4", "luca"));
        utentiSistema.add(new UserItem("U5", "sara"));

        GalleryItem personale = createGalleryInternal("La mia galleria personale", "Personale", List.of(CURRENT_USERNAME));
        GalleryItem condivisa = createGalleryInternal("Progetto GeoPic Team", "Condivisa", List.of(CURRENT_USERNAME, "mario", "anna"));

        fotoPerGalleria.get(personale.getId()).add(new PhotoItem(nextPhotoId(), nowString(), "40.8518", "14.2681", "Napoli", "Pixel 8",
                List.of(new SubjectItem("Monumento", "Architettura"), new SubjectItem("Piazza", "Luogo"))));
        fotoPerGalleria.get(personale.getId()).add(new PhotoItem(nextPhotoId(), nowString(), "41.9028", "12.4964", "Roma", "iPhone 13",
                List.of(new SubjectItem("Colosseo", "Monumento"), new SubjectItem("Turisti", "Persone"))));
        fotoPerGalleria.get(personale.getId()).add(new PhotoItem(nextPhotoId(), nowString(), "45.4642", "9.1900", "Milano", "Canon EOS R50",
                List.of(new SubjectItem("Duomo", "Monumento"))));
        fotoPerGalleria.get(condivisa.getId()).add(new PhotoItem(nextPhotoId(), nowString(), "40.8359", "14.2488", "Lungomare Napoli", "Samsung S23",
                List.of(new SubjectItem("Mare", "Natura"), new SubjectItem("Amici", "Persone"))));

        List<String> ids = fotoPerGalleria.get(personale.getId()).stream().limit(2).map(PhotoItem::getId).collect(Collectors.toList());
        videoPerGalleria.get(personale.getId()).add(new VideoItem(nextVideoId(), "Video demo vacanza", ids));
    }

    private static GalleryItem createGalleryInternal(String nome, String tipo, List<String> partecipanti) {
        GalleryItem g = new GalleryItem(nextGalleryId(), nome, tipo, new ArrayList<>(new LinkedHashSet<>(partecipanti)));
        mieGallerie.add(g);
        fotoPerGalleria.putIfAbsent(g.getId(), new ArrayList<>());
        videoPerGalleria.putIfAbsent(g.getId(), new ArrayList<>());
        return g;
    }

    private static void createSharedGalleryDialog(Component parent) {
        JTextField nomeField = new JTextField(20);
        DefaultListModel<String> userModel = new DefaultListModel<>();
        for (UserItem u : utentiSistema) {
            if (!CURRENT_USERNAME.equalsIgnoreCase(u.getUsername())) userModel.addElement(u.getUsername());
        }
        JList<String> usersList = new JList<>(userModel);
        usersList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        usersList.setVisibleRowCount(6);

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel top = new JPanel(new GridLayout(0, 1, 4, 4));
        top.add(new JLabel("Nome galleria condivisa:"));
        top.add(nomeField);
        top.add(new JLabel("Aggiungi uno o più utenti esistenti:"));
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(usersList), BorderLayout.CENTER);

        int res = JOptionPane.showConfirmDialog(parent, panel, "Nuova galleria condivisa", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String nome = nomeField.getText() == null ? "" : nomeField.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Inserisci il nome della galleria.");
            return;
        }

        Set<String> partecipanti = new LinkedHashSet<>();
        partecipanti.add(CURRENT_USERNAME);
        partecipanti.addAll(usersList.getSelectedValuesList());
        createGalleryInternal(nome, "Condivisa", new ArrayList<>(partecipanti));
    }

    private static void createVideoDialog(Component parent, GalleryItem gallery) {
        List<PhotoItem> photos = fotoPerGalleria.getOrDefault(gallery.getId(), List.of());
        if (photos.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Nessuna foto nella galleria personale. Aggiungi prima delle foto.");
            return;
        }

        JTextField nomeVideoField = new JTextField("Video " + (videoPerGalleria.getOrDefault(gallery.getId(), List.of()).size() + 1), 20);
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (PhotoItem p : photos) {
            listModel.addElement(p.getId() + " | " + p.getDataScatto() + " | " + p.getLuogo());
        }
        JList<String> list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setVisibleRowCount(8);

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel top = new JPanel(new GridLayout(0, 1, 4, 4));
        top.add(new JLabel("Nome video:"));
        top.add(nomeVideoField);
        top.add(new JLabel("Seleziona le foto da prendere per il video:"));
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);

        int res = JOptionPane.showConfirmDialog(parent, panel, "Crea Video", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String nomeVideo = nomeVideoField.getText() == null ? "" : nomeVideoField.getText().trim();
        if (nomeVideo.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Inserisci un nome video.");
            return;
        }

        int[] selected = list.getSelectedIndices();
        if (selected.length == 0) {
            JOptionPane.showMessageDialog(parent, "Seleziona almeno una foto.");
            return;
        }

        List<String> photoIds = new ArrayList<>();
        for (int idx : selected) photoIds.add(photos.get(idx).getId());
        videoPerGalleria.computeIfAbsent(gallery.getId(), k -> new ArrayList<>()).add(new VideoItem(nextVideoId(), nomeVideo, photoIds));
    }

    private static void addPhotoToGallery(String galleryId, UploadFilePanel.DatiFotoInput dati) {
        List<SubjectItem> soggetti = dati.soggetti().stream()
                .map(s -> new SubjectItem(s.nome(), s.categoria()))
                .collect(Collectors.toList());
        PhotoItem p = new PhotoItem(
                nextPhotoId(),
                (dati.dataScatto() == null || dati.dataScatto().isBlank()) ? nowString() : dati.dataScatto().trim(),
                dati.latitudine().trim(),
                dati.longitudine().trim(),
                dati.luogo().trim(),
                dati.dispositivo().trim(),
                soggetti
        );
        fotoPerGalleria.computeIfAbsent(galleryId, k -> new ArrayList<>()).add(p);
    }

    private static void reloadGalleryPanel(GalleryPanel panel, String galleryId) {
        if (panel == null || galleryId == null) return;
        panel.reload(fotoPerGalleria.getOrDefault(galleryId, List.of()), videoPerGalleria.getOrDefault(galleryId, List.of()));
    }

    private static List<PhotoItem> resolveVideoSequence(String galleryId, VideoItem video) {
        if (galleryId == null) return List.of();
        Map<String, PhotoItem> byId = new HashMap<>();
        for (PhotoItem p : fotoPerGalleria.getOrDefault(galleryId, List.of())) byId.put(p.getId(), p);
        List<PhotoItem> result = new ArrayList<>();
        for (String id : video.getFotoIds()) {
            if (byId.containsKey(id)) result.add(byId.get(id));
        }
        return result;
    }

    private static GalleryItem findGalleryById(String id) {
        for (GalleryItem g : mieGallerie) if (g.getId().equals(id)) return g;
        return null;
    }

    private static String nextGalleryId() { return "G" + (galleryCounter++); }
    private static String nextPhotoId() { return "F" + (photoCounter++); }
    private static String nextVideoId() { return "V" + (videoCounter++); }
    private static String nowString() { return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); }

    @FunctionalInterface
    public interface TriConsumer<A, B, C> { void accept(A a, B b, C c); }
}
