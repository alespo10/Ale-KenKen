package Model;

import java.util.ArrayList;
import java.util.List;

public class Blocco  {

    // Tipo di operazione del vincolo
    public enum TipoVincolo {
        SOMMA('+'),
        DIFFERENZA('-'),
        PRODOTTO('*'),
        DIVISIONE('/');

        private final char simbolo;

        TipoVincolo(char simbolo) {
            this.simbolo = simbolo;
        }

        public char getSimbolo() {
            return simbolo;
        }

        @Override
        public String toString() {
            return String.valueOf(simbolo);
        }
    }

    private TipoVincolo tipoVincolo; // Tipo del vincolo (somma, prodotto, ecc.)
    private int risultatoVincolo; // Valore target del vincolo
    private List<Casella> caselle; // Caselle incluse nel blocco

    // Costruttore
    public Blocco(TipoVincolo tipoVincolo, int risultatoVincolo) {
        this.tipoVincolo = tipoVincolo;
        this.risultatoVincolo = risultatoVincolo;
        this.caselle = new ArrayList<>();
    }

    // Aggiungi una casella al blocco
    public void aggiungiCasella(Casella casella) {
        caselle.add(casella);
    }

    // Restituisce la lista delle caselle
    public List<Casella> getCaselle() {
        return caselle;
    }

    // Verifica se il blocco soddisfa il vincolo
    public boolean verificaVincolo() {
        List<Integer> valori = new ArrayList<>();
        for (Casella casella : caselle) {
            if (casella.getValore() == 0) {
                return false; // Se una casella è vuota, non possiamo ancora verificare
            }
            valori.add(casella.getValore());
        }
        // Controllo in base al tipo di vincolo
        switch (tipoVincolo) {
            case SOMMA:
                return verificaSomma(valori);
            case PRODOTTO:
                return verificaProdotto(valori);
            case DIFFERENZA:
                return verificaDifferenza(valori);
            case DIVISIONE:
                return verificaDivisione(valori);
            default:
                throw new IllegalStateException("Tipo di vincolo non supportato");
        }
    }

    // Verifica che la somma dei valori sia uguale al risultatoVincolo
    private boolean verificaSomma(List<Integer> valori) {
        int somma = 0;
        for (int valore : valori) {
            somma += valore;
        }
        return somma == risultatoVincolo;
    }

    // Verifica che il prodotto dei valori sia uguale al risultatoVincolo
    private boolean verificaProdotto(List<Integer> valori) {
        int prodotto = 1;
        for (int valore : valori) {
            prodotto *= valore;
        }
        return prodotto == risultatoVincolo;
    }

    // Verifica che la differenza assoluta sia uguale al risultatoVincolo
    private boolean verificaDifferenza(List<Integer> valori) {
        if (valori.size() != 2) {
            return false; // Differenza valida solo per due valori
        }
        int diff = Math.abs(valori.get(0) - valori.get(1));
        return diff == risultatoVincolo;
    }

    // Verifica che il quoziente o il reciproco sia uguale al risultatoVincolo
    private boolean verificaDivisione(List<Integer> valori) {
        if (valori.size() != 2) {
            return false; // Divisione valida solo per due valori
        }
        int max = Math.max(valori.get(0), valori.get(1));
        int min = Math.min(valori.get(0), valori.get(1));
        return (max / min) == risultatoVincolo && (max % min) == 0;
    }

    // Restituisce il tipo di vincolo
    public TipoVincolo getTipoVincolo() {
        return tipoVincolo;
    }

    // Restituisce il risultato del vincolo
    public int getRisultatoVincolo() {
        return risultatoVincolo;
    }

}
