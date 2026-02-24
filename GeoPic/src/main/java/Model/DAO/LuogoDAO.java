package Model.DAO;

public interface LuogoDAO {
    void insertLuogo(String coordinate, String toponimo);

    void deleteLuogo(String coordinate);
}
