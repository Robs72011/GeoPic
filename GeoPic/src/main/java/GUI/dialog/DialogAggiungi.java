package GUI.dialog;

import Controller.Controller;

import javax.swing.*;
import java.awt.*;

/**
 * Classe base astratta per tutte le dialog modali di inserimento dati del sistema.
 * Fornisce il layout standard (BorderLayout), il pannello bottoni Salva/Annulla
 * e i metodi helper per la visualizzazione di messaggi all'utente.
 * Le sottoclassi devono solo costruire il proprio form nel costruttore
 * usando {@link #creaMainPanel()} e {@link #creaPannelloBottoni(Runnable)}.
 */
public abstract class DialogAggiungi extends JDialog {

    protected final Controller controller;

    protected DialogAggiungi(Frame parentFrame, String titolo, Controller controller) {
        super(parentFrame, titolo, true);
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(parentFrame);
    }

    /**
     * Crea e restituisce il pannello principale già configurato
     * con BorderLayout(10,10) e margini interni standard.
     */
    protected JPanel creaMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        return mainPanel;
    }

    protected void configuraDimensioni(int width, int height, int minWidth, int minHeight, boolean resizable) {
        setSize(width, height);
        setMinimumSize(new Dimension(minWidth, minHeight));
        setResizable(resizable);
    }

    protected void montaContenutoConBottoniSalva(JPanel mainPanel, Runnable onSalva) {
        add(mainPanel, BorderLayout.CENTER);
        add(creaPannelloBottoni(onSalva), BorderLayout.SOUTH);
    }

    /**
     * Crea il pannello con i bottoni Salva e Annulla allineati a destra.
     * Il bottone Salva è impostato come default button (invocabile con Invio).
     *
     * @param onSalva il metodo da eseguire al click di Salva
     */
    protected JPanel creaPannelloBottoni(Runnable onSalva) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalva = new JButton("Salva");
        JButton btnAnnulla = new JButton("Annulla");

        btnAnnulla.addActionListener(_ -> dispose());
        btnSalva.addActionListener(_ -> onSalva.run());
        getRootPane().setDefaultButton(btnSalva);

        buttonPanel.add(btnSalva);
        buttonPanel.add(btnAnnulla);
        return buttonPanel;
    }

    protected void mostraMessaggio(String messaggio, String titolo, int tipoMessaggio) {
        JOptionPane.showMessageDialog(this, messaggio, titolo, tipoMessaggio);
    }

    protected void mostraErrore(String messaggio) {
        mostraMessaggio(messaggio, "Errore", JOptionPane.ERROR_MESSAGE);
    }

    protected void mostraSuccessoEChiudi(String messaggio) {
        mostraSuccessoEChiudi(messaggio, "Successo");
    }

    protected void mostraSuccessoEChiudi(String messaggio, String titolo) {
        mostraMessaggio(messaggio, titolo, JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    protected String trimToEmpty(String valore) {
        return valore == null ? "" : valore.trim();
    }

    protected boolean isBlank(String valore) {
        return trimToEmpty(valore).isEmpty();
    }
}
