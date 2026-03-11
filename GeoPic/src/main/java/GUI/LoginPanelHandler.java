package GUI;

import Controller.Controller;

/**
 * Gestore dell'autenticazione per la componente {@link LoginPanel}.
 * Questa classe funge da delegato per mantenere lo stato temporaneo delle credenziali
 * (utente e password) e coordinare la comunicazione tra l'interfaccia di login
 * e il {@link Controller} per la validazione sul database.
 */
public class LoginPanelHandler {
    private final Controller controller;
    private String utente;
    private String password;


    /**
     * Costruisce l'handler di autenticazione.
     * @param controller Il riferimento al {@link Controller} che contiene
     * la logica di accesso al database.
     */
    public LoginPanelHandler(Controller controller){
        this.controller = controller;
    }

    /**
     * Imposta le credenziali di login (temporanee) per il tentativo di autenticazione.
     * @param utente Il nome utente inserito.
     * @param password La password inserita.
     */
    public void setlogincredits(String utente, String password){
        this.utente = utente;
        this.password = password;
    }

    /**
     * Esegue il processo di autenticazione invocando il {@link Controller}.
     * @return 2 se l'utente è un amministratore, 1 se è un utente standard,
     * -1 se l'autenticazione fallisce.
     */
    public int auth(){
        return controller.authentication(utente, password);
    }
}
