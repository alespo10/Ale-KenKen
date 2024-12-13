package Memento;

import Model.Blocco;

import java.io.Serializable;
import java.util.List;

public class GameMemento implements Memento, Serializable {
    private final int[][] valoriGriglia;
    private final List<BloccoInfo> blocchiInfo;
    private final List<int[][]> soluzioni; // Aggiunto campo per le soluzioni

    public GameMemento(int[][] valoriGriglia, List<BloccoInfo> blocchiInfo, List<int[][]> soluzioni) {
        this.valoriGriglia = valoriGriglia;
        this.blocchiInfo = blocchiInfo;
        this.soluzioni = soluzioni;
    }
    public int[][] getValoriGriglia() {
        return valoriGriglia;
    }

    public List<BloccoInfo> getBlocchiInfo() {
        return blocchiInfo;
    }

    public List<int[][]> getSoluzioni() {return soluzioni;}

    public static class BloccoInfo implements Serializable {
        public final Blocco.TipoVincolo tipoVincolo;
        public final int risultatoVincolo;
        public final List<int[]> celle;

        public BloccoInfo(Blocco.TipoVincolo tipoVincolo, int risultatoVincolo, List<int[]> celle) {
            this.tipoVincolo = tipoVincolo;
            this.risultatoVincolo = risultatoVincolo;
            this.celle = celle;
        }
    }
}



