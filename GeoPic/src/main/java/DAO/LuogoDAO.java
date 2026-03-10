package DAO;

import java.util.ArrayList;

public interface LuogoDAO {
    void insertLuogo(String coordinate, String toponimo);

    void deleteLuogo(String coordinate);

    void getAllLuoghi(ArrayList<String> coordinate, ArrayList<String> toponimo);
}
