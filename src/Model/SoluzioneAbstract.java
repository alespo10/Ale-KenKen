package Model;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

public abstract class SoluzioneAbstract<P,S> {

    protected abstract boolean assegnabile( P p, S s );
    protected abstract void assegna( P ps, S s );
    protected abstract void deassegna( P ps);
    protected abstract void scriviSoluzione();

    private final P prossimoPuntoDiScelta(List<P> ps, P p ) {
        if( esisteSoluzione(p) ) throw new NoSuchElementException();
        int i=ps.indexOf(p);
        return ps.get(i+1);
    }//prossimoPuntoDiScelta

    protected abstract boolean esisteSoluzione( P p );

    protected abstract boolean ultimaSoluzione( P p );

    //factory
    protected abstract List<P> puntiDiScelta();
    protected abstract Collection<S> scelte(P p );


    protected void tentativo(List<P> punti, P punto) {
        if (ultimaSoluzione(punto)) return; // Termina solo se raggiunto il numero massimo di soluzioni

        for (S scelta : scelte(punto)) {
            if (assegnabile(punto, scelta)) {
                assegna(punto, scelta);
                if (esisteSoluzione(punto)) {
                    scriviSoluzione();
                } else {
                    // Prova con il prossimo punto
                    List<P> nuoviPunti = new ArrayList<>(punti);
                    nuoviPunti.remove(punto);
                    if (!nuoviPunti.isEmpty()) {
                        tentativo(nuoviPunti, nuoviPunti.getFirst());
                    }
                }
                deassegna(punto); // Utilizzo il Backtracking: de assegno la scelta
            }
        }
    }


    protected abstract void risolvi();

}