package lavadordepratos;

import java.util.logging.Logger;

public class Escorredor {
    private static final Logger LOGGER = Logger.getLogger(Escorredor.class.getName());
    private static final int CAPACIDADE_MINIMA = 2;

    private final Object portaLavadores = new Object();
    private final Object portaEnxugadores = new Object();

    private final Prato[] pratos;
    private int inicio;
    private int fim;
    private int quantidade;
    private boolean producaoEncerrada;

    public Escorredor(int capacidade) {
        if (capacidade < CAPACIDADE_MINIMA) {
            throw new IllegalArgumentException("A capacidade deve ser de pelo menos " + CAPACIDADE_MINIMA + ".");
        }
        pratos = new Prato[capacidade];
    }
   
    public void aguardarEspaco() throws InterruptedException {
        synchronized (portaLavadores) {
            synchronized (this) {
                esperarEnquantoCheio();
            }
        }
    }

    public void colocar(Prato prato) throws InterruptedException {
        synchronized (portaLavadores) {
            synchronized (this) {
                esperarEnquantoCheio();

                pratos[fim] = prato;
                fim = (fim + 1) % pratos.length;
                quantidade++;
                LOGGER.fine(() -> "Colocou " + prato + ". Pratos no escorredor: " + quantidade);

                if (quantidade == 1) {
                    notify();
                }
                if (quantidade == pratos.length) {
                    LOGGER.info(() -> "ESCORREDOR CHEIO! Pratos no escorredor: " + quantidade);
                }
            }
        }
    }

    public Prato retirar() throws InterruptedException {
        synchronized (portaEnxugadores) {
            synchronized (this) {
                if (quantidade == 0) {
                    if (producaoEncerrada) {
                        return null;
                    }
                    LOGGER.info(() -> "Enxugador aguardando: escorredor VAZIO. Pratos no escorredor: " + quantidade);
                    while (quantidade == 0 && !producaoEncerrada) {
                        wait();
                    }
                    if (quantidade == 0) {
                        return null; 
                    }
                }

                boolean estavaCheio = quantidade == pratos.length;
                Prato prato = pratos[inicio];
                pratos[inicio] = null;
                inicio = (inicio + 1) % pratos.length;
                quantidade--;
                LOGGER.fine(() -> "Retirou " + prato + ". Pratos no escorredor: " + quantidade);

                if (estavaCheio) {
                    notify(); 
                }
                if (quantidade == 0) {
                    LOGGER.info(() -> "ESCORREDOR VAZIO! Pratos no escorredor: " + quantidade);
                }
                return prato;
            }
        }
    }

    public synchronized boolean estaVazio() {
        return quantidade == 0;
    }
    
    public synchronized void encerrarProducao() {
        producaoEncerrada = true;
    }
    
    private void esperarEnquantoCheio() throws InterruptedException {
        if (quantidade == pratos.length) {
            LOGGER.info(() -> "Lavador aguardando: escorredor CHEIO. Pratos no escorredor: " + quantidade);
            while (quantidade == pratos.length) {
                wait();
            }
        }
    }
}
