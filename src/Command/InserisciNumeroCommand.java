package Command;

import Model.Casella;

public class InserisciNumeroCommand implements Command {
    private final Casella casella;
    private final int nuovoValore;
    private final int valorePrecedente;

    public InserisciNumeroCommand(Casella casella, int nuovoValore) {
        this.casella = casella;
        this.nuovoValore = nuovoValore;
        this.valorePrecedente = casella.getValore();
    }

    @Override
    public boolean doIt() {
        casella.setValore(nuovoValore);
        return true;
    }

    @Override
    public boolean undoIt() {
        casella.setValore(valorePrecedente);
        return true;
    }
}
