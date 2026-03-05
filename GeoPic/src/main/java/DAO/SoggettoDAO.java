package DAO;

import java.util.ArrayList;

public interface SoggettoDAO {
    void insertSoggetto(String nomeSoggetto, String categoria);

    void deleteSoggetto(String nomeSoggetto);

    void insertUtenteAsSoggetto(String nomeUtente, String categoria, int idUtente);

    void deleteUtenteAsSoggetto(String nomeUtente);

    void getAllSoggetti(ArrayList<String> nomeSoggetto, ArrayList<String> categoria);
}
