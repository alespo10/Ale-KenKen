package Model;

import java.util.*;
import java.util.List;

public class Griglia{
    private final int dimensione;
    private final Casella[][] griglia;
    private final List<Blocco> blocchi;
    private static final Random RANDOM = new Random();  // lo uso per generarmi la creazione random

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
        if (x == dimensione) {
            return true;
        }
        // Calcola la prossima cella (spostandosi alla riga successiva dopo l'ultima colonna)
        int prossimoX = (y == dimensione - 1) ? x + 1 : x;
        int prossimoY = (y == dimensione - 1) ? 0 : y + 1;

        // Crea una lista di numeri da 1 a dimensione e randomizza
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
                griglia[x][y].setValore(0); // Utilizzo il Backtracking
            }
        }

        return false;
    }

    // Controlla se un numero può essere inserito nella cella
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


    //Metodo con dei limiti di generazione (si può migliorare)
    private List<Casella> generaBloccoAdiacente(int startX, int startY, boolean[][] visitato, Blocco.TipoVincolo tipoVincolo) {
        List<Casella> caselle = new ArrayList<>();
        caselle.add(griglia[startX][startY]); // Aggiungi la cella iniziale
        visitato[startX][startY] = true;

        // Scegli la dimensione del blocco (1-3 celle)
        int dimensioneBlocco = RANDOM.nextInt(3) + 1;  // 1, 2 o 3 celle

        // Proviamo ad aggiungere altre celle al blocco
        while (caselle.size() < dimensioneBlocco) {
            // Trova una cella valida adiacente
            List<Casella> celleAdiacenti = new ArrayList<>();

            for (Casella casella : caselle) {
                int x = casella.getX();
                int y = casella.getY();

                // Verifica le celle adiacenti (sopra, sotto, sinistra, destra)
                if (validaCella(x - 1, y, visitato)) celleAdiacenti.add(griglia[x - 1][y]);  // Cella sopra
                if (validaCella(x + 1, y, visitato)) celleAdiacenti.add(griglia[x + 1][y]);  // Cella sotto
                if (validaCella(x, y - 1, visitato)) celleAdiacenti.add(griglia[x][y - 1]);  // Cella a sinistra
                if (validaCella(x, y + 1, visitato)) celleAdiacenti.add(griglia[x][y + 1]);  // Cella a destra
            }

            // Se ci sono celle adiacenti valide, scegli una casualmente
            if (!celleAdiacenti.isEmpty()) {
                Casella nuovaCasella = celleAdiacenti.get(RANDOM.nextInt(celleAdiacenti.size()));
                caselle.add(nuovaCasella);
                visitato[nuovaCasella.getX()][nuovaCasella.getY()] = true; // Segna come visitata
            } else {
                break; // Se non ci sono celle adiacenti valide, fermiamo l'espansione
            }
        }

        // Valida il risultato del blocco in base all'operazione
        if (!validaOperazione(tipoVincolo, caselle)) {
            for (Casella c : caselle) {
                visitato[c.getX()][c.getY()] = false; // Reset delle celle visitate se invalide
            }
            return new ArrayList<>(); // Ritorna blocco vuoto se l'operazione non è valida
        }

        return caselle; // Ritorna il blocco valido
    }

    private boolean validaOperazione(Blocco.TipoVincolo tipoVincolo, List<Casella> caselle) {
        List<Integer> valori = new ArrayList<>();
        for (Casella casella : caselle) {
            valori.add(casella.getValore());
        }

        switch (tipoVincolo) {
            case SOMMA:
                return true; // La somma è sempre valida
            case PRODOTTO:
                return true; // Il prodotto è sempre valido
            case DIFFERENZA:
                if (valori.size() != 2) return false; // La differenza richiede esattamente 2 valori
                return Math.abs(valori.get(0) - valori.get(1)) > 0;
            case DIVISIONE:
                if (valori.size() != 2) return false; // La divisione richiede esattamente 2 valori
                int max = Math.max(valori.get(0), valori.get(1));
                int min = Math.min(valori.get(0), valori.get(1));
                return (max % min == 0); // Verifica che la divisione sia esatta
            default:
                return false; // Operazione non supportata
        }
    }

    private boolean validaCella(int x, int y, boolean[][] visitato) {
        return x >= 0 && x < dimensione && y >= 0 && y < dimensione && !visitato[x][y];
    }


    public void aggiungiBlocco(Blocco blocco) {
        blocchi.add(blocco);
    }


    private Blocco creaBlocco(Blocco.TipoVincolo tipoVincolo, List<Casella> caselle) {
        int risultatoVincolo = calcolaRisultatoVincolo(tipoVincolo, caselle);
        Blocco blocco = new Blocco(tipoVincolo, risultatoVincolo);
        for (Casella casella : caselle) {
            blocco.aggiungiCasella(casella);
            casella.setBlocco(blocco);
        }
        return blocco;
    }

    private int calcolaRisultatoVincolo(Blocco.TipoVincolo tipoVincolo, List<Casella> caselle) {
        List<Integer> valori = new ArrayList<>();
        for (Casella casella : caselle) {
            valori.add(casella.getValore());
        }
        switch (tipoVincolo) {
            case SOMMA:
                return valori.stream().mapToInt(Integer::intValue).sum();
            case PRODOTTO:
                return valori.stream().reduce(1, (a, b) -> a * b);
            case DIFFERENZA:
                return Math.abs(valori.get(0) - valori.get(1));
            case DIVISIONE:
                return Math.max(valori.get(0), valori.get(1)) / Math.min(valori.get(0), valori.get(1));
            default:
                throw new IllegalStateException("Tipo di vincolo non supportato");
        }
    }

    public int getDimensione(){return dimensione;}


    private Blocco.TipoVincolo scegliVincoloCasuale() {
        Blocco.TipoVincolo[] vincoli = Blocco.TipoVincolo.values();
        return vincoli[RANDOM.nextInt(vincoli.length)];
    }

    private void generaGrigliaVuota() {
        for (int x = 0; x < dimensione; x++) {
            for (int y = 0; y < dimensione; y++) {
                griglia[x][y].setValore(0); // Tutte le celle iniziano vuote
            }
        }
    }

    public Casella getCella(int x,int y){ return griglia[x][y];}

    public List<Blocco> getBlocchi() {
        return blocchi;
    }

    public Casella[][] getGriglia()
    {   return griglia;
    }

}



