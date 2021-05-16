package Trabalho_2;

public class Atuadores extends Thread {
    private final int id;// identificador do atuador
    // !Relacionados ao vetor compartilhado
    private Buffer compartilhado;// área compartilhada por todas as threas

    // !Relacioando ao controle da sinalização
    private int sinalVermelhoCount;
    private int sinalAmareloCount;
    private boolean isSinalVermelho;// informa se o atuador está ou não em uma situação de sinal vermelho
    private boolean isSinalAmarelo;// informa se o atuador está ou não em uma situação de sinal amarelo
    // !Relacionados a média das medições
    private int medicoesCount;// número de medições no buffer compartilhado que esse atuador é responsável
    private int somaValores;
    private int media;

    // !Monitor da aplicação
    private LeitorEscritor monitor;

    public Atuadores(int i, LeitorEscritor m, Buffer b) {
        this.id = i;
        this.monitor = m;
        this.compartilhado = b;
        this.sinalAmareloCount = 0;
        this.sinalVermelhoCount = 0;
        this.isSinalAmarelo = false;
        this.isSinalVermelho = false;
        this.medicoesCount = 0;
        this.somaValores = 0;
    }

    // Percorre o buffer recolhendo as leituras pertinentes ao atuador
    private void percorrer_buffer() {
        Leitura tempLeitura;// variável temporária
        int tamanhoCompartilhado = compartilhado.getLength();// tamanho do vetor que contém as leituras
        int fim_for = compartilhado.getProxPos();// for termina quando chega na próxima posição a ser escrita no vetor
        for (int i = compartilhado.getUltimaPosEscrita(id); ((i % tamanhoCompartilhado) + tamanhoCompartilhado)
                % tamanhoCompartilhado != fim_for; i--) {
            tempLeitura = compartilhado.ler(((i % tamanhoCompartilhado) + tamanhoCompartilhado) % tamanhoCompartilhado);
            if (tempLeitura == null || tempLeitura.getIdSensor() != this.id) {// ignora leituras que não tenham o sensor
                                                                              // de mesmo id
                continue;
            }
            controleDeSinais(tempLeitura.getValor());
            somaValores += tempLeitura.getValor();
            medicoesCount++;

        }
    }

    // Calcula a média das temperaturas
    public void setMedia() {
        media = somaValores / medicoesCount;
    }

    private void controleDeSinais(int temperatura) {
        if (temperatura > 35) {
            if (medicoesCount < 5) {
                sinalVermelhoCount++;
            }
            sinalAmareloCount++;
        } else {
            sinalVermelhoCount = 0;
        }
        if (sinalVermelhoCount == 5) {
            isSinalVermelho = true;
        }
        if (sinalAmareloCount == 15) {
            isSinalAmarelo = true;
        }
    }

    private void sinalizar() {
        if (isSinalVermelho) {
            System.out.printf("SINAL VERMELHO!!!\nTemperatura media: %d\n", media);
        } else if (isSinalAmarelo) {
            System.out.printf("Sinal Amarelo!\nTemperatura media: %d\n", media);
        } else {
            System.out.printf("Leitura normal\nTemperatura media: %d\n", media);
        }
        // System.out.printf("Temperatura media: %d\n", media);
        // System.out.print("Temperatura media: ");
        // System.out.println(media);
    }

    // atividade realizada pela thread
    @Override
    public void run() {
        while (true) {// realiza o looping indeterminadamente
            try {
                Thread.sleep(2000);// pausa a thread por 2 segundos
                monitor.entrar_leitura();// verifica se o buffer compartilhado está disponível para leitura
                percorrer_buffer();
                setMedia();//cáculo da média
                sinalizar();
                monitor.sair_leitura();//finaliza o processo de leitura 
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

    }
}
