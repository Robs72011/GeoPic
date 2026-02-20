package View;

import Model.Soggetto;
import View.UploadFilePanel.DatiFotoInput;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AppMain {

    public record UserItem(String id, String username) {}
    public record GalleryItem(String id, String nome, String tipo) {}
    public record PhotoItem(String id, String titolo) {}

    private static final String START   = "START";
    private static final String USER    = "USER";
    private static final String UPLOAD  = "UPLOAD";
    private static final String GALLERY = "GALLERY";
    private static final String PHOTO   = "PHOTO";

    private static class InMemoryStore {
        // Simulazione tabelle SQL
        final List<GalleryItem> allGalleries = new ArrayList<>();
        final Map<String, List<PhotoItem>> photosByGallery = new HashMap<>();

        // Mappa delle partecipazioni (IDGalleria -> Set di IDUtenti partecipanti)
        final Map<String, Set<String>> partecipazioni = new HashMap<>();

        InMemoryStore() {
            // Galleria Personale (Proprietario u1)
            allGalleries.add(new GalleryItem("G000000001", "Le mie vacanze", "Personale"));
            // Gallerie Condivise (u1 partecipa ma non è proprietario)
            allGalleries.add(new GalleryItem("G000000002", "Progetto Lavoro", "Condivisa"));
            allGalleries.add(new GalleryItem("G000000003", "Calcetto", "Condivisa"));

            photosByGallery.put("G000000001", new ArrayList<>());
            photosByGallery.put("G000000002", new ArrayList<>());
            photosByGallery.put("G000000003", new ArrayList<>());
        }

        List<GalleryItem> listGalleriesForUser(String userId) {
            // Restituisce tutte le gallerie visibili all'utente (sia Personali che Condivise)
            // In un DB reale qui faresti una JOIN tra GALLERIA e PARTECIPA
            return new ArrayList<>(allGalleries);
        }

        List<PhotoItem> listPhotos(String galleryId) {
            return new ArrayList<>(photosByGallery.getOrDefault(galleryId, List.of()));
        }

        void creaFoto(String galleryId, DatiFotoInput dati) {
            String newId = "P" + (10000000 + new Random().nextInt(9000000));
            String titolo = "Foto " + dati.dispositivo;

            photosByGallery.computeIfAbsent(galleryId, k -> new ArrayList<>())
                    .add(new PhotoItem(newId, titolo));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InMemoryStore store = new InMemoryStore();
            JFrame frame = new JFrame("GeoPic - Sistema Gestione Gallerie");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);

            CardLayout cards = new CardLayout();
            JPanel root = new JPanel(cards);

            AtomicReference<UserHomePanel> userRef = new AtomicReference<>();
            AtomicReference<GalleryPanel> galleryRef = new AtomicReference<>();

            UploadFilePanel upload = new UploadFilePanel();
            PhotoPanel photoPanel = new PhotoPanel(() -> cards.show(root, GALLERY));

            GalleryPanel galleryPanel = new GalleryPanel(
                    () -> {
                        userRef.get().reload(store.listGalleriesForUser("u1"));
                        cards.show(root, USER);
                    },
                    galleryId -> galleryRef.get().reload(store.listPhotos(galleryId)),
                    (photoId, titolo) -> {
                        photoPanel.showPhoto(photoId, titolo);
                        cards.show(root, PHOTO);
                    }
            );
            galleryRef.set(galleryPanel);

            UserHomePanel user = new UserHomePanel(
                    () -> userRef.get().reload(store.listGalleriesForUser("u1")),
                    () -> cards.show(root, START),
                    (galleryId, nome, tipo) -> {
                        galleryPanel.setGalleryContext(galleryId, nome, tipo);
                        galleryPanel.reload(store.listPhotos(galleryId));
                        cards.show(root, GALLERY);
                    },
                    (galleryId, galleryName) -> {
                        upload.configure("Carica in: " + galleryName,
                                () -> cards.show(root, USER),
                                dati -> {
                                    store.creaFoto(galleryId, dati);
                                    galleryPanel.reload(store.listPhotos(galleryId));
                                    cards.show(root, GALLERY);
                                }
                        );
                        cards.show(root, UPLOAD);
                    },
                    (id, nome) -> {},
                    () -> {}
            );
            userRef.set(user);

            root.add(new StartPanel(() -> {}, () -> {
                user.reload(store.listGalleriesForUser("u1"));
                cards.show(root, USER);
            }), START);
            root.add(user, USER);
            root.add(upload, UPLOAD);
            root.add(galleryPanel, GALLERY);
            root.add(photoPanel, PHOTO);

            frame.setContentPane(root);
            frame.setVisible(true);
        });
    }
}