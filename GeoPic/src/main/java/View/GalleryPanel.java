package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

public class GalleryPanel extends JPanel {

    private final JLabel title = new JLabel();
    private final JLabel subtitle = new JLabel();
    private final DefaultTableModel photoModel;
    private final JTable photoTable;
    private final DefaultTableModel videoModel;
    private final JTable videoTable;
    private final JButton btnCreateVideo;
    private final JTabbedPane tabs;

    private String currentGalleryId;
    private AppMain.GalleryItem currentGallery;
    private List<AppMain.PhotoItem> currentPhotos = List.of();
    private List<AppMain.VideoItem> currentVideos = List.of();

    private final Runnable onBackToUser;
    private final Consumer<String> onRefresh;
    private final Consumer<AppMain.GalleryItem> onAddPhoto;
    private final Consumer<AppMain.GalleryItem> onCreateVideo;
    private final Consumer<AppMain.PhotoItem> onOpenPhoto;
    private final Consumer<AppMain.VideoItem> onOpenVideo;

    public GalleryPanel(Runnable onBackToUser,
                        Consumer<String> onRefresh,
                        Consumer<AppMain.GalleryItem> onAddPhoto,
                        Consumer<AppMain.GalleryItem> onCreateVideo,
                        Consumer<AppMain.PhotoItem> onOpenPhoto,
                        Consumer<AppMain.VideoItem> onOpenVideo) {
        super(new BorderLayout(10, 10));
        this.onBackToUser = onBackToUser;
        this.onRefresh = onRefresh;
        this.onAddPhoto = onAddPhoto;
        this.onCreateVideo = onCreateVideo;
        this.onOpenPhoto = onOpenPhoto;
        this.onOpenVideo = onOpenVideo;

        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JButton back = new JButton("Indietro");
        JButton refresh = new JButton("Aggiorna");
        JButton addPhoto = new JButton("Aggiungi Foto");
        btnCreateVideo = new JButton("Crea Video");

        JPanel titleBox = new JPanel(new GridLayout(0, 1));
        titleBox.add(title);
        titleBox.add(subtitle);

        JPanel top = new JPanel(new BorderLayout());
        top.add(titleBox, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(addPhoto);
        actions.add(btnCreateVideo);
        actions.add(refresh);
        actions.add(back);
        top.add(actions, BorderLayout.EAST);

        photoModel = new DefaultTableModel(new Object[]{"idx", "ID Foto", "Data Scatto", "Coordinate", "Luogo", "Dispositivo"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        photoTable = new JTable(photoModel);
        hideIndex(photoTable);

        videoModel = new DefaultTableModel(new Object[]{"idx", "ID Video", "Nome", "#Foto"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        videoTable = new JTable(videoModel);
        hideIndex(videoTable);

        tabs = new JTabbedPane();
        tabs.addTab("Foto", new JScrollPane(photoTable));
        tabs.addTab("Video (solo galleria personale)", new JScrollPane(videoTable));

        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        back.addActionListener(e -> onBackToUser.run());
        refresh.addActionListener(e -> { if (currentGalleryId != null) onRefresh.accept(currentGalleryId); });
        addPhoto.addActionListener(e -> { if (currentGallery != null) onAddPhoto.accept(currentGallery); });
        btnCreateVideo.addActionListener(e -> { if (currentGallery != null) onCreateVideo.accept(currentGallery); });

        photoTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && photoTable.getSelectedRow() != -1) {
                    int idx = (int) photoModel.getValueAt(photoTable.getSelectedRow(), 0);
                    if (idx >= 0 && idx < currentPhotos.size()) onOpenPhoto.accept(currentPhotos.get(idx));
                }
            }
        });
        videoTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && videoTable.getSelectedRow() != -1) {
                    int idx = (int) videoModel.getValueAt(videoTable.getSelectedRow(), 0);
                    if (idx >= 0 && idx < currentVideos.size()) onOpenVideo.accept(currentVideos.get(idx));
                }
            }
        });
    }

    private void hideIndex(JTable table) {
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
    }

    public void setGalleryContext(AppMain.GalleryItem gallery) {
        this.currentGallery = gallery;
        this.currentGalleryId = gallery.getId();
        title.setText("Galleria: " + gallery.getNome() + " (" + gallery.getTipo() + ")");
        subtitle.setText("Partecipanti: " + String.join(", ", gallery.getPartecipanti()));

        boolean personale = gallery.isPersonale();
        btnCreateVideo.setVisible(personale);
        tabs.setEnabledAt(1, personale);
        if (!personale && tabs.getSelectedIndex() == 1) tabs.setSelectedIndex(0);
    }

    public void reload(List<AppMain.PhotoItem> photos, List<AppMain.VideoItem> videos) {
        currentPhotos = photos != null ? photos : List.of();
        currentVideos = videos != null ? videos : List.of();

        photoModel.setRowCount(0);
        for (int i = 0; i < currentPhotos.size(); i++) {
            AppMain.PhotoItem p = currentPhotos.get(i);
            photoModel.addRow(new Object[]{i, p.getId(), p.getDataScatto(), p.getCoordinate(), p.getLuogo(), p.getDispositivo()});
        }

        videoModel.setRowCount(0);
        for (int i = 0; i < currentVideos.size(); i++) {
            AppMain.VideoItem v = currentVideos.get(i);
            videoModel.addRow(new Object[]{i, v.getId(), v.getNome(), v.getFotoIds().size()});
        }
    }
}
