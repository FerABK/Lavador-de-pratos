package lavadordepratos;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    
    private static final Level NIVEL_LOG = Level.INFO;
    private static final long DURACAO_MS = 60_000L;
    private static final String PACOTE_LOG = App.class.getPackageName();

    private static final Logger LOGGER_RAIZ = Logger.getLogger(PACOTE_LOG);
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    private final Escorredor escorredor;
    private final List<Lavador> lavadores = new ArrayList<>();
    private final List<Enxugador> enxugadores = new ArrayList<>();
    private final List<Thread> threadsLavadores = new ArrayList<>();
    private final List<Thread> threadsEnxugadores = new ArrayList<>();

    public App(int quantidadeLavadores, int quantidadeEnxugadores, int capacidadeEscorredor) {
        if (quantidadeLavadores <= 0 || quantidadeEnxugadores <= 0) {
            throw new IllegalArgumentException("Deve existir pelo menos um lavador e um enxugador.");
        }

        escorredor = new Escorredor(capacidadeEscorredor);
        for (int i = 1; i <= quantidadeLavadores; i++) {
            lavadores.add(new Lavador(escorredor, i));
        }
        for (int i = 1; i <= quantidadeEnxugadores; i++) {
            enxugadores.add(new Enxugador(escorredor, i));
        }
    }

    public void iniciar() {
        LOGGER.info("Iniciando a aplicação Lavador de Pratos.");
        iniciarThreads(enxugadores, threadsEnxugadores, "Enxugador-");
        iniciarThreads(lavadores, threadsLavadores, "Lavador-");
    }

    public void parar() {
        LOGGER.info("Solicitando o encerramento dos trabalhos.");

        lavadores.forEach(Lavador::parar);
        aguardar(threadsLavadores);

        escorredor.encerrarProducao();
        enxugadores.forEach(Enxugador::parar);
        interromper(threadsEnxugadores);
        aguardar(threadsEnxugadores);

        LOGGER.info("Todos os trabalhos foram encerrados. Aplicação finalizada.");
    }

    private static void iniciarThreads(List<? extends Runnable> tarefas, List<Thread> threads, String prefixo) {
        int numero = 1;
        for (Runnable tarefa : tarefas) {
            Thread thread = new Thread(tarefa, prefixo + numero++);
            threads.add(thread);
            thread.start();
        }
    }

    private static void interromper(List<Thread> threads) {
        threads.forEach(Thread::interrupt);
    }

    private static void aguardar(List<Thread> threads) {
        boolean interrompido = false;
        for (Thread thread : threads) {
            while (thread.isAlive()) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    interrompido = true; 
                }
            }
        }
        if (interrompido) {
            Thread.currentThread().interrupt();
            LOGGER.warning("A thread principal foi interrompida durante o encerramento.");
        }
    }

    public static void configurarLog(Level nivel) {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tH:%1$tM:%1$tS.%1$tL [%4$s] %5$s%n");

        LOGGER_RAIZ.setUseParentHandlers(false);
        for (var handler : LOGGER_RAIZ.getHandlers()) {
            LOGGER_RAIZ.removeHandler(handler);
        }

        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(nivel);
        LOGGER_RAIZ.addHandler(handler);
        LOGGER_RAIZ.setLevel(nivel);
    }

    public static void main(String[] args) throws InterruptedException {
        configurarLog(NIVEL_LOG);

        App app = new App(2, 2, 5);
        app.iniciar();

        Thread.sleep(DURACAO_MS);

        app.parar();
    }
}