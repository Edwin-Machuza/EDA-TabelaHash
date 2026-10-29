public class Estudante {
    private String nome;
    private String curso;
    private int ano;
    private String codigo;
    private int idade;
    private String sexo;
    private double media;

    public Estudante(){

    }

    public Estudante(String codigo, String nome, String curso, int ano, int idade, String sexo, double media){
        this.codigo = codigo;
        this.nome = nome;
        this.curso = curso;
        this.ano = ano;
        this.idade = idade;
        this.sexo = sexo;
        this.media = media;
    }

    public String getNome() {
        return nome;
    }

    public String getCurso() {
        return curso;
    }

    public int getAno() {
        return ano;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getIdade() {
        return idade;
    }

    public String getSexo() {
        return sexo;
    }

    public double getMedia() {
        return media;
    }

    @Override
    public String toString(){
        return "========================================\nCódigo do Estudante: "+
                getCodigo()+"\nNúmero do Estudante: "+getNome()+"\nCurso do Estudante: "+
                getCurso()+"\nAno do Estudante: "+getAno()+"º\nIdade do Estudante: "+getIdade()+
                "\nSexo do Estudante: "+getSexo()+"\nMédia do Estudante: "+getMedia()+
                "\n========================================\n";
    }
}
