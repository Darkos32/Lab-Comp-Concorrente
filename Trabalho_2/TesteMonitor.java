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

                monitor.entrar_escrita();
                System.out.println("Thread começou a escrever");
                Thread.sleep(1000);
                System.out.println("Thread terminou a escrever");
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

                monitor.entrar_leitura();
                System.out.println("Thread começou a ler");
                Thread.sleep(2000);
                System.out.println("Thread terminou de ler");
                monitor.sair_leitura();
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    }
}

public class TesteMonitor {
    public static void main(String[] args) {
        Thread threads[] = new Thread[5];
        Thread threads2[] = new Thread[5];
        LeitorEscritor monitor = new LeitorEscritor();
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new TesteEscritor(monitor);
        }
        for (int i = 0; i < threads2.length; i++) {
            threads2[i] = new TesteLeitor(monitor);
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads2) {
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println("ERRO -- JOIN");
                return;
            }
        }
        for (Thread thread : threads2) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println("ERRO -- JOIN");
                return;
            }
        }
    }
}
