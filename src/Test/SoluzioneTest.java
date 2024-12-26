package Test;

import Model.Griglia;
import Model.Soluzione;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SoluzioneTest {

    @Test
    void testGeneraSoluzioni() {
        Griglia griglia = new Griglia(4); // Griglia di dimensione 4x4
        int maxSoluzioni = 3;
        Soluzione solver = new Soluzione(griglia, maxSoluzioni);

        solver.risolvi();
        List<int[][]> soluzioni = solver.getSoluzioni();

        assertNotNull(soluzioni);
        assertFalse(soluzioni.isEmpty());
    }

    @Test
    void testVerificaSoluzioni() {
        int maxSoluzioni = 3;
        Griglia griglia = new Griglia(3); // Griglia di dimensione 3x3
        Soluzione solver = new Soluzione(griglia,maxSoluzioni);

        solver.risolvi();
        List<int[][]> soluzioni = solver.getSoluzioni();

        int[][] utente = {
                {1, 2, 3},
                {3, 1, 2},
                {2, 3, 1}
        };

        boolean trovato = soluzioni.stream()
                .anyMatch(soluzione -> Arrays.deepEquals(soluzione, utente));
        assertTrue(trovato);
    }
}
