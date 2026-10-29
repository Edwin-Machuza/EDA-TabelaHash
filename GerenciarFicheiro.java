import java.io.*;
import java.util.ArrayList;

public class GerenciarFicheiro {

    private int totalLinhas, validos, invalidos, inseridos, duplicados;

    public void carregar(String caminho, int max, TabelaHash tabela) {
        totalLinhas = validos = invalidos = inseridos = duplicados = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            br.readLine();
            String linha;

            while ((linha = br.readLine()) != null && totalLinhas < max) {
                if (linha.trim().isEmpty()) continue;
                totalLinhas++;

                Estudante e = converter(linha);
                if (e == null) {
                    invalidos++;
                    continue;
                }
                validos++;
                if (tabela.tentarInserir(e)) inseridos++;
                else duplicados++;
            }
        } catch (FileNotFoundException fnf) {
            System.out.println("Ficheiro não encontrado: " + caminho);
            System.out.println("Em" + new File(caminho).getAbsolutePath());
        } catch (IOException ex) {
            System.out.println("Erro de leitura: " + ex.getMessage());
        }
    }

    private Estudante converter(String linha) {
        String[] c = linha.split(",", -1); 
        if (c.length != 7) return null;

        try {
            String codigo = c[0].trim();
            String nome   = c[1].trim();
            String curso  = c[2].trim();
            int ano       = Integer.parseInt(c[3].trim());
            int idade     = Integer.parseInt(c[4].trim());
            String sexo   = c[5].trim().toUpperCase();
            double media  = Double.parseDouble(c[6].trim());

            if (codigo.isEmpty() || nome.isEmpty() || curso.isEmpty()) return null;
            if (ano < 1 || ano > 4) return null;
            if (idade < 15 || idade > 100) return null;
            if (!sexo.equals("M") && !sexo.equals("F")) return null;
            if (media < 0 || media > 20) return null;

            return new Estudante(codigo, nome, curso, ano, idade, sexo, media);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public int getTotalLinhas() { return totalLinhas; }
    public int getValidos()     { return validos; }
    public int getInvalidos()   { return invalidos; }
    public int getInseridos()   { return inseridos; }
    public int getDuplicados()  { return duplicados; }

    public ArrayList<Estudante> lerEstudantes(String caminho, int max) {
        ArrayList<Estudante> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            br.readLine();
            String linha;
            while ((linha = br.readLine()) != null && lista.size() < max) {
                if (linha.trim().isEmpty()) continue;
                Estudante e = converter(linha);
                if (e != null) lista.add(e);
            }
        } catch (FileNotFoundException f) {
            System.out.println("Ficheiro nao encontrado: " + new File(caminho).getAbsolutePath());
        } catch (IOException ex) {
            System.out.println("Erro de leitura: " + ex.getMessage());
        }
        return lista;
    }
}