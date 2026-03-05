package DAO;

import java.util.ArrayList;

public interface UtenteDAO {
    void insertUtente(int idUtente, String username, String password, boolean isAdmin, boolean isSoggetto);

    void deleteUtente(int idUtente);

    void getAllUtenti(ArrayList<Integer> idUtente, ArrayList<String> username, ArrayList<String> password,
                      ArrayList<Boolean> isAdmin, ArrayList<Boolean> isSoggetto);

    void getLoggedInUtente(int idUtente, String username, String password,
                           Boolean isAdmin, Boolean isSoggetto);
}
