import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Desempenho {

    private static final int[] TAMANHOS = {1000, 5000, 10000};
    private static final int CAPACIDADE_INICIAL = 1009;

    private static class Resultado {
        long colisoes, maxSond;
        double mediaSond, carga;
        double tempoInsercaoMs, tempoPesquisaMs, pesquisaMediaNs;
    }

    public static void executar(String caminho) {
        ArrayList<Estudante> todos = new GerenciarFicheiro().lerEstudantes(caminho, 10000);
        if (todos.isEmpty()) {
            System.out.println("Nao foi possivel ler estudantes do ficheiro.");
            return;
        }

        medir(todos.subList(0, Math.min(1000, todos.size())));

        System.out.println("\n========== TESTE DE DESEMPENHO ==========");
        System.out.printf("%-9s %-9s %-11s %-10s %-8s %-15s %-15s %-14s%n",
                "Registos", "Colisoes", "Media sond.", "Max. sond.", "Carga",
                "Insercao (ns)", "Pesquisa (ns)", "Pesq. media(ns)");

        for (int t : TAMANHOS) {
            if (t > todos.size()) {
                System.out.println(t + " : o ficheiro so tem " + todos.size() + " registos validos.");
                break;
            }
            Resultado r = medir(todos.subList(0, t));
            System.out.printf(Locale.US, "%-9d %-9d %-11.2f %-10d %-8.4f %-15.3f %-15.3f %-14.0f%n",
                    t, r.colisoes, r.mediaSond, r.maxSond, r.carga,
                    r.tempoInsercaoMs, r.tempoPesquisaMs, r.pesquisaMediaNs);
        }
        System.out.println("Pesquisa (ms) = tempo total de pesquisar todos os codigos inseridos.");
    }

    private static Resultado medir(List<Estudante> lista) {
        TabelaHash tabela = new TabelaHash(CAPACIDADE_INICIAL);

        long ini = System.nanoTime();
        for (Estudante e : lista) tabela.tentarInserir(e);
        long tIns = System.nanoTime() - ini;

        int encontrados = 0;
        ini = System.nanoTime();
        for (Estudante e : lista) {
            if (tabela.pesquisar(e.getCodigo()) != null) encontrados++;
        }
        long tPesq = System.nanoTime() - ini;

        Resultado r = new Resultado();
        r.colisoes = tabela.getColisoes();
        r.maxSond = tabela.getMaxSondagens();
        r.mediaSond = tabela.getInsercoes() == 0 ? 0 : (double) tabela.getSondagens() / tabela.getInsercoes();
        r.carga = tabela.getFatorCarga();
        r.tempoInsercaoMs = tIns / 1_000_000.0;
        r.tempoPesquisaMs = tPesq / 1_000_000.0;
        r.pesquisaMediaNs = encontrados == 0 ? 0 : (double) tPesq / lista.size();
        return r;
    }
}