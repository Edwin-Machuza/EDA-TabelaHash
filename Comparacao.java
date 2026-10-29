import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class Comparacao {
    private static final int PESQUISAS = 1000;

    public void executar(TabelaHash tabela) {
        ArrayList<Estudante> lista = tabela.listarEstudantes();
        int n = lista.size();
        if (n == 0) {
            System.out.println("Nao ha estudantes para comparar.");
            return;
        }
        int q = Math.min(PESQUISAS, n);

        Random rnd = new Random(42);
        String[] existentes = new String[q];
        String[] inexistentes = new String[q];
        for (int i = 0; i < q; i++) {
            existentes[i] = lista.get(rnd.nextInt(n)).getCodigo();
            inexistentes[i] = "INEXISTENTE-" + i;
        }

        for (int r = 0; r < 3; r++) {
            tempoSequencial(lista, existentes);
            tempoHash(tabela, existentes);
            tempoSequencial(lista, inexistentes);
            tempoHash(tabela, inexistentes);
        }

        double seqEx = (double) tempoSequencial(lista, existentes) / q;
        double hashEx = (double) tempoHash(tabela, existentes) / q;
        double seqIn = (double) tempoSequencial(lista, inexistentes) / q;
        double hashIn = (double) tempoHash(tabela, inexistentes) / q;

        System.out.println("\n========== PESQUISA SEQUENCIAL vs HASH ==========");
        System.out.println("Estudantes na estrutura : " + n);
        System.out.println("Pesquisas por teste     : " + q);
        System.out.printf("%-22s %-18s %-18s %-10s%n", "Tipo de codigo", "Sequencial (ns)", "Hash (ns)", "Ganho");
        linha("Existentes", seqEx, hashEx);
        linha("Inexistentes", seqIn, hashIn);

        System.out.println("\nInterpretacao:");
        System.out.println("- A pesquisa sequencial percorre a lista: O(n). Num codigo inexistente percorre sempre os "
                + n + " elementos.");
        System.out.println("- A pesquisa hash avalia h1 e h2 e examina poucas posicoes: O(1) em media, "
                + "e quase independente de n.");
    }

    private static void linha(String tipo, double seq, double hash) {
        double ganho = seq / Math.max(hash, 1.0);
        System.out.printf(Locale.US, "%-22s %-18.0f %-18.0f %-10.1fx%n", tipo, seq, hash, ganho);
    }

    private static Estudante sequencial(ArrayList<Estudante> lista, String codigo) {
        for (Estudante e : lista) {
            if (e.getCodigo().equals(codigo)) return e;
        }
        return null;
    }

    private static long tempoSequencial(ArrayList<Estudante> lista, String[] codigos) {
        int achados = 0;
        long ini = System.nanoTime();
        for (String c : codigos) if (sequencial(lista, c) != null) achados++;
        long t = System.nanoTime() - ini;
        return achados >= 0 ? t : 0;
    }

    private static long tempoHash(TabelaHash tabela, String[] codigos) {
        int achados = 0;
        long ini = System.nanoTime();
        for (String c : codigos) if (tabela.pesquisar(c) != null) achados++;
        long t = System.nanoTime() - ini;
        return achados >= 0 ? t : 0;
    }
}
