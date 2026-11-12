package lavadordepratos;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class Enxugador implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Enxugador.class.getName());

    private static final long CONTAGEM_MINIMA = 30_000_000L;
    private static final long CONTAGEM_MAXIMA = 100_000_000L;
    private static final long PASSO_CONTAGEM = 10_000_000L;
    private static final int QUANTIDADE_PASSOS = (int) ((CONTAGEM_MAXIMA - CONTAGEM_MINIMA) / PASSO_CONTAGEM) + 1;

    private final Escorredor escorredor;
    private final int identificador;
    private volatile boolean done;

    public Enxugador(Escorredor escorredor, int identificador) {
        this.escorredor = escorredor;
        this.identificador = identificador;
    }

    public void parar() {
        done = true;
    }

    @Override
    public void run() {
        LOGGER.fine(() -> "Enxugador " + identificador + " iniciou o trabalho.");

        
        while (!done || !escorredor.estaVazio()) {
            try {
                Prato prato = escorredor.retirar();
                if (prato == null) {
                    break; 
                }

                LOGGER.fine(() -> "Enxugador " + identificador + " começou a enxugar " + prato + ".");
                long contagem = Contagem.contarAte(sortearContagem());
                LOGGER.fine(() -> "Enxugador " + identificador + " terminou de enxugar " + prato
                        + " (contagem " + contagem + ").");
            } catch (InterruptedException e) {
                
                LOGGER.fine(() -> "Enxugador " + identificador + " foi acordado por interrupção.");
            }
        }

        LOGGER.fine(() -> "Enxugador " + identificador + " encerrou o trabalho.");
    }

    private long sortearContagem() {
        return CONTAGEM_MINIMA + ThreadLocalRandom.current().nextInt(QUANTIDADE_PASSOS) * PASSO_CONTAGEM;
    }
}
