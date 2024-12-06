package Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class Soluzione extends SoluzioneAbstract<Casella, Integer> {
    private final Griglia griglia;
    private final List<int[][]> soluzioni;

    private final int dimensione;
    private int soluzioniTrovate;
    private final int maxSoluzioni;

    public Soluzione(Griglia griglia, int maxSoluzioni) {
        this.griglia = griglia;
        this.dimensione = griglia.getDimensione();
        this.soluzioni = new ArrayList<>();
        this.soluzioniTrovate = 0;
        this.maxSoluzioni = maxSoluzioni;
    }

    @Override
    protected boolean assegnabile(Casella casella, Integer valore) {
        int x = casella.getX();
        int y = casella.getY();

        // Controlla che il valore non sia già presente nella riga o nella colonna
        for (int i = 0; i < dimensione; i++) {
            if (griglia.getCella(x, i).getValore() == valore || griglia.getCella(i, y).getValore() == valore) {
                return false; // Valore già presente nella riga o colonna
            }
        }

        // Recupera il blocco della cella
        Blocco blocco = casella.getBlocco();
        if (blocco != null) {
            casella.setValore(valore); // Assegna temporaneamente il valore

            if (bloccoCompleto(blocco)) {
                // Verifica il vincolo solo se tutte le celle del blocco sono assegnate
                boolean vincoloSoddisfatto = blocco.verificaVincolo();
                casella.setValore(0); // Reset temporaneo
                return vincoloSoddisfatto;
            } else {
                // Se il blocco è parzialmente riempito, verifica che il valore non renda impossibile soddisfare il vincolo
                boolean vincoloPossibile = vincoloAncoraPossibile(blocco);
                casella.setValore(0); // Reset temporaneo
                return vincoloPossibile;
            }
        }

        return true; // Se la cella non appartiene a un blocco, il valore è sempre assegnabile
    }

    // Controlla se tutte le celle del blocco sono assegnate
    private boolean bloccoCompleto(Blocco blocco) {
        for (Casella c : blocco.getCaselle()) {
            if (c.isVuota()) {
                return false; // Almeno una cella è vuota
            }
        }
        return true; // Tutte le celle sono assegnate
    }

    // Controlla se il vincolo è ancora possibile con le celle parzialmente assegnate
    private boolean vincoloAncoraPossibile(Blocco blocco) {
        List<Integer> valori = new ArrayList<>();
        int celleVuote = 0;

        for (Casella c : blocco.getCaselle()) {
            if (c.isVuota()) {
                celleVuote++;
            } else {
                valori.add(c.getValore());
            }
        }

        // Ottieni i parametri del blocco
        Blocco.TipoVincolo tipoVincolo = blocco.getTipoVincolo();
        int risultatoVincolo = blocco.getRisultatoVincolo();

        // Calcola se è possibile soddisfare il vincolo
        switch (tipoVincolo) {
            case SOMMA:
                int sommaCorrente = valori.stream().mapToInt(Integer::intValue).sum();
                return sommaCorrente <= risultatoVincolo && sommaCorrente + celleVuote * dimensione >= risultatoVincolo;
            case PRODOTTO:
                int prodottoCorrente = valori.stream().reduce(1, (a, b) -> a * b);
                return prodottoCorrente <= risultatoVincolo && prodottoCorrente * Math.pow(dimensione, celleVuote) >= risultatoVincolo;
            case DIFFERENZA:
                if (valori.size() != 1) {
                    return true; // Differenza valida solo se mancano altre celle
                }
                int diff = Math.abs(risultatoVincolo - valori.get(0));
                return diff <= dimensione;
            case DIVISIONE:
                if (valori.size() != 1) {
                    return true; // Divisione valida solo se mancano altre celle
                }
                int quoziente = Math.max(valori.get(0), risultatoVincolo) / Math.min(valori.get(0), risultatoVincolo);
                return quoziente <= dimensione;
            default:
                return false;
        }
    }


    @Override
    protected void assegna(Casella casella, Integer valore) {
        casella.setValore(valore);
    }

    @Override
    protected void deassegna(Casella casella) {
        casella.setValore(0);
    }

    @Override
    protected void scriviSoluzione() {
        int[][] soluzioneCorrente = new int[dimensione][dimensione];
        for (int x = 0; x < dimensione; x++) {
            for (int y = 0; y < dimensione; y++) {
                soluzioneCorrente[x][y] = griglia.getCella(x, y).getValore();
            }
        }
        soluzioni.add(soluzioneCorrente);
        soluzioniTrovate++;
        System.out.println("Soluzione trovata. Totale soluzioni: " + soluzioniTrovate);
    }


    @Override
    protected boolean esisteSoluzione(Casella casella) {
        return tuttiAssegnati();
    }

    @Override
    protected boolean ultimaSoluzione(Casella casella) {
        return  maxSoluzioni ==  soluzioniTrovate;
    }

    @Override
    protected List<Casella> puntiDiScelta() {
        // Ottieni una lista di tutte le celle vuote
        List<Casella> caselle = new ArrayList<>();
        for (int x = 0; x < dimensione; x++) {
            for (int y = 0; y < dimensione; y++) {
                if (griglia.getCella(x, y).isVuota()) {
                    caselle.add(griglia.getCella(x, y));
                }
            }
        }
        return caselle;
    }

    @Override
    protected Collection<Integer> scelte(Casella casella) {
        // Ritorna tutti i numeri da 1 a dimensione come possibili valori
        Collection<Integer> valori = new HashSet<>();
        for (int i = 1; i <= dimensione; i++) {
            valori.add(i);
        }
        return valori;
    }

    @Override
    public void risolvi() {
        List<Casella> punti = puntiDiScelta();
        if (!punti.isEmpty()) {
            tentativo(punti, punti.getFirst()); // Avvia il backtracking con la prima cella
        }
    }


    private boolean tuttiAssegnati() {
        // Verifica se tutte le celle sono assegnate
        for (int x = 0; x < dimensione; x++) {
            for (int y = 0; y < dimensione; y++) {
                if (griglia.getCella(x, y).isVuota()) {
                    return false;
                }
            }
        }
        return true;
    }


}
