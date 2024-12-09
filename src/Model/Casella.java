package Model;


public class Casella {
    private final int x;
    private final int y;
    private int valore;
    private Blocco blocco;  // Blocco a cui appartiene la casella

    // Costruttore
    public Casella(int x, int y) {
        this.x = x;
        this.y = y;
        this.valore = 0; // Il valore iniziale è vuoto
    }

    public int getValore() {
        return valore;
    }

    public void setValore(int valore) {
        this.valore = valore;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isVuota() {
        return valore == 0;
    }


    public Blocco getBlocco() {
        return blocco;
    }

    public void setBlocco(Blocco blocco) {
        this.blocco = blocco;  // Assegna il blocco alla casella
    }

    @Override
    public String toString() {
        return "Casella[x=" + x + ", y=" + y + ", valore=" + valore + "]";
    }


}
