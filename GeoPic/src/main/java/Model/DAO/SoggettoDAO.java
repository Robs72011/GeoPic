package Model.DAO;

import Model.Soggetto;

import java.util.ArrayList;

public interface SoggettoDAO {
    void insertSoggetto(String nomeSoggetto, String categoria);

    void deleteSoggetto(String nomeSoggetto);

    void insertUtenteAsSoggetto(String nomeUtente, String categoria, String idUtente);

    void deleteUtenteAsSoggetto(String nomeUtente);

    void getAllSoggetti(ArrayList<String> nomeSoggetto, ArrayList<String> categoria);
}
