package Model.DAO;

public interface LuogoDAO {
    void addLuogo(String coordinate, String toponimo);

    void removeLuogo(String coordinate);
}
