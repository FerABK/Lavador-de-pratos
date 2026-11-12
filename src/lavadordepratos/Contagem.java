package lavadordepratos;


final class Contagem {

    private Contagem() {
    }

    static long contarAte(long limite) {
        long contador = 0;
        for (long i = 0; i < limite; i++) {
            contador++;
        }
        return contador;
    }
}
