package Model;


public class Casella {
    private final int x; // Coordinata X nella griglia
    private final int y; // Coordinata Y nella griglia
    private int valore; // Valore attuale della casella
    private Blocco blocco;  // Blocco a cui appartiene la casella

    // Costruttore
    public Casella(int x, int y) {
        this.x = x;
        this.y = y;
        this.valore = 0; // Valore iniziale, vuoto
    }

    // Getter e setter per il valore
    public int getValore() {
        return valore;
    }

    public void setValore(int valore) {
        this.valore = valore;
    }

    // Getter per le coordinate
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Verifica se la casella è vuota
    public boolean isVuota() {
        return valore == 0;
    }


    // Metodo per ottenere il blocco a cui la casella appartiene
    public Blocco getBlocco() {
        return blocco;
    }

    // Metodo per associare una casella a un blocco (assegna il blocco alla casella)
    public void setBlocco(Blocco blocco) {
        this.blocco = blocco;  // Assegna il blocco alla casella
    }

    @Override
    public String toString() {
        return "Casella[x=" + x + ", y=" + y + ", valore=" + valore + "]";
    }


}
