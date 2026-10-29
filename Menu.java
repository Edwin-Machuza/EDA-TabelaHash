import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Menu {

    private static final int CAPACIDADE_INICIAL = 1000;

    private final BufferedReader bfr = new BufferedReader(new InputStreamReader(System.in));
    private TabelaHash tabela = new TabelaHash(CAPACIDADE_INICIAL);
    private boolean carregado = false;

    public void mostrarMenu() {
        int opcao;

        do {
            imprimirMenu();
            opcao = lerInt("");

            switch (opcao) {
                case 1 -> carregarCSV();
                case 2 -> pesquisarEstudante();
                case 3 -> inserirEstudante();
                case 4 -> removerEstudante();
                case 5 -> estatisticasEstudantes();
                case 6 -> estatisticasHash();
                case 7 -> testeDesempenho();
                case 8 -> compararPesquisas();
                case 9 -> mostrarPorCurso();
                case 0 -> System.out.println("Obrigado e volte sempre!");
                default -> System.out.println("Opção inválida, escolha uma das opções do menu!");
            }
        } while (opcao != 0);
    }

    private void imprimirMenu() {
        System.out.println("""

                ================================================
                SISTEMA DE GESTAO DE ESTUDANTES
                ================================================
                1 - Carregar ficheiro CSV
                2 - Pesquisar estudante
                3 - Inserir estudante
                4 - Remover estudante
                5 - Estatisticas dos estudantes
                6 - Estatisticas da Hash Table
                7 - Executar teste de desempenho
                8 - Comparar pesquisa sequencial e Hash
                9 - Mostrar estudantes de um curso
                0 - Sair""");
        System.out.print("Opcao: ");
    }


    private void carregarCSV() {
        if (carregado) {
            System.out.println("O ficheiro ja foi carregado (" + tabela.getQuantidade()
                    + " estudantes na tabela). Reinicie o programa para carregar de novo.");
            return;
        }

        String caminho = pedirCaminho();

        int n = 0;
        while (n < 1 || n > 10000) {
            n = lerInt("Quantos registos carregar (1 - 10000)? ");
            if (n < 1 || n > 10000) System.out.println("Insira um valor entre 1 e 10000.");
        }

        ArrayList<Estudante> existentes = tabela.listarEstudantes();
        tabela = new TabelaHash(n);
        for (Estudante e : existentes) tabela.tentarInserir(e);

        GerenciarFicheiro gf = new GerenciarFicheiro();
        gf.carregar(caminho, n, tabela);
        carregado = gf.getTotalLinhas() > 0;

        System.out.println("\n===== RELATORIO DE LEITURA =====");
        System.out.println("Total de linhas lidas : " + gf.getTotalLinhas());
        System.out.println("Registos validos      : " + gf.getValidos());
        System.out.println("Registos invalidos    : " + gf.getInvalidos());
        System.out.println("Estudantes inseridos  : " + gf.getInseridos());
        System.out.println("Codigos duplicados    : " + gf.getDuplicados());

    }

    private void pesquisarEstudante() {
        if (verificarCarregado()) return;
        System.out.print("Codigo do estudante: ");
        String codigo = ler();

        Estudante e = tabela.pesquisar(codigo);
        if (e == null) System.out.println("Estudante com codigo " + codigo + " nao encontrado.");
        else System.out.println("Encontrado: " + e);
    }

    private void inserirEstudante() {
        System.out.print("Codigo: ");
        String codigo = ler();
        System.out.print("Nome: ");
        String nome = ler();
        System.out.print("Curso: ");
        String curso = ler();
        int ano = lerInt("Ano (1-4): ");
        int idade = lerInt("Idade: ");
        System.out.print("Sexo (M/F): ");
        String sexo = ler().toUpperCase();
        double media = lerDouble();

        if (codigo.isEmpty() || nome.isEmpty() || curso.isEmpty()
                || ano < 1 || ano > 4 || idade < 15 || idade > 100
                || !(sexo.equals("M") || sexo.equals("F"))
                || media < 0 || media > 20) {
            System.out.println("Dados invalidos. Estudante nao inserido.");
            return;
        }

        if (tabela.tentarInserir(new Estudante(codigo, nome, curso, ano, idade, sexo, media)))
            System.out.println("Estudante inserido com sucesso. Total na tabela: " + tabela.getQuantidade());
        else
            System.out.println("Ja existe um estudante com o codigo " + codigo + ".");
    }

    private void removerEstudante() {
        if (verificarCarregado()) return;
        System.out.print("Codigo do estudante a remover: ");
        String codigo = ler();

        if (tabela.remover(codigo)) System.out.println("Estudante removido (posicao marcada como REMOVIDO).");
        else System.out.println("Codigo inexistente.");
    }

    private void mostrarPorCurso() {
        if (verificarCarregado()) return;
        System.out.print("Nome do curso: ");
        String curso = ler();

        int cont = 0;
        for (Estudante e : tabela.listarEstudantes()) {
            if (e.getCurso().equalsIgnoreCase(curso)) {
                System.out.println(e);
                cont++;
            }
        }
        System.out.println(cont + " estudante(s) encontrado(s).");
    }

    private void estatisticasEstudantes() {
        if (verificarCarregado()) return;
        ArrayList<Estudante> lista = tabela.listarEstudantes();
        EstatisticasEstudantes est = new EstatisticasEstudantes();
        EstatisticasEstudantes.imprimir(lista);
    }

    private void estatisticasHash() {
        if (verificarCarregado()) return;
        tabela.imprimirEstatisticas();
    }

    private String caminhoCSV = "src/estudantes.csv";

    private String pedirCaminho() {
        System.out.print("Caminho do ficheiro (Enter = " + caminhoCSV + "): ");
        String c = ler();
        if (!c.isEmpty()) caminhoCSV = c;
        return caminhoCSV;
    }

    private void testeDesempenho() {
        Desempenho.executar(pedirCaminho());
    }

    private void compararPesquisas() {
        if (verificarCarregado()) return;
        new Comparacao().executar(tabela);
    }

    private boolean verificarCarregado() {
        if (!carregado && tabela.getQuantidade() == 0) {
            System.out.println("Nao ha dados. Use a opcao 1 para carregar o CSV primeiro.");
            return true;
        }
        return false;
    }

    private String ler() {
        try {
            String s = bfr.readLine();
            if (s == null) {                 // fim da entrada (Ctrl+D / Ctrl+Z)
                System.out.println("\nEntrada terminada.");
                System.exit(0);
            }
            return s.trim();
        } catch (IOException e) {
            System.out.println("Erro de leitura: " + e.getMessage());
            return "";
        }
    }

    private int lerInt(String mensagem) {
        System.out.print(mensagem);
        try {
            return Integer.parseInt(ler());
        } catch (NumberFormatException e) {
            System.out.println("Digite um valor numerico inteiro!");
            return -1;
        }
    }

    private double lerDouble() {
        System.out.print("Media (0-20): ");
        try {
            return Double.parseDouble(ler().replace(',', '.'));   // aceita 14,5 e 14.5
        } catch (NumberFormatException e) {
            System.out.println("Digite um valor numerico!");
            return -1;
        }
    }
}