package DAO;

import java.util.ArrayList;

/**
 * Interfaccia DAO per la gestione della relazione tra le entità {@link Model.Soggetto}
 * e {@link Model.Fotografia}.
 * Definisce le operazioni per tracciare quali soggetti appaiono in quali fotografie.
 */
public interface MostraDAO {

    /**
     * Associa un soggetto a una fotografia, indicando che il soggetto appare in essa.
     * @param nomeSoggetto Il nome identificativo del soggetto.
     * @param IDFoto L'identificativo della fotografia in cui appare il soggetto.
     */
    void insertSoggettoInFoto(String nomeSoggetto, int IDFoto);

    /**
     * Rimuove l'associazione tra un soggetto e una fotografia.
     * @param nomeSoggetto Il nome identificativo del soggetto.
     * @param IDFoto L'identificativo della fotografia da cui rimuovere l'associazione.
     */
    void deleteSoggettoDaFoto(String nomeSoggetto,  int IDFoto);

    /**
     * Recupera tutte le associazioni soggetto-fotografia presenti nel database,
     * popolando le liste fornite come parametri.
     * @param nomeSoggetto Lista in cui verranno inseriti i nomi dei soggetti.
     * @param idFoto Lista in cui verranno inseriti gli ID delle fotografie corrispondenti.
     */
    void getAllSoggettiMostrati(ArrayList<String> nomeSoggetto, ArrayList<Integer> idFoto);
}
