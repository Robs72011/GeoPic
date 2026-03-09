package GUI;

import java.io.File;
import java.util.List;

public class Slideshow {
    private final String slideshowid;
    private final String slideshowname;
    private final List<File> imageFiles;
    private final String slideshowdesc;


    public Slideshow(String id, String nome, List<File> imageFiles, String descrizione) {
        this.slideshowid = id;
        this.slideshowname = nome;
        this.imageFiles = imageFiles;
        this.slideshowdesc = descrizione;
    }

    public String getid() {
        return slideshowid;
    }

    public List<File> getImageFiles() {
        return imageFiles;
    }

    public String getDescrizione() {
        return slideshowdesc;
    }

    public String getName() {return slideshowname;}

}