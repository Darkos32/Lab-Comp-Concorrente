package Trabalho_2;

import java.util.Random;

public class Sensores extends Thread {
    private final Integer id;// identificador do sensor
    private int contador;// controle de quantas medições um determinado sensor fez. Também serve como id
                         // para essas medições
    private static Integer proxPos = -1;// posição onde deve ser armazenada a medição feita pelo sensor
    private Leitura proxLeitura;// a medição realizada pelo sensor
    private Buffer compartilhado;// objeto onde serão armazenadas as medições
    private LeitorEscritor monitor;// controle da escrita no Buffer compartilhado

    // construtor da classe
    public Sensores(int identificador, Buffer compartilhado, LeitorEscritor monitor) {
        this.id = identificador;
        this.compartilhado = compartilhado;
        this.monitor = monitor;
        this.contador = 0;
        if (Sensores.proxPos < 0) {
            Sensores.proxPos = 0;
        }
    }

    // simula uma tomada de medição da temperatura
    private int getMedicao() {
        Random r = new Random();
        int proxMedicao = r.ints(0, 25, 41).toArray()[0];
        return proxMedicao;
    }

    private void ArmazenarMedicao() {
        int temperatura = this.getMedicao();
        if (temperatura > 30) {
            this.proxLeitura = new Leitura(this.contador, this.id, temperatura);
            monitor.entrar_escrita();
            compartilhado.escrever(this.proxLeitura);
            Sensores.proxPos = (Sensores.proxPos + 1) % 60;
            monitor.sair_escrita();

        }
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                this.ArmazenarMedicao();
            } catch (Exception e) {
                System.out.println("Erro -- Thread sleep");
            }
        }
    }
}
