package Trabalho_2;

import java.util.Random;

public class Sensores extends Thread {
    private final Integer id;// identificador do sensor
    private int contador;// controle de quantas medições um determinado sensor fez. Também serve como id
                         // para essas medições
    private boolean isWating;
    private Leitura proxLeitura;// a medição realizada pelo sensor
    private Buffer compartilhado;// objeto onde serão armazenadas as medições
    private LeitorEscritor monitor;// controle da escrita no Buffer compartilhado
    private final int log;
    // construtor da classe
    public Sensores(int identificador, Buffer compartilhado, LeitorEscritor monitor, int l) {
        this.id = identificador;
        this.compartilhado = compartilhado;
        this.monitor = monitor;
        this.isWating = false;
        this.contador = 0;
        this.log = l;
    }

    // simula uma tomada de medição da temperatura
    private int getMedicao() {
        Random r = new Random();
        int proxMedicao = r.ints(1, 25, 41).toArray()[0];//gera um número inteiro  aleatório no intervalo [25,40]
        return proxMedicao;
    }

    private void ArmazenarMedicao() {
        int temperatura = this.getMedicao();// simula a temperatura
        if (temperatura > 30) {
            this.proxLeitura = new Leitura(this.contador, this.id, temperatura);
            monitor.entrar_escrita();// verifica se a thread está disponível para escrita
            if (log == 1)
                System.out.println("Sensor começou a escrever");
            compartilhado.escrever(this.proxLeitura, this.id);// escreve a medição no buffer compartilhado
            if (log == 1)
                System.out.println("Sensor terminou de escrever");
            monitor.sair_escrita();// finaliza o processo de escrita
            

        }
    }

    // atividade executada pela thread
    @Override
    public void run() {
        while (true) {// repete o looping indefinidamente
            try {
                Thread.sleep(1000);// pausa a thread por 1 segundo
                this.ArmazenarMedicao();
            } catch (InterruptedException e) {
                System.out.println("Erro -- Thread sleep");
            }
        }
    }
}
