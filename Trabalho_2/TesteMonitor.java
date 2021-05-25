package Trabalho_2;

class TesteEscritor extends Thread {
    private LeitorEscritor monitor;

    public TesteEscritor(LeitorEscritor m) {
        monitor = m;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                monitor.entrar_escrita();
        
                monitor.sair_escrita();
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    }
}

class TesteLeitor extends Thread {
    private LeitorEscritor monitor;

    public TesteLeitor(LeitorEscritor m) {
        monitor = m;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                monitor.entrar_leitura();
                //Thread.sleep(1000);
                monitor.sair_leitura();
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    }
}

public class TesteMonitor {
    public static void main(String[] args) {
        Thread threads[] = new Thread[Integer.parseInt(args[0])];
        Thread threads2[] = new Thread[Integer.parseInt(args[0])];
        LeitorEscritor monitor = new LeitorEscritor(1);
        for (int i = 0; i < threads2.length; i++) {// criação das threads
            threads[i] = new TesteLeitor(monitor);
            threads2[i] = new TesteEscritor(monitor);
        }
        for (int i = 0; i < threads2.length; i++) {// inicialização das threads
            threads[i].start();
            threads2[i].start();
        }
    }
}
