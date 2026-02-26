package DAO;

import java.util.ArrayList;

public interface UtenteDAO {
    void insertUtente(String idUtente, String username, String password, boolean isAdmin, boolean isSoggetto);

    void deleteUtente(String idUtente);

    void getAllUtenti(ArrayList<String> idUtente, ArrayList<String> username, ArrayList<String> password,
                      ArrayList<Boolean> isAdmin, ArrayList<Boolean> isSoggetto);

    void getLoggedInUtente(String idUtente, String username, String password,
                           Boolean isAdmin, Boolean isSoggetto);
}
