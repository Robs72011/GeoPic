package Model.DAO;

public interface MostraDAO {
    void addSoggettoAFoto(String nomeSoggetto, String IDFoto);

    void removeSoggettoDaFoto(String nomeSoggetto,  String IDFoto);
}
