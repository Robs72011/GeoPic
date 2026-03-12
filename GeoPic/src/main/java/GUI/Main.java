package GUI;

import GUI.dialog.LoginDialog;
import GUI.frame.FinestraAdmin;
import GUI.frame.FinestraUtente;

import Controller.Controller;


import javax.swing.*;

/**
 * Classe di avvio dell'applicazione.
 * Si occupa di inizializzare il {@link Controller}, caricare i dati dal database
 * e gestire il flusso di autenticazione iniziale tramite {@link LoginDialog}.
 */
public class Main {

    /**
     * Metodo principale che avvia il thread di interfaccia grafica (EDT).
     * Gestisce la logica di inizializzazione e la transizione dalla schermata
     * di login alla visualizzazione principale dell'applicazione.
     * @param args Argomenti passati da riga di comando (non utilizzati).
     */
    public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                // Inizializza il Controller e carica il data graph in memoria
                Controller controller = new Controller();//crea il controller
                controller.loadInMemory();

                // Avvia il pannello di login
                LoginDialog dialog = new LoginDialog(controller);
                int authenticate = dialog.mostraDialogo();

                // Gestisce la navigazione post-autenticazione
                if(authenticate == 1) {
                    System.out.println("Utente: " + dialog.getUtente());
                    System.out.println("Password: " + dialog.getPassword());
                    // Avvia l'interfaccia principale per l'utente standard
                    new FinestraUtente(controller);
                }
                else if(authenticate == 2){
                    // Avvia l'interfaccia di amministrazione
                    new FinestraAdmin(controller);
                }
        });
    }
}
