package Trabalho_2;

/**
 * Trabalho2
 */
public class Trabalho2 {
    public static void main(String[] args) {
        LeitorEscritor monitor = new LeitorEscritor();
        Buffer compartilhado = new Buffer(60);
        Thread[] sensores, atuadores;
        sensores = new Thread[Integer.parseInt(args[0])];
        atuadores = new Thread[Integer.parseInt(args[0])];

        for (int i = 0; i < atuadores.length; i++) {// criação das threads
            sensores[i] = new Sensores(i, compartilhado, monitor);
            atuadores[i] = new Atuadores(i, monitor, compartilhado);
        }
        for (int i = 0; i < atuadores.length; i++) {// inicialização das threads
            sensores[i].start();
            atuadores[i].start();
        }
    }

}