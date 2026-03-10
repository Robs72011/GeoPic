package DAO;

import java.util.ArrayList;

/**
 * Interfaccia DAO per la gestione della relazione "Contiene" tra le entità {@link Model.Galleria}
 * e {@link Model.Fotografia}.
 * Gestisce l'associazione tra le gallerie e le foto in esse contenute all'interno del database.
 */
public interface ContieneDAO {

    /**
     * Inserisce un'associazione tra una galleria e una fotografia, aggiungendo la foto alla galleria.
     * @param IDGalleria L'identificativo univoco della galleria.
     * @param IDFoto L'identificativo univoco della fotografia da aggiungere.
     */
    void insertFotoAGalleria(int IDGalleria, int IDFoto);

    /**
     * Rimuove l'associazione tra una galleria e una fotografia, eliminando la foto dalla galleria.
     * @param IDGalleria L'identificativo univoco della galleria.
     * @param IDFoto L'identificativo univoco della fotografia da rimuovere.
     */
    void deleteFotoDaGalleria(int IDGalleria, int IDFoto);

    /**
     * Recupera tutte le associazioni foto-galleria presenti nel database,
     * popolando le liste passate come parametri.
     * @param idGalleria Lista in cui vengono memorizzati gli ID delle gallerie.
     * @param idFoto Lista in cui vengono memorizzati gli ID delle fotografie associate.
     */
    void getAllContenute(ArrayList<Integer> idGalleria, ArrayList<Integer>idFoto);
}
