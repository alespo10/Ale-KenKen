package View;

import Model.Blocco;
import Model.Casella;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grafica extends JFrame {

    //Bottoni iniziali
    private JButton nuovoGiocoButton;
    private JButton caricaButton;

    //Bottoni interfaccia gioco
    private JButton provaSoluzione;
    private JButton salvaButton;
    private JButton undoButton;
    private JButton redoButton;

    //Input + avvio
    private JTextField numeroSoluzioniField;
    private JTextField dimensioneMatriceField;
    private JButton avviaButton;

    //Visualizzazione griglia
    private JPanel grigliaPanel;
    private JLabel infoBloccoLabel;
    private boolean inAggiornamento = false;

    private JTextField[][] caselle;
    private final Map<Blocco, Color> coloriBlocchi = new HashMap<>();

    public Grafica() {
        setTitle("KenKen");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new CardLayout());

        JPanel schermataIniziale = new JPanel(new GridLayout(3, 1, 10, 10));
        nuovoGiocoButton = new JButton("Nuovo Gioco");
        caricaButton = new JButton("Carica Gioco");
        schermataIniziale.add(nuovoGiocoButton);
        schermataIniziale.add(caricaButton);



        JPanel schermataNuovoGioco = new JPanel(new GridLayout(4, 2, 10, 10));
        JLabel numeroSoluzioniLabel = new JLabel("Numero di soluzioni:");
        numeroSoluzioniField = new JTextField();
        JLabel dimensioneMatriceLabel = new JLabel("Dimensione della matrice:");
        dimensioneMatriceField = new JTextField();
        avviaButton = new JButton("Avvia");

        schermataNuovoGioco.add(numeroSoluzioniLabel);
        schermataNuovoGioco.add(numeroSoluzioniField);
        schermataNuovoGioco.add(dimensioneMatriceLabel);
        schermataNuovoGioco.add(dimensioneMatriceField);
        schermataNuovoGioco.add(new JLabel());
        schermataNuovoGioco.add(avviaButton);


        JPanel schermataGioco = new JPanel(new BorderLayout());
        infoBloccoLabel = new JLabel("Blocco: Operazione + Risultato", JLabel.CENTER);
        grigliaPanel = new JPanel();
        schermataGioco.add(infoBloccoLabel, BorderLayout.NORTH);
        schermataGioco.add(grigliaPanel, BorderLayout.CENTER);

        // Aggingo la parte sotto della griglia

        salvaButton = new JButton("Salva");
        undoButton = new JButton("Undo");
        redoButton = new JButton("Redo");
        provaSoluzione = new JButton("Verifica soluzione");

        JPanel pulsantiPanel = new JPanel(new FlowLayout());
        pulsantiPanel.add(undoButton);
        pulsantiPanel.add(redoButton);
        pulsantiPanel.add(provaSoluzione);
        pulsantiPanel.add(salvaButton);


        schermataGioco.add(pulsantiPanel, BorderLayout.SOUTH);


        add(schermataIniziale, "iniziale");
        add(schermataNuovoGioco, "nuovoGioco");
        add(schermataGioco, "gioco");

        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "iniziale");
    }

    public void mostraGriglia(Casella[][] griglia, List<Blocco> blocchi) {
        grigliaPanel.removeAll();
        int dimensione = griglia.length;

        grigliaPanel.setLayout(new GridLayout(dimensione, dimensione));
        caselle = new JTextField[dimensione][dimensione];
        assegnaColoriBlocchi(blocchi);

        for (int x = 0; x < dimensione; x++) {
            for (int y = 0; y < dimensione; y++) {
                Casella casella = griglia[x][y];
                Blocco blocco = casella.getBlocco();

                JTextField cella = new JTextField();
                cella.setHorizontalAlignment(JTextField.CENTER);
                cella.setOpaque(true);
                cella.setBackground(coloriBlocchi.get(blocco));
                cella.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                if (casella.getValore() != 0) {
                    cella.setText(String.valueOf(casella.getValore()));
                    cella.setEditable(false);
                }

                int finalX = x;
                int finalY = y;
                cella.getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        aggiornaValore();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        aggiornaValore();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        // Non necessario per JTextField
                    }

                    private void aggiornaValore() {
                        if (inAggiornamento) {
                            return; // Evita notifiche durante la sincronizzazione
                        }

                        try {
                            int nuovoValore = Integer.parseInt(cella.getText());

                            // Verifica se il valore è valido
                            if (nuovoValore < 1 || nuovoValore > caselle.length) {
                                JOptionPane.showMessageDialog(null,
                                        "Valore non valido! Inserisci un numero tra 1 e " + caselle.length + ".",
                                        "Errore di inserimento",
                                        JOptionPane.ERROR_MESSAGE);

                                // Ripristina il valore corrente della casella nel campo di testo
                                cella.setText(casella.getValore() == 0 ? "" : String.valueOf(casella.getValore()));
                                return;
                            }

                            // Notifica il controller della modifica
                            if (cellaModificaListener != null ) {
                                cellaModificaListener.onCellaModifica(finalX, finalY, nuovoValore);
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null,
                                    "Inserisci un numero valido.",
                                    "Errore di inserimento",
                                    JOptionPane.ERROR_MESSAGE);
                            cella.setText(""); // Resetta il campo
                        }
                    }


                });

                cella.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (blocco != null) {
                            infoBloccoLabel.setText("Blocco: " + blocco.getTipoVincolo() +
                                    " " + blocco.getRisultatoVincolo());
                        } else {
                            infoBloccoLabel.setText("Blocco non assegnato.");
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        infoBloccoLabel.setText("Blocco: Operazione + Risultato");
                    }
                });

                caselle[x][y] = cella;
                grigliaPanel.add(cella);
            }
        }

        grigliaPanel.revalidate();
        grigliaPanel.repaint();
    }




    private void assegnaColoriBlocchi(List<Blocco> blocchi) {
        coloriBlocchi.clear(); // Ripulisce i colori precedenti
        for (Blocco blocco : blocchi) {
            coloriBlocchi.putIfAbsent(blocco, new Color((int) (Math.random() * 0xFFFFFF)));
        }
    }

    public void setNuovoGiocoListener(ActionListener listener) {
        nuovoGiocoButton.addActionListener(listener);
    }

    public void setCaricaGiocoListener(ActionListener listener) {
        caricaButton.addActionListener(listener);
    }

    public void setAvviaListener(ActionListener listener) {
        avviaButton.addActionListener(listener);
    }

    public void setVerificaListener(ActionListener listener) {
        provaSoluzione.addActionListener(listener);
    }

    public void mostraSchermata(String schermata) {
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), schermata);
    }

    public int getNumeroSoluzioni() {
        try {
            return Integer.parseInt(numeroSoluzioniField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Inserisci un numero valido per le soluzioni.", "Errore", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }

    public int getDimensioneMatrice() {
        try {
            return Integer.parseInt(dimensioneMatriceField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Inserisci un numero valido per la dimensione della matrice.", "Errore", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }

    // Metodi di supporto Command

    public void aggiornaGriglia(Casella[][] griglia) {
        // Imposta il flag per evitare loop
        inAggiornamento = true;

        SwingUtilities.invokeLater(() -> {
            for (int x = 0; x < griglia.length; x++) {
                for (int y = 0; y < griglia[x].length; y++) {
                    Casella casella = griglia[x][y];
                    JTextField cella = caselle[x][y];
                    String nuovoValore = casella.getValore() == 0 ? "" : String.valueOf(casella.getValore());

                    if (!cella.getText().equals(nuovoValore)) {
                        cella.setText(nuovoValore);
                    }
                }
            }

            // Disattiva il flag dopo l'aggiornamento
            inAggiornamento = false;
        });
    }

    //Listner per Undo/Redo
    public void setUndoListener(ActionListener listener) {
        undoButton.addActionListener(listener);
    }
    public void setRedoListener(ActionListener listener) {
        redoButton.addActionListener(listener);
    }
    //Listner per mostrare/nascondere Undo/Redo
    public void abilitaUndo(boolean abilitato) {
        undoButton.setEnabled(abilitato);
    }
    public void abilitaRedo(boolean abilitato) {
        redoButton.setEnabled(abilitato);
    }
    //Listner per caricare e salvare la griglia
    public void setSalvaListener(ActionListener listener) {salvaButton.addActionListener(listener);}
    public void setCaricaListener(ActionListener listener) {caricaButton.addActionListener(listener);}


    private CellaModificaListener cellaModificaListener;
    public void setCellaModificaListener(CellaModificaListener listener) {
        this.cellaModificaListener = listener;
    }

    @FunctionalInterface
    public interface CellaModificaListener {
        void onCellaModifica(int x, int y, int nuovoValore);
    }



}