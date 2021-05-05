/**
 * Vetor
 */
class Vetor {
    private Integer vetor[];// vetor
    private int tam;// tamanho do vetor

    // construtor da classe
    public Vetor(int tamanho) {
        this.tam = tamanho;
        vetor = new Integer[tam];
        this.preencher();
    }

    // Formatação em string do vetor
    @Override
    public String toString() {
        String lista = new String();
        lista = "[";
        for (Integer integer : vetor) {
            lista = lista + " " + integer.toString();
        }
        lista += " ]";
        return lista;
    }

    // preenche a posição do vetor com o valor da própria posição
    private void preencher() {
        for (int i = 0; i < vetor.length; i++) {
            vetor[i] = i;
        }
    }

    // incrementa o valor na posição passada por parâmetro em 1
    public void incrementa(int posicao) {
        this.vetor[posicao]++;
    }

    // retorna o valor da variável tam
    public int getTam() {
        return tam;
    }

    public  void verifica() {
        for (int i = 0; i < vetor.length; i++) {
            if (vetor[i] != i + 1) {
                System.out.println("Houve um problema");
                return;
            }
        }
        System.out.println("Todos os elementos do vetor foram incrementados em um");
    }

}

/**
 * Representa as threads
 */
class Tarefa extends Thread {
    private final int id;// identificador da thread
    private static Vetor alvo = null;// vetor sobre o qual a thread age
    private static int nthreads = 0;// número total de threads

    // construtor da classe
    public Tarefa(int tid, int nthreads, Vetor v) {
        id = tid;
        if (alvo == null) {
            alvo = v;
        }
        if (Tarefa.nthreads == 0) {
            Tarefa.nthreads = nthreads;
        }
    }

    public void run() {
        // começa a partir da posição igual ao identificador da thread e aumenta com
        // passo igual ao número de threads
        for (int i = id; i < Tarefa.alvo.getTam(); i += Tarefa.nthreads) {
            Tarefa.alvo.incrementa(i);
        }
    }
}

/**
 * Lab06
 */
public class Lab06 {
    public static void main(String[] args) {
        Thread threads[] = new Thread[4];
        Vetor v = new Vetor(100);
        // cria as threads
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Tarefa(i, 4, v);
        }
        // começa as threads
        for (Thread thread : threads) {
            thread.start();
        }
        // espera que todas as threads terminem para continuar
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println("ERRO -- JOIN");
                return;
            }
        }
        v.verifica();


    }

}