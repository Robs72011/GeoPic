package GUI;

import Controller.Controller;

public class LoginPanelHandler {
    private final Controller controller;
    private String utente;
    private String password;
    private int risultato_login = -1;


    public LoginPanelHandler(Controller controller){
        this.controller = controller;
    }

    public void setlogincredits(String utente, String password){
        this.utente = utente;
        this.password = password;
    }

    /**
     * Controlla il login tramite il Controller (che va al DB).
     * @return true se l'utente è autenticato (utente normale o admin)
     */

    public int auth(){
        risultato_login = controller.authentication(utente, password);
        return risultato_login;
    }
}
