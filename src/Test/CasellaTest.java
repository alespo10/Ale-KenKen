package Test;

import org.junit.jupiter.api.Test;
import Model.Casella;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CasellaTest {

    @Test
    public void testSetValore() {
        Casella casella = new Casella(0, 0);
        casella.setValore(5);
        assertEquals(5, casella.getValore(), "Il valore dovrebbe essere 5");
    }

}
