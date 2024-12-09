package Model;

import java.util.ArrayList;
import java.util.List;

public class Blocco  {

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

    private TipoVincolo tipoVincolo;
    private int risultatoVincolo; // Valore target del vincolo
    private List<Casella> caselle; //Caselle associate al blocco

    public Blocco(TipoVincolo tipoVincolo, int risultatoVincolo) {
        this.tipoVincolo = tipoVincolo;
        this.risultatoVincolo = risultatoVincolo;
        this.caselle = new ArrayList<>();
    }

    public void aggiungiCasella(Casella casella) {
        caselle.add(casella);
    }

    public List<Casella> getCaselle() {
        return caselle;
    }

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

    private boolean verificaSomma(List<Integer> valori) {
        int somma = 0;
        for (int valore : valori) {
            somma += valore;
        }
        return somma == risultatoVincolo;
    }

    private boolean verificaProdotto(List<Integer> valori) {
        int prodotto = 1;
        for (int valore : valori) {
            prodotto *= valore;
        }
        return prodotto == risultatoVincolo;
    }

    private boolean verificaDifferenza(List<Integer> valori) {
        if (valori.size() != 2) {
            return false; // Differenza valida solo per due valori
        }
        int diff = Math.abs(valori.get(0) - valori.get(1));
        return diff == risultatoVincolo;
    }

    private boolean verificaDivisione(List<Integer> valori) {
        if (valori.size() != 2) {
            return false; // Divisione valida solo per due valori
        }
        int max = Math.max(valori.get(0), valori.get(1));
        int min = Math.min(valori.get(0), valori.get(1));
        return (max / min) == risultatoVincolo && (max % min) == 0;
    }

    public TipoVincolo getTipoVincolo() {
        return tipoVincolo;
    }

    public int getRisultatoVincolo() {
        return risultatoVincolo;
    }

}
