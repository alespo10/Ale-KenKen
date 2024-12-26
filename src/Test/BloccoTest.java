package Test;

import org.junit.jupiter.api.Test;
import Model.Blocco;
import Model.Casella;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BloccoTest {

    @Test
    public void testAddCasella() {
        Blocco blocco = new Blocco( Blocco.TipoVincolo.SOMMA, 15);
        Casella casella = new Casella(0, 0);
        blocco.aggiungiCasella(casella);

        assertTrue(blocco.getCaselle().contains(casella), "La casella dovrebbe appartenere al blocco");
    }

}
