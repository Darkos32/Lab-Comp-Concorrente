
public class Atuadores extends Thread {
    private final int id;// identificador do atuador
    private final int log;
    // !Relacionados ao vetor compartilhado
    private Buffer compartilhado;// área compartilhada por todas as threas

    // !Relacioando ao controle da sinalização
    private int sinalVermelhoCount;
    private int sinalAmareloCount;
    private boolean isSinalVermelho;// informa se o atuador está ou não em uma situação de sinal vermelho
    private boolean isSinalAmarelo;// informa se o atuador está ou não em uma situação de sinal amarelo
    // !Relacionados a média das medições
    private float medicoesCount;// número de medições no buffer compartilhado que esse atuador é responsável
    private float somaValores;
    private float media;

    // !Monitor da aplicação
    private LeitorEscritor monitor;

    public Atuadores(int i, LeitorEscritor m, Buffer b, int l) {
        this.id = i;
        this.monitor = m;
        this.compartilhado = b;
        this.sinalAmareloCount = 0;
        this.sinalVermelhoCount = 0;
        this.isSinalAmarelo = false;
        this.isSinalVermelho = false;
        this.medicoesCount = 0;
        this.somaValores = 0;
        this.log = l;
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
            // if (log == 1) {
            //     System.out.print(fim_for);
            // }
            controleDeSinais(tempLeitura.getValor());
            somaValores += tempLeitura.getValor();
            medicoesCount++;

        }
        // if(log ==1)
        //     System.out.println();
    }

    // Calcula a média das temperaturas
    public void setMedia() {
        if(medicoesCount!=0)
            media = somaValores / medicoesCount;
        else {
            media = -1;
        }
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
            System.out.printf("Sensor: %d\nSINAL VERMELHO!!!\nTemperatura media: %f\n",id,media);
        } else if (isSinalAmarelo) {
            System.out.printf("Sensor: %d\nSinal Amarelo!\nTemperatura media: %f\n",id, media);
        } else {
            System.out.printf("Sensor: %d\nLeitura normal\nTemperatura media: %f\n",id, media);
        }
        // System.out.printf("Temperatura media: %d\n", media);
        // System.out.print("Temperatura media: ");
        // System.out.println(media);
    }

    private void reset(){
        isSinalAmarelo = false;
        isSinalVermelho = false;
        sinalAmareloCount = 0;
        sinalVermelhoCount = 0;
        somaValores = 0;
        medicoesCount = 0;
    }

    // atividade realizada pela thread
    @Override
    public void run() {
        while (true) {// realiza o looping indeterminadamente
            try {
                Thread.sleep(2000);// pausa a thread por 2 segundos
                monitor.entrar_leitura();// verifica se o buffer compartilhado está disponível para leitura
                if (log==1) {
                    System.out.println("Atuador começou a ler");
                }
                percorrer_buffer();
                setMedia();//cáculo da média
                if(media!=-1)
                    sinalizar();
                reset();
                if (log == 1) {
                    System.out.println("Atuador terminou a ler");
                }
                monitor.sair_leitura();//finaliza o processo de leitura 
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

    }
}
