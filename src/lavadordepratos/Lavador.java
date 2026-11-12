package lavadordepratos;

import java.util.logging.Logger;

public class Lavador implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Lavador.class.getName());

    private final Escorredor escorredor;
    private final int identificador;
    private volatile boolean done;

    public Lavador(Escorredor escorredor, int identificador) {
        this.escorredor = escorredor;
        this.identificador = identificador;
    }

    public void parar() {
        done = true;
    }

    @Override
    public void run() {
        LOGGER.fine(() -> "Lavador " + identificador + " iniciou o trabalho.");

        try {
            while (!done) {
                escorredor.aguardarEspaco();
                if (done) {
                    break; 
                }

                Prato prato = new Prato();
                LOGGER.fine(() -> "Lavador " + identificador + " começou a lavar " + prato + ".");
                long contagem = Contagem.contarAte(prato.getNivelSujeira().getIteracoesLavagem());
                LOGGER.fine(() -> "Lavador " + identificador + " terminou de lavar " + prato
                        + " (contagem " + contagem + ").");

                escorredor.colocar(prato);
            }
        } catch (InterruptedException e) {
            LOGGER.fine(() -> "Lavador " + identificador + " foi interrompido enquanto aguardava.");
            Thread.currentThread().interrupt();
        }

        LOGGER.fine(() -> "Lavador " + identificador + " encerrou o trabalho.");
    }
}