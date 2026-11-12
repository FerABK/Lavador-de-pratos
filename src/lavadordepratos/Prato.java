package lavadordepratos;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Prato {
    private static final int LIMITE_BAIXO = 30;   
    private static final int LIMITE_MEDIO = 90;
    private static final int TOTAL_SORTEIO = 100;

    private static final AtomicInteger PROXIMO_NUMERO = new AtomicInteger(1);

    private final int numeroSerie;
    private final NivelSujeira nivelSujeira;

    public Prato() {
        numeroSerie = PROXIMO_NUMERO.getAndIncrement();

        int sorteio = ThreadLocalRandom.current().nextInt(TOTAL_SORTEIO);
        if (sorteio < LIMITE_BAIXO) {
            nivelSujeira = NivelSujeira.BAIXO;
        } else if (sorteio < LIMITE_MEDIO) {
            nivelSujeira = NivelSujeira.MEDIO;
        } else {
            nivelSujeira = NivelSujeira.ENGORDURADO;
        }
    }

    public int getNumeroSerie() {
        return numeroSerie;
    }

    public NivelSujeira getNivelSujeira() {
        return nivelSujeira;
    }

    @Override
    public String toString() {
        return "Prato " + numeroSerie + " (" + nivelSujeira + ")";
    }
}
