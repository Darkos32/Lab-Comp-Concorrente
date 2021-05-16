package Trabalho_2;

public class LeitorEscritor {
    private int lendo;// número de leitores ativos
    private boolean escrevendo;// se existe ou não alguma thread escrevendo

    public LeitorEscritor() {
        lendo = 0;
        escrevendo = false;
    }

    public synchronized void entrar_leitura() {
        while (escrevendo) {
            try {
                this.wait();
            } catch (Exception e) {
                System.out.println("ERRO -- Wait -- Leitura");
            }
        }
        this.lendo++;

    }

    public synchronized void sair_leitura() {
        this.lendo--;// decrementa o número de leitores
        this.notifyAll();// libera as threads bloqueadas
    }

    public synchronized void entrar_escrita() {
        while (escrevendo || lendo > 0) {
            try {
                this.wait();
            } catch (Exception e) {
                System.out.println("ERRO -- Wait -- Escrita");
            }
        }
        this.escrevendo = true;
    }

    public synchronized void sair_escrita() {
        this.escrevendo = false;
        this.notifyAll();
    }
}
