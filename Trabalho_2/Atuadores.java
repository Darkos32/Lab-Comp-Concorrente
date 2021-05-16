package Trabalho_2;

public class Atuadores extends Thread {
    private final int id;// identificador do atuador
    // !Relacionados ao vetor compartilhado
    private Buffer compartilhado;// área compartilhada por todas as threas
    
    // !Relacioando ao controle da sinalização
    private int sinalVermelhoCount;
    private int sinalAmareloCount;
    private boolean isSinalVermelho;
    private boolean isSinalAmarelo;
    // !Relacionados a média das medições
    private int medicoesCount;
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
        for (int i = compartilhado.getUltimaPosEscrita(id); i != compartilhado.getProxPos(); i--) {
            tempLeitura = compartilhado.ler((i % compartilhado.getLength()) + compartilhado.getLength());
            if (tempLeitura.getIdSensor() != this.id) {// ignora leituras que não tenham o sensor de mesmo id
                continue;
            }
            controleDeSinais(tempLeitura.getValor());
            somaValores += tempLeitura.getValor();
            medicoesCount++;
            // ultimos[i % ultimos.length] = tempLeitura;

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
            System.out.println("SINAL VERMELHO!!!");
        } else if (isSinalAmarelo) {
            System.out.println("Sinal Amarelo!");
        } else {
            System.out.println("Leitura normal");
        }
        System.out.print("Temperatura media: ");
        System.out.println(media);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(2000);
                monitor.entrar_leitura();
                percorrer_buffer();
                setMedia();
                sinalizar();
                monitor.sair_leitura();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

    }
}
