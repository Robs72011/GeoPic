package Model;

import java.util.ArrayList;

public class GalleriaPrivata extends Galleria{

    //Rappresenta la relazione "Salvato in" tra GalleriaPersonale e Video
    private ArrayList<Video> videos = null;

    public GalleriaPrivata(Integer idGalleria, String nomeGalleria, Utente proprietario,
                           ArrayList<Fotografia> fotoContenute, ArrayList<Video> videos){
        super(idGalleria, nomeGalleria, proprietario, fotoContenute);

        if(videos !=  null){
            this.videos = videos;
        }else {
            this.videos = new ArrayList<>();
        }
    }

    public ArrayList<Video> getVideos(){
        return this.videos;
    }

    public boolean addVideo(Video video){
        boolean aggiunta = false;

        if(!(this.videos.contains(video))){
            this.videos.add(video);

            aggiunta = true;
        }

        return aggiunta;
    }

    public boolean removeVideo(Video video){
        boolean rimozione = false;

        if(this.videos.contains(video)){
            this.videos.remove(video);

            rimozione = true;
        }

        return rimozione;
    }

}