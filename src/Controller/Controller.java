package Controller;

import Command.HistoryCommandHandler;
import Command.InserisciNumeroCommand;
import Memento.Memento;
import Model.Casella;
import Model.Griglia;
import Model.Soluzione;
import View.Grafica;
import javax.swing.*;
import java.io.*;
import Memento.GameMemento;


public class Controller {
    private final Grafica grafica;
    private Griglia griglia;
    private final HistoryCommandHandler historyHandler;
    private int indiceSoluzioneCorrente; // Indice della soluzione attualmente mostrata


    public Controller() {
        this.historyHandler = new HistoryCommandHandler();
        this.grafica = new Grafica();

        grafica.setSalvaListener(e -> salvaPartita());
        grafica.setCaricaListener(e -> caricaPartita());
        grafica.setVisible(true);

        grafica.setNuovoGiocoListener(e -> grafica.mostraSchermata("nuovoGioco"));

        grafica.setAvviaListener(e -> {
            int numeroSoluzioni = grafica.getNumeroSoluzioni();
            int dimensioneMatrice = grafica.getDimensioneMatrice();

            if (numeroSoluzioni > 0 && dimensioneMatrice > 2) {
                griglia = new Griglia(dimensioneMatrice);
                griglia.popolaGriglia();

                Soluzione solver = new Soluzione(griglia, grafica.getNumeroSoluzioni());
                solver.risolvi();

                griglia.setSoluzioni(solver.getSoluzioni());   //assegno le soluzioni direttamente alla griglia

                grafica.mostraSchermata("gioco");
                grafica.mostraGriglia(griglia.getGriglia(), griglia.getBlocchi());
            }
        });

        // Listener per il pulsante Undo
        historyHandler.setOnStateChanged(v -> {
            grafica.aggiornaGriglia(griglia.getGriglia());
            aggiornaStatoPulsanti();
        });
        grafica.setUndoListener(e -> historyHandler.undo());
        grafica.setRedoListener(e -> historyHandler.redo());

        grafica.setCellaModificaListener((x, y, nuovoValore) -> {
            Casella casella = griglia.getCella(x, y);

            if(!griglia.puoiInserire(x, y, nuovoValore)) {
                JOptionPane.showMessageDialog(grafica, "Errore: Numero gia presente in riga o colonna.", "Errore", JOptionPane.ERROR_MESSAGE);
            }else {
                InserisciNumeroCommand cmd = new InserisciNumeroCommand(casella, nuovoValore);
                historyHandler.handle(cmd);
                grafica.aggiornaGriglia(griglia.getGriglia());
                aggiornaStatoPulsanti();
            }

        });

        // Listener per il bottone "Visualizza Soluzioni"
        grafica.setVisualizzaSoluzioniListener(e -> {
            if (!griglia.getSoluzioni().isEmpty()) {
                indiceSoluzioneCorrente = 0; // Mostra la prima soluzione
                mostraSoluzione(indiceSoluzioneCorrente);
                grafica.mostraNavigazioneSoluzioni(true); // Mostra i bottoni di navigazione
            } else {
                JOptionPane.showMessageDialog(grafica, "Nessuna soluzione disponibile.");
            }
        });
        // Listener per il bottone "Avanti"
        grafica.setAvantiListener(e -> {
            if (indiceSoluzioneCorrente < griglia.getSoluzioni().size() - 1) {
                indiceSoluzioneCorrente++;
                mostraSoluzione(indiceSoluzioneCorrente);
            }
        });
        // Listener per il bottone "Indietro"
        grafica.setIndietroListener(e -> {
            if (indiceSoluzioneCorrente > 0) {
                indiceSoluzioneCorrente--;
                mostraSoluzione(indiceSoluzioneCorrente);
            }
        });

        //Listner di verifica della soluzione finale
        grafica.setVerificaListener(e -> {
            int[][] valoriInseriti = griglia.getValoriAttuali();
            System.out.println("Valori Inseriti:");
            for (int[] row : valoriInseriti) {
                System.out.println(java.util.Arrays.toString(row));
            }

            System.out.println("Soluzioni Generate:");
            for (int[][] soluzione : griglia.getSoluzioni()) {
                for (int[] row : soluzione) {
                    System.out.println(java.util.Arrays.toString(row));
                }
                System.out.println("----");
            }

            if (griglia.getSoluzioni().isEmpty()) {
                JOptionPane.showMessageDialog(grafica, "Errore: impossibile verificare la soluzione.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean corretto = false;
            for (int[][] soluzione : griglia.getSoluzioni()) {
                if (confrontaSoluzione(valoriInseriti, soluzione)) {
                    corretto = true;
                    break;
                }
            }
            if (corretto) {
                JOptionPane.showMessageDialog(grafica, "Complimenti, hai vinto!", "Vittoria", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(grafica, "La soluzione non è corretta, riprova.", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });
        aggiornaStatoPulsanti();
    }



    private boolean confrontaSoluzione(int[][] valoriUtente, int[][] soluzione) {
        int dimensione = valoriUtente.length;
        for (int x = 0; x < dimensione; x++) {
            for (int y = 0; y < dimensione; y++) {
                if (valoriUtente[x][y] != soluzione[x][y]) {
                    return false;
                }
            }
        }
        return true;
    }

    private void aggiornaStatoPulsanti() {
        grafica.abilitaUndo(!historyHandler.isUndoVuoto());
        grafica.abilitaRedo(!historyHandler.isRedoVuoto());
    }

    private void salvaPartita() {
        if (griglia == null) {
            JOptionPane.showMessageDialog(grafica, "Nessuna partita in corso da salvare.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(grafica) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
                oos.writeObject(griglia.salva()); // Salva il memento con le soluzioni
                JOptionPane.showMessageDialog(grafica, "Partita salvata con successo.", "Salvataggio", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(grafica, "Errore durante il salvataggio: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void caricaPartita() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(grafica) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
                Memento memento = (Memento) ois.readObject();
                if (griglia == null) {
                    int dimensione = ((GameMemento) memento).getValoriGriglia().length;
                    griglia = new Griglia(dimensione);
                }
                griglia.ripristina(memento);

                // Verifica che le soluzioni siano state ripristinate
                if (griglia.getSoluzioni() == null || griglia.getSoluzioni().isEmpty()) {
                    JOptionPane.showMessageDialog(grafica, "Attenzione: nessuna soluzione trovata nel salvataggio.", "Avviso", JOptionPane.WARNING_MESSAGE);
                }

                // Aggiorna la vista con lo stato ripristinato
                grafica.mostraGriglia(griglia.getGriglia(), griglia.getBlocchi());
                grafica.mostraSchermata("gioco"); // Mostra la schermata di gioco
                aggiornaStatoPulsanti();
                JOptionPane.showMessageDialog(grafica, "Partita caricata con successo.", "Caricamento", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(grafica, "Errore durante il caricamento: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostraSoluzione(int indice) {
        int[][] soluzione = griglia.getSoluzioni().get(indice);
        for (int x = 0; x < griglia.getDimensione(); x++) {
            for (int y = 0; y < griglia.getDimensione(); y++) {
                griglia.getCella(x, y).setValore(soluzione[x][y]);
            }
        }
        grafica.aggiornaGriglia(griglia.getGriglia());
        grafica.aggiornaSoluzioneCorrente(indice, griglia.getSoluzioni().size());
    }

}



