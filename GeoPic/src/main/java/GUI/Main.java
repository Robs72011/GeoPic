package GUI;

import Controller.Controller;

import javax.swing.*;

/**
 * Classe di avvio dell'applicazione.
 * Si occupa di inizializzare il {@link Controller}, caricare i dati dal database
 * e gestire il flusso di autenticazione iniziale tramite {@link LoginPanel}.
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
                LoginPanel dialog = new LoginPanel();
                int authenticate = dialog.Login(controller);

                // Gestisce la navigazione post-autenticazione
                if(authenticate == 1) {
                    System.out.println("Utente: " + dialog.getUtente());
                    System.out.println("Password: " + dialog.getPassword());
                    // Avvia l'interfaccia principale per l'utente standard
                    new TabbedPaneFrame(controller);
                }
                else if(authenticate == 2){
                    //parte da implementare per la gui admin
                }
        });
    }
}
