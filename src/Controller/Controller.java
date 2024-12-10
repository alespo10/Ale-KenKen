package Controller;


import Model.Blocco;
import Model.Casella;
import Model.Griglia;
import Model.Soluzione;
import View.Grafica;

import javax.swing.*;
import java.util.List;


public class Controller {
    private final Grafica grafica;
    private Griglia griglia;

    public Controller() {
        this.grafica = new Grafica();
        grafica.setVisible(true);

        grafica.setNuovoGiocoListener(e -> grafica.mostraSchermata("nuovoGioco"));

        grafica.setAvviaListener(e -> {
            int numeroSoluzioni = grafica.getNumeroSoluzioni();
            int dimensioneMatrice = grafica.getDimensioneMatrice();

            if (numeroSoluzioni > 0 && dimensioneMatrice > 2) {
                griglia = new Griglia(dimensioneMatrice);
                griglia.popolaGriglia();

                grafica.mostraSchermata("gioco");
                grafica.mostraGriglia(griglia.getGriglia(), griglia.getBlocchi());
            }
        });

        //Listner di verifica della soluzioen finale
        grafica.setVerificaListener(e -> {
            Griglia grigliaVuota = creaGrigliaVuota(griglia);
            System.out.println("Struttura della Griglia Vuota:");
            for (Blocco blocco : grigliaVuota.getBlocchi()) {
                System.out.println("Vincolo=" + blocco.getTipoVincolo() + ", Risultato=" + blocco.getRisultatoVincolo());
                for (Casella casella : blocco.getCaselle()) {
                    System.out.println("  Cella: (" + casella.getX() + ", " + casella.getY() + "), Valore=" + casella.getValore());
                }
            }
            // Ottieni i valori attuali inseriti dall'utente
            int[][] valoriInseriti = griglia.getValoriAttuali();
            System.out.println("Valori Inseriti:");
            for (int[] row : valoriInseriti) {
                System.out.println(java.util.Arrays.toString(row));
            }

            //Richiamo la classe Soluzione passando la griglia vuota su cui generare le soluzioni
            // Se passavo quella originale con i numeri inseriti causa errori
            Soluzione solver = new Soluzione(grigliaVuota, grafica.getNumeroSoluzioni());
            solver.risolvi();

            List<int[][]> soluzioni = solver.getSoluzioni();
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

        // aggiungere metodo che cambia gli stati dei vari pulsanti
    }

    private Griglia creaGrigliaVuota(Griglia griglia) {
        Griglia grigliaVuota = new Griglia(griglia.getDimensione());
        for (Blocco bloccoOriginale : griglia.getBlocchi()) {
            Blocco bloccoVuoto = new Blocco(
                    bloccoOriginale.getTipoVincolo(),
                    bloccoOriginale.getRisultatoVincolo()
            );
            // Itera sulle caselle del blocco originale
            for (Casella casellaOriginale : bloccoOriginale.getCaselle()) {
                Casella casellaVuota = grigliaVuota.getCella(
                        casellaOriginale.getX(),
                        casellaOriginale.getY()
                );
                casellaVuota.setBlocco(bloccoVuoto);
                bloccoVuoto.aggiungiCasella(casellaVuota);
                casellaVuota.setValore(0);
            }
            grigliaVuota.aggiungiBlocco(bloccoVuoto);
        }
        return grigliaVuota;
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

}



