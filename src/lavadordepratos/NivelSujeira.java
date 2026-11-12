package lavadordepratos;


public enum NivelSujeira {
    BAIXO(30_000_000L),
    MEDIO(50_000_000L),
    ENGORDURADO(100_000_000L);

    private final long iteracoesLavagem;

    NivelSujeira(long iteracoesLavagem) {
        this.iteracoesLavagem = iteracoesLavagem;
    }

    public long getIteracoesLavagem() {
        return iteracoesLavagem;
    }
}
