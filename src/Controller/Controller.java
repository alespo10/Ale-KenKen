package Controller;

import Command.HistoryCommandHandler;
import Command.InserisciNumeroCommand;
import Model.Casella;
import Model.Griglia;
import Model.Soluzione;
import View.Grafica;
import javax.swing.*;
import java.util.List;


public class Controller {
    private final Grafica grafica;
    private Griglia griglia;
    private final HistoryCommandHandler historyHandler;
    private List<int[][]> soluzioni;

    public Controller() {
        this.historyHandler = new HistoryCommandHandler();
        this.grafica = new Grafica();
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
                soluzioni = solver.getSoluzioni();
                System.out.println(soluzioni);

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
                // Crea il comando PRIMA di applicare il nuovo valore
                InserisciNumeroCommand cmd = new InserisciNumeroCommand(casella, nuovoValore);

                // Esegui il comando tramite il gestore della cronologia
                historyHandler.handle(cmd);

                // Aggiorna la grafica
                grafica.aggiornaGriglia(griglia.getGriglia());
                aggiornaStatoPulsanti();
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
            for (int[][] soluzione : soluzioni) {
                for (int[] row : soluzione) {
                    System.out.println(java.util.Arrays.toString(row));
                }
                System.out.println("----");
            }

            if (soluzioni.isEmpty()) {
                JOptionPane.showMessageDialog(grafica, "Errore: impossibile verificare la soluzione.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean corretto = false;
            for (int[][] soluzione : soluzioni) {
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


}



