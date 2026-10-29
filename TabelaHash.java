import java.util.ArrayList;

public class TabelaHash {

    public static final int VAZIO = -1;
    public static final int OCUPADO = -2;
    public static final int REMOVIDO = -3;
    private static final double LIMITE_CARGA = 0.70;
    private final int capacidadeInformada; 
    private final int capacidadeInicial;
    private Entrada[] tabela;
    private int capacidade;
    private int primo;
    private int n;

    private static class Entrada {
        Estudante estudante;
        int estado = VAZIO;
    }

    private long colisoes, sondagens, maxSondagens, insercoes;
    private int quantRedimensionamento;

    public TabelaHash(int capacidade) {
        this.capacidadeInformada = capacidade;
        capacidade = proximoPrimo(capacidade);
        this.capacidade = capacidade;
        this.capacidadeInicial = capacidade;
        criarTabela(capacidade);
    }

    private void criarTabela(int cap) {
        this.capacidade = cap;
        this.tabela = new Entrada[cap];
        for (int i = 0; i < cap; i++) tabela[i] = new Entrada();
        this.primo = primoAnterior(cap - 1);
        this.n = 0;
    }

    private long valorBase(String codigo) {
        long h = 0;
        for (int i = 0; i < codigo.length(); i++) {
            h = (h * 31 + codigo.charAt(i)) & 0x7fffffffL;
        }
        return h;
    }

    private int hash1(long base) {
        return (int) (base % capacidade);
    }

    private int hash2(long base) {
        return (int) (primo - (base % primo));
    }

    private int posicao(int h1, int h2, int i) {
        return (int) ((h1 + (long) i * h2) % capacidade);
    }

//    public void inserir(Estudante estudante) {
//        if (!tentarInserir(estudante)) {
//            System.out.println("O estudante já está registado ou é inválido!");
//        }
//    }

    public boolean tentarInserir(Estudante estudante) {
        if (estudante == null || estudante.getCodigo() == null) return false;

        if ((double) (n + 1) / capacidade > LIMITE_CARGA) {
            redimensionar();
        }
        return colocar(estudante, true);
    }

    private boolean colocar(Estudante est, boolean contar) {
        String codigo = est.getCodigo();
        long base = valorBase(codigo);
        int h1 = hash1(base), h2 = hash2(base);

        int destino = -1, primeiroRemovido = -1;
        int tentativas = 0, col = 0;

        for (int i = 0; i < capacidade; i++) {
            int idx = posicao(h1, h2, i);
            Entrada e = tabela[idx];
            tentativas++;

            if (e.estado == VAZIO) {
                destino = (primeiroRemovido != -1) ? primeiroRemovido : idx;
                break;
            }
            if (e.estado == REMOVIDO) {
                if (primeiroRemovido == -1) primeiroRemovido = idx;
            } else {
                if (e.estudante.getCodigo().equals(codigo)) return false;
                col++;
            }
        }
        if (destino == -1) destino = primeiroRemovido;
        if (destino == -1) return false;

        tabela[destino].estudante = est;
        tabela[destino].estado = OCUPADO;
        n++;

        if (contar) {
            insercoes++;
            colisoes += col;
            sondagens += tentativas;
            if (tentativas > maxSondagens) maxSondagens = tentativas;
        }
        return true;
    }

    public Estudante pesquisar(String codigo) {
        int idx = localizar(codigo);
        return idx == -1 ? null : tabela[idx].estudante;
    }

    public boolean remover(String codigo) {
        int idx = localizar(codigo);
        if (idx == -1) return false;
        tabela[idx].estado = REMOVIDO;
        tabela[idx].estudante = null;
        n--;
        return true;
    }

    private int localizar(String codigo) {
        if (codigo == null) return -1;
        long base = valorBase(codigo);
        int h1 = hash1(base), h2 = hash2(base);

        for (int i = 0; i < capacidade; i++) {
            int idx = posicao(h1, h2, i);
            Entrada e = tabela[idx];
            if (e.estado == VAZIO) return -1;
            if (e.estado == OCUPADO && e.estudante.getCodigo().equals(codigo)) return idx;
        }
        return -1;
    }

    // ---------- redimensionamento ----------
    private void redimensionar() {
        Entrada[] antiga = tabela;
        quantRedimensionamento++;
        criarTabela(proximoPrimo(capacidade + 1));      // antes: proximoPrimo(2 * capacidade)

        for (Entrada e : antiga) {
            if (e.estado == OCUPADO) colocar(e.estudante, false);
        }
    }

    // ---------- primos ----------
    private boolean ePrimo(int c) {
        if (c < 2) return true;
        for (int i = 2; (long) i * i <= c; i++) if (c % i == 0) return true;
        return false;
    }

    private int proximoPrimo(int x) {
        while (ePrimo(x)) x++;
        return x;
    }

    private int primoAnterior(int x) {
        while (x > 2 && ePrimo(x)) x--;
        return x;
    }

    public int getQuantidade() { return n; }
    public double getFatorCarga() { return (double) n / capacidade; }

    public ArrayList<Estudante> listarEstudantes() {
        ArrayList<Estudante> lista = new ArrayList<>();
        for (Entrada e : tabela) if (e.estado == OCUPADO) lista.add(e.estudante);
        return lista;
    }

    public void imprimirEstatisticas() {
        System.out.println("========== ESTATISTICAS DA HASH TABLE ==========");
        System.out.println("Capacidade inicial       : " + capacidadeInformada);
        if (capacidadeInformada != capacidadeInicial) {
            System.out.println("Capacidade inicial primo : " + capacidadeInicial + " (ajustada para numero primo)");
        }
        System.out.println("Capacidade final         : " + capacidade);
        System.out.println("Elementos armazenados    : " + n);
        System.out.printf ("Factor de carga          : %.4f%n", getFatorCarga());
        System.out.println("Colisoes                 : " + colisoes);
        System.out.println("Sondagens realizadas     : " + sondagens);
        System.out.printf ("Media sondagens/insercao : %.2f%n",
                insercoes == 0 ? 0.0 : (double) sondagens / insercoes);
        System.out.println("Maior sondagem           : " + maxSondagens);
        System.out.println("Redimensionamentos       : " + quantRedimensionamento);
    }

    public long getColisoes() { return colisoes; }
    public long getSondagens() { return sondagens; }
    public long getMaxSondagens() { return maxSondagens; }
    public long getInsercoes() { return insercoes; }
}