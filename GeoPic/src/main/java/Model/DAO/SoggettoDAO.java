package Model.DAO;

import Model.Soggetto;

public interface SoggettoDAO {
    void addSoggetto(String nomeSoggetto, String categoria);

    void removeSoggetto(String nomeSoggetto);

    void addUtenteSoggetto(String nomeUtente, String categoria, String idUtente);

    void removeUtenteSoggetto(String nomeUtente);
}
