package View;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AppMain {

    // ---- MODELLI VIEW ----
    public record UserItem(String id, String username) {}
    public record GalleryItem(String id, String nome, String tipo) {}  // "Personale"/"Condivisa"
    public record PhotoItem(String id, String titolo) {}

    // ---- CARD NAMES ----
    private static final String START   = "START";
    private static final String ADMIN   = "ADMIN";
    private static final String USER    = "USER";
    private static final String UPLOAD  = "UPLOAD";
    private static final String GALLERY = "GALLERY";
    private static final String PHOTO   = "PHOTO";

    // ---- DEMO DATA (SOSTITUISCI CON I TUOI SERVICE/CONTROLLER) ----
    private static class InMemoryStore {
        final List<UserItem> users = new ArrayList<>();
        final List<GalleryItem> galleries = new ArrayList<>();
        final Map<String, List<PhotoItem>> photosByGallery = new HashMap<>();

        InMemoryStore() {
            users.add(new UserItem("u1", "mario"));
            users.add(new UserItem("u2", "anna"));
            users.add(new UserItem("u3", "valentina"));

            galleries.add(new GalleryItem("g1", "Vacanze 2024", "Personale"));
            galleries.add(new GalleryItem("g2", "Uni", "Personale"));
            galleries.add(new GalleryItem("g3", "Team Project", "Condivisa"));

            photosByGallery.put("g1", new ArrayList<>(List.of(
                    new PhotoItem("p1", "spiaggia.jpg"),
                    new PhotoItem("p2", "tramonto.jpg")
            )));
            photosByGallery.put("g2", new ArrayList<>(List.of(
                    new PhotoItem("p3", "appunti.png")
            )));
            photosByGallery.put("g3", new ArrayList<>());
        }

        List<UserItem> listUsers() { return new ArrayList<>(users); }
        boolean deleteUser(String userId) { return users.removeIf(u -> u.id().equals(userId)); }

        List<GalleryItem> listGalleriesForUser(String userId) {
            return new ArrayList<>(galleries);
        }

        List<PhotoItem> listPhotos(String galleryId) {
            return new ArrayList<>(photosByGallery.getOrDefault(galleryId, List.of()));
        }

        void uploadPhoto(String galleryId, String filePath) {
            String newId = "p" + (1000 + new Random().nextInt(9000));
            String name = filePath.substring(Math.max(filePath.lastIndexOf('\\'), filePath.lastIndexOf('/')) + 1);
            photosByGallery.computeIfAbsent(galleryId, k -> new ArrayList<>()).add(new PhotoItem(newId, name));
        }

        void uploadVideo(String galleryId, String filePath) {
            System.out.println("Upload video in galleryId=" + galleryId + " file=" + filePath);
        }

        void importGallery(String filePath) {
            String newId = "g" + (1000 + new Random().nextInt(9000));
            String name = filePath.substring(Math.max(filePath.lastIndexOf('\\'), filePath.lastIndexOf('/')) + 1);
            galleries.add(new GalleryItem(newId, "Import " + name, "Personale"));
            photosByGallery.putIfAbsent(newId, new ArrayList<>());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InMemoryStore store = new InMemoryStore();

            JFrame frame = new JFrame("GeoPic");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(950, 520);
            frame.setLocationRelativeTo(null);

            CardLayout cards = new CardLayout();
            JPanel root = new JPanel(cards);

            // Holder per risolvere le lambda che referenziano pannelli creati dopo
            AtomicReference<AdminHomePanel> adminRef = new AtomicReference<>();
            AtomicReference<UserHomePanel> userRef = new AtomicReference<>();
            AtomicReference<GalleryPanel> galleryRef = new AtomicReference<>();

            // Panel riusabile
            UploadFilePanel upload = new UploadFilePanel();

            // PHOTO
            PhotoPanel photoPanel = new PhotoPanel(() -> cards.show(root, GALLERY));

            // GALLERY
            GalleryPanel galleryPanel = new GalleryPanel(
                    () -> { // back to USER
                        userRef.get().reload(store.listGalleriesForUser("u3"));
                        cards.show(root, USER);
                    },
                    (galleryId) -> { // refresh photos for current gallery
                        galleryRef.get().reload(store.listPhotos(galleryId));
                    },
                    (photoId, titolo) -> { // open photo
                        photoPanel.showPhoto(photoId, titolo);
                        cards.show(root, PHOTO);
                    }
            );
            galleryRef.set(galleryPanel);

            // ADMIN
            AdminHomePanel admin = new AdminHomePanel(
                    () -> adminRef.get().reload(store.listUsers()),   // refresh
                    (userId) -> store.deleteUser(userId),             // delete
                    () -> cards.show(root, START)                     // logout
            );
            adminRef.set(admin);

            // USER
            UserHomePanel user = new UserHomePanel(
                    () -> userRef.get().reload(store.listGalleriesForUser("u3")), // refresh
                    () -> cards.show(root, START),                                // logout

                    // open gallery (doppio click)
                    (galleryId, nome, tipo) -> {
                        galleryPanel.setGalleryContext(galleryId, nome, tipo);
                        galleryPanel.reload(store.listPhotos(galleryId));
                        cards.show(root, GALLERY);
                    },

                    // carica foto (serve galleria selezionata)
                    (galleryId, galleryName) -> {
                        upload.configure(
                                "Carica Foto",
                                "Caricherai una foto nella galleria: " + galleryName,
                                () -> { userRef.get().reload(store.listGalleriesForUser("u3")); cards.show(root, USER); },
                                (filePath) -> store.uploadPhoto(galleryId, filePath)
                        );
                        cards.show(root, UPLOAD);
                    },

                    // carica video (serve galleria selezionata)
                    (galleryId, galleryName) -> {
                        upload.configure(
                                "Carica Video",
                                "Caricherai un video nella galleria: " + galleryName,
                                () -> { userRef.get().reload(store.listGalleriesForUser("u3")); cards.show(root, USER); },
                                (filePath) -> store.uploadVideo(galleryId, filePath)
                        );
                        cards.show(root, UPLOAD);
                    },

                    // carica galleria (non richiede selezione)
                    () -> {
                        upload.configure(
                                "Carica Galleria",
                                "Importerai una galleria da file.",
                                () -> { userRef.get().reload(store.listGalleriesForUser("u3")); cards.show(root, USER); },
                                store::importGallery
                        );
                        cards.show(root, UPLOAD);
                    }
            );
            userRef.set(user);

            // START (ora puoi referenziare adminRef/userRef senza errori)
            StartPanel start = new StartPanel(
                    () -> { adminRef.get().reload(store.listUsers()); cards.show(root, ADMIN); },
                    () -> { userRef.get().reload(store.listGalleriesForUser("u3")); cards.show(root, USER); }
            );

            // add cards
            root.add(start, START);
            root.add(admin, ADMIN);
            root.add(user, USER);
            root.add(upload, UPLOAD);
            root.add(galleryPanel, GALLERY);
            root.add(photoPanel, PHOTO);

            frame.setContentPane(root);
            frame.setVisible(true);
            cards.show(root, START);
        });
    }
}