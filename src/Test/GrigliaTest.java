package Test;

import Model.Griglia;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GrigliaTest {

    @Test
    void testCreaGriglia() {
        Griglia griglia = new Griglia(4);
        assertNotNull(griglia);
        assertEquals(4, griglia.getDimensione());
    }

    @Test
    void testAggiungiValore() {
        Griglia griglia = new Griglia(4);
        griglia.getCella(0, 0).setValore(5);
        assertEquals(5, griglia.getCella(0, 0).getValore());
    }


    //Effettuo un test errato deliberatamente
    @Test
    void testValoreFuoriRange() {
        Griglia griglia = new Griglia(3); // Griglia 3x3

        // Prova ad aggiungere un valore fuori dal range consentito
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            griglia.getCella(0, 0).setValore(10); // Valore fuori dal range [1, dimensione]
        });

        assertEquals("Valore non valido", exception.getMessage());
    }

}
