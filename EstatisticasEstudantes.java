import java.util.ArrayList;
import java.util.Locale;

public class EstatisticasEstudantes {

    public EstatisticasEstudantes(){}

    public static void imprimir(ArrayList<Estudante> lista) {
        int total = lista.size();
        if (total == 0) {
            System.out.println("Nao ha estudantes para analisar.");
            return;
        }
        porSexo(lista, total);
        porCurso(lista, total);
        porAno(lista, total);
        medias(lista, total);
        idades(lista, total);
    }

    private static double pct(int parte, int total) {
        return 100.0 * parte / total;
    }

    private static void porSexo(ArrayList<Estudante> lista, int total) {
        int m = 0, f = 0;
        for (Estudante e : lista) {
            if (e.getSexo().equals("M")) m++;
            else f++;
        }
        System.out.println("\n========== ESTATISTICAS POR SEXO ==========");
        System.out.printf(Locale.US, "Masculino : %d (%.1f%%)%n", m, pct(m, total));
        System.out.printf(Locale.US, "Feminino  : %d (%.1f%%)%n", f, pct(f, total));
    }

    private static void porCurso(ArrayList<Estudante> lista, int total) {
        // listas paralelas (evita HashMap/TreeMap)
        ArrayList<String> nomes = new ArrayList<>();
        ArrayList<Integer> contagem = new ArrayList<>();
        ArrayList<Double> somas = new ArrayList<>();

        for (Estudante e : lista) {
            int i = nomes.indexOf(e.getCurso());
            if (i == -1) {
                nomes.add(e.getCurso());
                contagem.add(1);
                somas.add(e.getMedia());
            } else {
                contagem.set(i, contagem.get(i) + 1);
                somas.set(i, somas.get(i) + e.getMedia());
            }
        }

        System.out.println("\n========== ESTATISTICAS POR CURSO ==========");
        for (int i = 0; i < nomes.size(); i++) {
            int c = contagem.get(i);
            System.out.println(nomes.get(i));
            System.out.println("--------------------------------");
            System.out.println("Estudantes  : " + c);
            System.out.printf(Locale.US, "Percentagem : %.1f%%%n", pct(c, total));
            System.out.printf(Locale.US, "Media       : %.2f%n%n", somas.get(i) / c);
        }
    }

    private static void porAno(ArrayList<Estudante> lista, int total) {
        int[] anos = new int[5];                       // posicoes 1 a 4
        for (Estudante e : lista) anos[e.getAno()]++;

        System.out.println("========== ESTATISTICAS POR ANO ==========");
        for (int a = 1; a <= 4; a++) {
            System.out.printf(Locale.US, "%do ano : %d (%.1f%%)%n", a, anos[a], pct(anos[a], total));
        }
    }

    private static void medias(ArrayList<Estudante> lista, int total) {
        double soma = 0;
        Estudante maior = lista.getFirst(), menor = lista.getFirst();
        int abaixo10 = 0, de10a139 = 0, de14a169 = 0, acima17 = 0;

        for (Estudante e : lista) {
            double m = e.getMedia();
            soma += m;
            if (m > maior.getMedia()) maior = e;
            if (m < menor.getMedia()) menor = e;

            if (m < 10) abaixo10++;
            else if (m < 14) de10a139++;
            else if (m < 17) de14a169++;
            else acima17++;
        }

        System.out.println("\n========== ESTATISTICAS DAS MEDIAS ==========");
        System.out.printf(Locale.US, "Media geral        : %.2f%n", soma / total);
        System.out.printf(Locale.US, "Maior media        : %.2f%n", maior.getMedia());
        System.out.printf(Locale.US, "Menor media        : %.2f%n", menor.getMedia());
        System.out.println("Maior media (aluno): " + maior);
        System.out.println("Menor media (aluno): " + menor);
        System.out.println("Media < 10         : " + abaixo10);
        System.out.println("Media 10 - 13,9    : " + de10a139);
        System.out.println("Media 14 - 16,9    : " + de14a169);
        System.out.println("Media >= 17        : " + acima17);
    }

    private static void idades(ArrayList<Estudante> lista, int total) {
        long soma = 0;
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        int a = 0, b = 0, c = 0, d = 0, e5 = 0;

        for (Estudante e : lista) {
            int id = e.getIdade();
            soma += id;
            if (id < min) min = id;
            if (id > max) max = id;

            if (id < 18) a++;
            else if (id <= 20) b++;
            else if (id <= 23) c++;
            else if (id <= 26) d++;
            else e5++;
        }

        System.out.println("\n========== ESTATISTICAS DE IDADE ==========");
        System.out.printf(Locale.US, "Idade media : %.2f%n", (double) soma / total);
        System.out.println("Menor idade : " + min);
        System.out.println("Maior idade : " + max);
        System.out.println("< 18        : " + a);
        System.out.println("18 - 20     : " + b);
        System.out.println("21 - 23     : " + c);
        System.out.println("24 - 26     : " + d);
        System.out.println("> 26        : " + e5);
    }
}