package Trabalho_2;

public class LeitorEscritor {
    private int lendo;// número de leitores ativos
    private boolean escrevendo;// se existe ou não alguma thread escrevendo
    private int esperando;
    private final int log;

    public LeitorEscritor(int l) {
        lendo = 0;
        escrevendo = false;
        esperando = 0;
        log = l;
    }

    public synchronized void entrar_leitura() {
        while (escrevendo || esperando > 0) {
            try {
                if (escrevendo || esperando > 0) {
                    if (log == 1)
                        System.out.println("Um leitor tentou ler");
                    this.wait();
                }
            } catch (Exception e) {
                System.out.println("ERRO -- Wait -- Leitura");
            }
        }
        if (log == 1)
            System.out.println("Um leitor começou a ler");
        this.lendo++;

    }

    public synchronized void sair_leitura() {
        if (log == 1)
            System.out.println("Um leitor terminou a leitura");
        this.lendo--;// decrementa o número de leitores
        if (lendo == 0)
            this.notify();// libera as threads bloqueadas
    }

    public synchronized void entrar_escrita() {
        while (escrevendo || lendo > 0) {
            try {
                if (escrevendo || lendo > 0) {
                    if (log == 1)
                        System.out.println("Um escritor tentou escrever");
                    esperando++;
                    this.wait();

                }
            } catch (Exception e) {
                System.out.println("ERRO -- Wait -- Escrita");
            }
        }
        if (log == 1)
            System.out.println("Um escritor começou a escrever");
        esperando--;
        this.escrevendo = true;
    }

    public synchronized void sair_escrita() {
        if (log == 1)
            System.out.println("Um escritor terminou de escrever");
        this.escrevendo = false;
        esperando = 0;
        this.notifyAll();
    }
}
