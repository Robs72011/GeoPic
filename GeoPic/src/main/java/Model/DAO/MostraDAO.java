package Model.DAO;

public interface MostraDAO {
    void insertSoggettoInFoto(String nomeSoggetto, String IDFoto);

    void deleteSoggettoDaFoto(String nomeSoggetto,  String IDFoto);
}
