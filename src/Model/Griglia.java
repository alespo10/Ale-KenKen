package Model;

import java.util.*;
import java.util.List;

public class Griglia{
    private final int dimensione;
    private final Casella[][] griglia;
    private final List<Blocco> blocchi;
    private static final Random RANDOM = new Random();

    // Costruttore
    public Griglia(int dimensione) {
        if (dimensione < 3) {
            throw new IllegalArgumentException("La dimensione della griglia deve essere >= 3");
        }
        this.dimensione = dimensione;
        this.griglia = new Casella[dimensione][dimensione];
        this.blocchi = new ArrayList<>();

        // Inizializza la griglia con oggetti Casella
        for (int x = 0; x < dimensione; x++) {
            for (int y = 0; y < dimensione; y++) {
                griglia[x][y] = new Casella(x, y);
            }
        }
    }

    // Metodo per popolare la griglia con blocchi e vincoli che richiamo nel Controller poi
    public void popolaGriglia() {
        generaGrigliaPiena();
        creaBlocchiAdiacenti(); // Step 2: Genera blocchi adiacenti con vincoli
        generaGrigliaVuota(); // Step 1: Inizializza la griglia vuota

    }

    public void generaGrigliaPiena() {
        riempiCella(0, 0);
    }


    private boolean riempiCella(int x, int y) {
        // Se abbiamo riempito tutte le righe, la griglia è completa
        if (x == dimensione) {
            return true;
        }
        // Calcola la prossima cella (spostandosi alla riga successiva dopo l'ultima colonna)
        int prossimoX = (y == dimensione - 1) ? x + 1 : x;
        int prossimoY = (y == dimensione - 1) ? 0 : y + 1;

        // Crea una lista di numeri da 1 a dimensione e mescola
        List<Integer> numeriDisponibili = new ArrayList<>();
        for (int numero = 1; numero <= dimensione; numero++) {
            numeriDisponibili.add(numero);
        }
        Collections.shuffle(numeriDisponibili);

        for (int numero : numeriDisponibili) {
            if (puoiInserire(x, y, numero)) {
                griglia[x][y].setValore(numero);
                if (riempiCella(prossimoX, prossimoY)) {
                    return true;
                }
                griglia[x][y].setValore(0); // Backtracking
            }
        }

        return false;
    }

    // Controlla se un numero può essere inserito nella cella (rispetta le regole di righe e colonne)
    private boolean puoiInserire(int x, int y, int numero) {
        // Controlla la riga
        for (int col = 0; col < dimensione; col++) {
            if (griglia[x][col].getValore() == numero) {
                return false;
            }
        }
        for (int row = 0; row < dimensione; row++) {
            if (griglia[row][y].getValore() == numero) {
                return false;
            }
        }
        return true;
    }


    // Crea blocchi partendo dalla griglia già popolata
    private void creaBlocchiAdiacenti() {
        boolean[][] visitato = new boolean[dimensione][dimensione]; // Per tenere traccia delle celle già assegnate

        for (int x = 0; x < dimensione; x++) {
            for (int y = 0; y < dimensione; y++) {
                if (!visitato[x][y]) {
                    // Scegli un tipo di vincolo casuale
                    Blocco.TipoVincolo tipoVincolo = scegliVincoloCasuale();
                    List<Casella> caselleBlocco = generaBloccoAdiacente(x, y, visitato, tipoVincolo);

                    // Se il blocco non è valido o è vuoto, crea un blocco di una sola cella
                    if (caselleBlocco.isEmpty()) {
                        caselleBlocco.add(griglia[x][y]);
                        tipoVincolo = Blocco.TipoVincolo.SOMMA; // Operazione SOMMA per un singolo elemento
                    }

                    Blocco blocco = creaBlocco(tipoVincolo, caselleBlocco);    // Crea e aggiungi il blocco
                    aggiungiBlocco(blocco);
                }
            }
        }
    }

    private void aggiungiBlocco(Blocco blocco) {
    }

    private Blocco creaBlocco(Blocco.TipoVincolo tipoVincolo, List<Casella> caselleBlocco) {
    }

    private List<Casella> generaBloccoAdiacente(int x, int y, boolean[][] visitato, Blocco.TipoVincolo tipoVincolo) {
    }

    private Blocco.TipoVincolo scegliVincoloCasuale() {
        return null;
    }

    // Step 1: Inizializza la griglia vuota (tutti i valori a 0)
    private void generaGrigliaVuota() {
        for (int x = 0; x < dimensione; x++) {
            for (int y = 0; y < dimensione; y++) {
                griglia[x][y].setValore(0); // Tutte le celle iniziano vuote
            }
        }
    }





}



