package Controller;


import Model.Griglia;
import View.Grafica;


public class Controller {
    private final Grafica grafica;
    private Griglia griglia;

    public Controller() {
        this.grafica = new Grafica();
        grafica.setVisible(true);

        // Listener per il nuovo gioco
        grafica.setNuovoGiocoListener(e -> grafica.mostraSchermata("nuovoGioco"));

        // Listener per avviare il gioco
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

    }


}
